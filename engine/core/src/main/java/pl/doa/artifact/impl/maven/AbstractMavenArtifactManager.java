package pl.doa.artifact.impl.maven;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Model;
import org.apache.maven.model.ModelBase;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.artifact.IArtifact;
import pl.doa.artifact.MavenDescriptorMatcher;
import pl.doa.artifact.impl.AbstractArtifactManager;
import pl.doa.artifact.impl.maven.exclusion.PropertiesExclusionsReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static pl.doa.utils.JarUtils.findJarEntry;

public abstract class AbstractMavenArtifactManager extends AbstractArtifactManager {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractMavenArtifactManager.class);

    private ThreadLocal<List<Exclusion>> exclusions = new ThreadLocal<List<Exclusion>>();

    public AbstractMavenArtifactManager(IDOA doa) {
        super(doa);
    }

    protected abstract IMavenResolver getMavenResolver();

    @Override
    protected IArtifact resolveArtifact(File artifactFile) throws GeneralDOAException {
        // looking for maven pom.xml
        InputStream mavenDescriptor;
        try {
            mavenDescriptor = findJarEntry(artifactFile, new MavenDescriptorMatcher());
        } catch (IOException e) {
            throw new GeneralDOAException(e);
        }
        if (mavenDescriptor == null) {
            LOG.debug(MessageFormat.format("No maven artifact found for artifact file: {0}, skipping ...",
                    artifactFile.getAbsolutePath()));
            return null;
        }

        // parsing maven descriptor
        Model mavenArtifactModel;
        try {
            MavenXpp3Reader pomReader = new MavenXpp3Reader();
            mavenArtifactModel = pomReader.read(mavenDescriptor);
        } catch (Exception e) {
            throw new GeneralDOAException(e);
        }
        return createArtifact(mavenArtifactModel, artifactFile);
    }

    private IArtifact createArtifact(Model mavenArtifactModel, File artifactFile) throws GeneralDOAException {
        if (artifactFile == null || !artifactFile.exists()) {
            LOG.error("Unable to find artifact file, resolving canceled");
            return null;
        }
        IArtifact newArtifact = createArtifactWithFile(mavenArtifactModel.getGroupId(),
                mavenArtifactModel.getArtifactId(),
                mavenArtifactModel.getVersion(), artifactFile);
        newArtifact.setDescription(mavenArtifactModel.getDescription());

        List<Dependency> notResolved = resolveAndDeployDependencies(mavenArtifactModel, newArtifact);
        if (notResolved != null && !notResolved.isEmpty()) {
            LOG.debug(String.format("Not resolved rependencies for artifact [%s.%s.%s]: ",
                    mavenArtifactModel.getGroupId(), mavenArtifactModel.getArtifactId(),
                    mavenArtifactModel.getVersion()));
            for (Dependency dependency : notResolved) {
                String group = dependency.getGroupId();
                String artifact = dependency.getArtifactId();
                String version = dependency.getVersion();
                LOG.warn(String.format("# %s.%s.%s", group, artifact, (version == null) ? "[newest]" : version));
            }
            throw new GeneralDOAException("Some of dependencies were not resolved!");
        }
        return newArtifact;
    }

    private List<Dependency> resolveAndDeployDependencies(ModelBase mavenArtifactModel, IArtifact artifact)
            throws GeneralDOAException {
        List<Dependency> notResolved = new ArrayList<Dependency>();
        List<Dependency> artifactDependencies = mavenArtifactModel.getDependencies();
        for (Dependency artifactDependency : artifactDependencies) {
            collectExclusions(artifactDependency);

            String groupId = artifactDependency.getGroupId();
            String artifactId = artifactDependency.getArtifactId();
            String artifactVersion = artifactDependency.getVersion();

            // checking exclusions
            if (artifactDependency.isOptional() || isExcluded(artifactDependency)) {
                LOG.debug(MessageFormat
                        .format("Skipping dependency: [{0}.{1}.{2}]", groupId, artifactId, artifactVersion));
                continue;
            }

            // looking for an exiting artifact in repository
            IArtifact dependendArtifact = findExistingArtifact(groupId, artifactId, artifactVersion);
            LOG.debug(MessageFormat.format("Dependend artifact {0}.{1} {2} exists? -> {3}", groupId, artifactId,
                    (artifactVersion == null) ? "[newest]" : artifactVersion,
                    (dependendArtifact != null) ? "YES" : "NO"));
            if (dependendArtifact == null) {
                IMavenResolver dependencyResolver = getMavenResolver();
                if (dependencyResolver == null) {
                    LOG.debug("No dependency resolver set, skipping ...");
                    continue;
                }
                LOG.debug(MessageFormat
                        .format("Processing dependency: {0}.{1}.{2}", groupId, artifactId,
                                (artifactVersion == null) ? "[newest]" : artifactVersion));

                Model dependencyModel = dependencyResolver.resolveArtifactModel(artifactDependency);
                File dependencyFile = dependencyResolver.resolveArtifactFile(artifactDependency);

                dependendArtifact = createArtifact(dependencyModel, dependencyFile);
                if (dependendArtifact != null) {
                    artifact.addDependency(dependendArtifact);
                } else {
                    notResolved.add(artifactDependency);
                }
            } else {
                artifact.addDependency(dependendArtifact);
            }
        }
        return notResolved;
    }

    /**
     * Checks if current dependency is excluded while checking dependencies from some other artifact.
     *
     * @param dependency Dependency from currently analyzed artifact.
     */
    private boolean isExcluded(Dependency dependency) {
        String scope = dependency.getScope();
        boolean runtimeScope = false;
        if (scope == null || scope.trim().length() == 0 || "runtime".equals(scope)) {
            runtimeScope = true;
        }
        List<Exclusion> exclusionList = exclusions.get();
        if (exclusionList == null || exclusionList.isEmpty()) {
            return !runtimeScope;
        }
        for (Exclusion exclusion : exclusionList) {
            String artifactId = exclusion.getArtifactId();
            String groupId = exclusion.getGroupId();
            if (dependency.getArtifactId().equals(artifactId) && dependency.getGroupId().equals(groupId)) {
                return true;
            }
        }
        return !runtimeScope;
    }

    /**
     * Appends new exlusions from currepend dependency
     *
     * @param artifactDependency Dependency from whom exclusions will be collected.
     */
    private void collectExclusions(Dependency artifactDependency) {
        List<Exclusion> exclusionList = exclusions.get();
        if (exclusionList == null || exclusionList.isEmpty()) {
            exclusionList = new ArrayList<Exclusion>();
            // setting default exclusion list
            exclusions.set(new ArrayList<Exclusion>() {{
                addAll(new PropertiesExclusionsReader().readExclusions());
            }});
        }
        List<Exclusion> artifactExclusions = artifactDependency.getExclusions();
        if (artifactExclusions == null || artifactExclusions.isEmpty()) {
            return;
        }
        for (Exclusion artifactExclusion : artifactExclusions) {
            exclusionList.add(artifactExclusion);
        }
    }
}

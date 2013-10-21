package pl.doa.artifact.impl.maven;

import org.apache.ivy.Ivy;
import org.apache.ivy.core.event.EventManager;
import org.apache.ivy.core.module.descriptor.Artifact;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.module.id.ModuleId;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.report.ArtifactDownloadReport;
import org.apache.ivy.core.report.DownloadReport;
import org.apache.ivy.core.report.DownloadStatus;
import org.apache.ivy.core.resolve.DownloadOptions;
import org.apache.ivy.core.resolve.ResolvedModuleRevision;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.plugins.resolver.ChainResolver;
import org.apache.ivy.plugins.resolver.FileSystemResolver;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.artifact.deployment.IArtifactManager;
import pl.doa.artifact.impl.ArtifactDeploymentListener;

import java.io.File;

public class IvyMavenResolver implements IMavenResolver {

    private final static Logger LOG = LoggerFactory.getLogger(IvyMavenResolver.class);

    private final IArtifactManager artifactManager;
    private final String cacheDirectory;
    private ChainResolver chainResolver;
    private Ivy ivy;

    public IvyMavenResolver(IArtifactManager artifactManager, String cacheDirectory) {
        this.artifactManager = artifactManager;
        this.cacheDirectory = cacheDirectory;

        // initializing ivy repository
        initializeRepository();
    }

    private void initializeRepository() {
        // initializing Ivy
        IvySettings settings = new IvySettings();
        settings.setDefaultCache(new File(this.cacheDirectory));
        chainResolver = new ChainResolver();
        chainResolver.setName("chain");
        settings.addResolver(chainResolver);
        settings.setDefaultResolver("chain");
        this.ivy = Ivy.newInstance(settings);
        EventManager eventManager = ivy.getEventManager();
        eventManager.addIvyListener(new ArtifactDeploymentListener());

        // setting default repositories
        FileSystemResolver fsResolver = new FileSystemResolver();
        fsResolver.setEventManager(eventManager);
        fsResolver.setSettings(settings);
        fsResolver.setName("local-maven-repo");
        fsResolver.setM2compatible(true);
        fsResolver.setLocal(true);
        fsResolver.setCheckconsistency(false);
        fsResolver
                .addArtifactPattern(System.getProperty("user.home")
                        + "/.m2/repository/[organisation]/[module]/[revision]/[module]-[revision](-[classifier]).jar");
        fsResolver
                .addIvyPattern(System.getProperty("user.home")
                        + "/.m2/repository/[organisation]/[module]/[revision]/[module]-[revision](-[classifier]).pom");
        chainResolver.add(fsResolver);
    }

    private ResolvedModuleRevision resolveRevision(Dependency dependency) {
        ModuleId moduleId = new ModuleId(dependency.getGroupId(), dependency.getArtifactId());
        ModuleRevisionId revisionId = new ModuleRevisionId(moduleId, dependency.getVersion());
        return ivy.findModule(revisionId);
    }

    @Override
    public Model resolveArtifactModel(Dependency dependency) {
        if (dependency.getVersion() == null) {
            LOG.warn("Dependency version is not provided, checking available revisions...");
            String[] possibleRevisions = ivy.listRevisions(dependency.getGroupId(), dependency.getArtifactId());
            for (int i = possibleRevisions.length - 1; i >= 0; i--) {
                String possibleRevision = possibleRevisions[i];
                LOG.debug(String.format("Trying to resolve dependency with revision: -> %s", possibleRevision));
                dependency.setVersion(possibleRevision);

                Model resolved = resolveArtifactModel(dependency);
                if (resolved != null) {
                    LOG.debug(String.format("Dependency with revision %s resolved", possibleRevision));
                    return resolved;
                } else {
                    LOG.warn(String.format("Dependency with revision %s could not be resolved", possibleRevision));
                }
            }
        }
        ResolvedModuleRevision revision = resolveRevision(dependency);
        if (revision == null) {
            LOG.error("Dependend artifact not found ...");
            return null;
        }
        return new IvyMavenModel(revision.getDescriptor());
    }

    @Override
    public File resolveArtifactFile(Dependency dependency) {
        ResolvedModuleRevision revision = resolveRevision(dependency);
        ModuleDescriptor descriptor = revision.getDescriptor();

        Artifact[] artifacts = descriptor.getAllArtifacts();
        for (Artifact ivyArtifact : artifacts) {
            if ("source".equals(ivyArtifact.getType()) || "javadoc".equals(ivyArtifact.getType())
                    || "wrapper".equals(ivyArtifact.getType())) {
                continue;
            }
            DownloadReport report = revision.getArtifactResolver().download(new Artifact[]{ivyArtifact},
                    new DownloadOptions());

            ArtifactDownloadReport downloadRep =
                    report.getArtifactReport(ivyArtifact);
            DownloadStatus status = downloadRep.getDownloadStatus();
            if (status == DownloadStatus.FAILED) {
                return null;
            }
            File dependencyFile = downloadRep.getLocalFile();
            return dependencyFile;
        }


        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}

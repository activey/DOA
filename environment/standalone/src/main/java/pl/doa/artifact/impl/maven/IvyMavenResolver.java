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
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.artifact.impl.ArtifactDeploymentListener;

import java.io.File;

public class IvyMavenResolver implements IMavenResolver {

    public static final String RESOLVER_NAME = "chain";

    private final static Logger LOG = LoggerFactory.getLogger(IvyMavenResolver.class);

    private final String cacheDirectory;
    private Ivy ivy;

    public IvyMavenResolver(String cacheDirectory) {
        this.cacheDirectory = cacheDirectory;

        // initializing ivy repository
        initializeRepository();
    }

    private void initializeRepository() {
        // initializing Ivy
        IvySettings settings = new IvySettings();
        settings.setDefaultCache(new File(this.cacheDirectory));

        this.ivy = Ivy.newInstance(settings);
        EventManager eventManager = ivy.getEventManager();
        eventManager.addIvyListener(new ArtifactDeploymentListener());
        settings.addResolver(new IvyDependencyResolver(RESOLVER_NAME, eventManager, settings));
        settings.setDefaultResolver(RESOLVER_NAME);

        ivy.bind();
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

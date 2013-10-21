package pl.doa.artifact.impl.maven;

import org.apache.ivy.core.module.descriptor.Artifact;
import org.apache.ivy.core.module.descriptor.DependencyDescriptor;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

public class IvyMavenModel extends Model {

    private static final Logger LOG = LoggerFactory.getLogger(IvyMavenModel.class);

    public IvyMavenModel(ModuleDescriptor descriptor) {
        Artifact[] moduleArtifacts = descriptor.getAllArtifacts();
        if (moduleArtifacts == null || moduleArtifacts.length == 0) {
            LOG.debug("No artifacts found in given module descriptor, skipping ...");
            return;
        }
        Artifact moduleArtifact = moduleArtifacts[0];

        setGroupId(descriptor.getModuleRevisionId().getOrganisation());
        setArtifactId(moduleArtifact.getId().getArtifactId().getName());
        setDescription(descriptor.getDescription());
        ModuleRevisionId revisionId = moduleArtifact.getModuleRevisionId();
        String version = null;
        if (revisionId != null) {
            version = revisionId.getRevision();
        }
        setVersion(version);

        DependencyDescriptor[] moduleDependencies = descriptor.getDependencies();
        for (DependencyDescriptor moduleDependency : moduleDependencies) {
            String groupId = moduleDependency.getDependencyId().getOrganisation();
            String artifactId = moduleDependency.getDependencyId().getName();
            String artifactVersion = moduleDependency.getDependencyRevisionId().getRevision();
            Dependency dependency = new Dependency();
            dependency.setGroupId(groupId);
            dependency.setArtifactId(artifactId);
            dependency.setVersion(artifactVersion);
            dependency.setOptional(moduleArtifact.isMetadata());

            String[] configurations = moduleDependency.getModuleConfigurations();
            String configuration = configurations[configurations.length - 1];
            if ("optional".equals(configuration)) {
                dependency.setOptional(true);
            } else {
                dependency.setScope(configuration);
            }
            addDependency(dependency);
        }
    }
}

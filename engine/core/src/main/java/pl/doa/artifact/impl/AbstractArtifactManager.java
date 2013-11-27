package pl.doa.artifact.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.artifact.ArtifactBuilder;
import pl.doa.artifact.IArtifact;
import pl.doa.artifact.deploy.IDeploymentProcessor;
import pl.doa.artifact.deployment.IArtifactManager;
import pl.doa.artifact.matcher.ArtifactPropertiesMatcher;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.impl.EntityLocationIterator;
import pl.doa.utils.FileUtils;
import pl.doa.utils.PathIterator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static pl.doa.jvm.factory.ObjectFactory.instantiateObjectWithArtifactDependencies;
import static pl.doa.utils.JarUtils.findJarEntry;

public abstract class AbstractArtifactManager implements IArtifactManager {

    private static final String ARTIFACT_PROCESSOR = "deploy.processor";
    private static final String ARTIFACT_ROOT = "deploy.root";
    private static final String ARTIFACT_ROOT_DEFAULT = "/tmp";

    private static final Logger LOG = LoggerFactory.getLogger(AbstractArtifactManager.class);
    private IDOA doa;

    public AbstractArtifactManager(IDOA doa) {
        this.doa = doa;
    }

    protected abstract IArtifact resolveArtifact(File artifactFile)
            throws GeneralDOAException;

    public final IArtifact deployArtifact(File artifactFile) throws GeneralDOAException {

        // creating IArtifact instance from input file
        IArtifact newArtifact = this.resolveArtifact(artifactFile);

        // processing dependencies
        postProcessArtifactDependencies(newArtifact);

        // running deployment processor
        postProcessArtifact(newArtifact);

        return newArtifact;
    }

    private void postProcessArtifactDependencies(IArtifact artifact) {
        List<IArtifact> dependencies = artifact.getDependencies();
        for (IArtifact dependency : dependencies) {
            try {
                // resolving nested dependencies
                postProcessArtifactDependencies(dependency);

                postProcessArtifact(dependency);
            } catch (GeneralDOAException e) {
                LOG.error("Unable to process dependency", e);
            }
        }
    }

    private void postProcessArtifact(IArtifact artifact) throws GeneralDOAException {
        // looking for artifact properties file
        File artifactFile = null;
        try {
            artifactFile = File.createTempFile("deploy-", ".tmp");
            FileUtils.copy(artifact.getArtifactFileStream(), artifactFile);
        } catch (Exception e) {
            throw new GeneralDOAException(e);
        }
        Properties artifactProperties = new Properties();
        try {
            InputStream artifactPropertiesStream = findJarEntry(artifactFile,
                    new ArtifactPropertiesMatcher());
            if (artifactPropertiesStream == null) {
                LOG.debug("No artifact properties found for artifact file: {}", artifact.getArtifactFileName());
            } else {
                LOG.debug("Found artifact properties for artifact file: {}", artifact.getArtifactFileName());
                artifactProperties.load(artifactPropertiesStream);
            }
        } catch (Exception e) {
            throw new GeneralDOAException(e);
        }

        // running deployment processor
        String rootLocation = artifactProperties.getProperty(ARTIFACT_ROOT, ARTIFACT_ROOT_DEFAULT);
        IEntitiesContainer deploymentRoot = getOrCreateDeploymentRoot(doa, rootLocation);
        if (deploymentRoot == null) {
            LOG.error("Unable to find deployment root: [{}]", rootLocation);
            return;
        }
        IDeploymentProcessor processor = instantiateObjectWithArtifactDependencies(doa, artifactProperties.getProperty(
                ARTIFACT_PROCESSOR), artifact);
        if (processor != null) {
            LOG.debug("Running deployment processor: {}, for artifact: [{}.{}]", processor.getClass(),
                    artifact.getGroupId(), artifact.getArtifactId());
            processor.setDoa(doa);
            processor.setArtifact(artifact);
            try {
                processor.process(artifactFile, deploymentRoot);
            } catch (Exception e) {
                throw new GeneralDOAException(e);
            }
        }
    }

    private IEntitiesContainer getOrCreateDeploymentRoot(IDOA doa, String rootLocation) {
        IEntitiesContainer root = (IEntitiesContainer) doa.lookupEntityByLocation(rootLocation);
        if (root != null) {
            return root;
        }
        return createContainers(doa, new EntityLocationIterator(rootLocation));
    }

    private IEntitiesContainer createContainers(IEntitiesContainer rootContainer, PathIterator<String> entryPath) {
        if (entryPath.hasNext()) {
            String part = entryPath.next();
            if (!entryPath.hasNext()) {
                return rootContainer;
            }
            IEntitiesContainer current = rootContainer.getEntityByName(part, IEntitiesContainer.class);
            if (current == null) {
                try {
                    current = doa.createContainer(part, rootContainer);
                } catch (GeneralDOAException e) {
                    LOG.error("", e);
                    return null;
                }
            }
            return createContainers(current, entryPath);
        }
        return rootContainer;
    }

    @Override
    public final IArtifact deployArtifact(String artifactFileName, byte[] artifactDataBytes)
            throws GeneralDOAException {
        return deployArtifact(artifactFileName, new ByteArrayInputStream(artifactDataBytes));
    }

    @Override
    public final IArtifact deployArtifact(String artifactFileName, InputStream artifactDataStream)
            throws GeneralDOAException {
        try {
            // saving stream as temporary file
            File tempFile = FileUtils.copy(artifactDataStream, File.createTempFile(artifactFileName, ".jar"));
            return deployArtifact(tempFile);
        } catch (Exception e) {
            throw new GeneralDOAException(e);
        }
    }

    @Override
    public final void undeployArtifact(IArtifact artifact) throws GeneralDOAException {
        // TODO implement it!
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    protected final ArtifactBuilder newArtifact() throws GeneralDOAException {
        return ArtifactBuilder.newArtifact(doa);
    }

    protected final IArtifact findExistingArtifact(final String groupId, final String artifactId,
            final String artifactVersion) {
        return (IArtifact) doa.lookup(IDOA.ARTIFACTS_CONTAINER,
                new IEntityEvaluator() {

                    @Override
                    public boolean isReturnableEntity(IEntity entity) {
                        if (!(entity instanceof IArtifact)) {
                            return false;
                        }
                        IArtifact artifact = (IArtifact) entity;
                        if (artifact.getGroupId() == null || artifact.getArtifactId() == null) {
                            return false;
                        }
                        String foundGroupId = artifact.getGroupId();
                        String foundArtifactId = artifact.getArtifactId();
                        String foundArtifactVersion = artifact.getVersion();
                        // pobieranie artefaktu w najnowszej wersji
                        if (artifactVersion == null) {
                            return (groupId.equals(foundGroupId) && artifactId.equals(foundArtifactId));
                        }
                        // pobieranie artefaktu w konkretnej wersji
                        return (groupId.equals(foundGroupId) && artifactId.equals(foundArtifactId) &&
                                artifactVersion.equals(
                                        foundArtifactVersion));
                    }

                });
    }
}
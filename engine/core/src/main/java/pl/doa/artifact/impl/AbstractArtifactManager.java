package pl.doa.artifact.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.artifact.ArtifactPropertiesMatcher;
import pl.doa.artifact.IArtifact;
import pl.doa.artifact.deploy.IDeploymentProcessor;
import pl.doa.artifact.deployment.IArtifactManager;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.impl.EntityLocationIterator;
import pl.doa.utils.FileUtils;
import pl.doa.utils.JarUtils;
import pl.doa.utils.PathIterator;

import java.io.*;
import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarFile;

public abstract class AbstractArtifactManager implements IArtifactManager {

    private static final String ARTIFACT_PROCESSOR = "deploy.processor";
    private static final String ARTIFACT_PROCESSOR_DEFAULT = "pl.doa.artifact.deploy.XMLDeploymentProcessor";
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
        resolveDependencies(newArtifact);

        // running deployment processor
        runDeploymentProcessor(newArtifact);

        return newArtifact;
    }

    private void resolveDependencies(IArtifact artifact) {
        List<IArtifact> dependencies = artifact.getDependencies();
        for (IArtifact dependency : dependencies) {
            try {
                // resolving nested dependencies
                resolveDependencies(dependency);

                runDeploymentProcessor(dependency);
            } catch (GeneralDOAException e) {
                LOG.error("Unable to process dependency", e);
            }
        }
    }

    private void runDeploymentProcessor(IArtifact artifact) throws GeneralDOAException {
        // looking for artifact properties file
        File artifactFile = null;
        try {
            artifactFile = File.createTempFile("deploy-", ".tmp");
            JarFile jarFile = new JarFile(FileUtils.copy(artifact.getArtifactFileStream(), artifactFile));
        } catch (Exception e) {
            throw new GeneralDOAException(e);
        }
        Properties artifactProperties = new Properties();
        InputStream artifactPropertiesStream = null;
        try {
            artifactPropertiesStream = JarUtils
                    .findJarEntry(artifactFile, new ArtifactPropertiesMatcher());
            if (artifactPropertiesStream == null) {
                LOG.debug(MessageFormat
                        .format("No artifact properties found for artifact file: {0}", artifact.getArtifactFileName()));
            } else {
                artifactProperties.load(artifactPropertiesStream);
            }
        } catch (Exception e) {
            throw new GeneralDOAException(e);
        }

        // running deployment processor
        String rootLocation = artifactProperties.getProperty(ARTIFACT_ROOT, ARTIFACT_ROOT_DEFAULT);
        IEntitiesContainer deploymentRoot = getDeploymentRoot(doa, rootLocation);
        if (deploymentRoot == null) {
            LOG.error(String.format("Unable to find deployment root: [%s]", rootLocation));
            return;
        }
        IDeploymentProcessor processor = (IDeploymentProcessor) doa.instantiateObject(artifactProperties
                .getProperty(ARTIFACT_PROCESSOR, ARTIFACT_PROCESSOR_DEFAULT));
        if (processor != null) {
            LOG.debug(String.format("Running deployment processor: %s", processor.getClass()));
            processor.setDoa(doa);
            processor.setArtifact(artifact);
            try {
                processor.process(artifactFile, deploymentRoot);
            } catch (Exception e) {
                throw new GeneralDOAException(e);
            }
        }
    }

    private IEntitiesContainer getDeploymentRoot(IDOA doa, String rootLocation) {
        IEntitiesContainer root = (IEntitiesContainer) doa.lookupEntityByLocation(rootLocation);
        if (root != null) {
            return root;
        }
        return createContainers(doa, new EntityLocationIterator(rootLocation));
    }

    private IEntitiesContainer createContainers(
            IEntitiesContainer restoreContainer, PathIterator<String> entryPath) {
        if (entryPath.hasNext()) {
            String part = entryPath.next();
            if (!entryPath.hasNext()) {
                return restoreContainer;
            }
            IEntitiesContainer current =
                    restoreContainer.getEntityByName(part,
                            IEntitiesContainer.class);
            if (current == null) {
                try {
                    current = doa.createContainer(part, restoreContainer);
                } catch (GeneralDOAException e) {
                    LOG.error("", e);
                    return null;
                }
            }
            return createContainers(current, entryPath);
        }
        return restoreContainer;
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
            File tempFile = FileUtils.copy(artifactDataStream, File.createTempFile(artifactFileName, ".deploy"));
            return deployArtifact(tempFile);
        } catch (Exception e) {
            throw new GeneralDOAException(e);
        }
    }

    @Override
    public final void undeployArtifact(IArtifact artifact) throws GeneralDOAException {
        // TODO implement it!
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
                            return (groupId.equals(foundGroupId) && artifactId.equals(
                                    foundArtifactId));
                        }
                        // pobieranie artefaktu w konkretnej wersji
                        return (groupId.equals(foundGroupId)
                                && artifactId.equals(foundArtifactId) && artifactVersion.equals(
                                foundArtifactVersion));
                    }

                });
    }

    protected final IArtifact createArtifact(String groupId, String artifactId,
            String artifactVersion) throws GeneralDOAException {
        IArtifact artifact = doa.createArtifact(String.format("%s.%s.%s", groupId, artifactId, artifactVersion));
        artifact.setGroupId(groupId);
        artifact.setArtifactId(artifactId);
        artifact.setVersion(artifactVersion);
        artifact.store(IDOA.ARTIFACTS_CONTAINER);
        return artifact;
    }

    protected final IArtifact createArtifact(String groupId, String artifactId,
            String artifactVersion, File artifactFile) throws GeneralDOAException {
        IArtifact artifact = createArtifact(groupId, artifactId, artifactVersion);
        artifact.setArtifactFileName(artifactFile.getName());
        try {
            artifact.setArtifactResourceStream(new FileInputStream(
                    artifactFile.getAbsolutePath()), artifactFile.length());
        } catch (Exception e) {
            throw new GeneralDOAException(e);
        }
        return artifact;
    }
}
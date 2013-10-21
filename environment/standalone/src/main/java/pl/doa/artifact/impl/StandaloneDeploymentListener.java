package pl.doa.artifact.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.IDOA;
import pl.doa.artifact.IDirectoryListener;
import pl.doa.artifact.IArtifact;
import pl.doa.entity.ITransactionCallback;
import pl.doa.utils.FileUtils;

import java.io.File;
import java.text.MessageFormat;

public class StandaloneDeploymentListener implements IDirectoryListener {

    private final static Logger LOG = LoggerFactory.getLogger(StandaloneDeploymentListener.class);

    private IDOA doa;

    public StandaloneDeploymentListener(IDOA doa) {
        this.doa = doa;
    }

    @Override
    public void directoryContentsChanged(File directory, File[] changedFiles, TYPE type) {
        for (final File file : changedFiles)
            switch (type) {
                case ADD: {
                    LOG.debug(String.format("Deploying artifact from file: %s", file.getAbsolutePath()));
                    String fileExt = FileUtils.getExtension(file);
                    if (!"jar".equals(fileExt)) {
                        LOG.warn(String.format("Unable to process deployment for file: %s", file.getAbsolutePath()));
                        continue;
                    }
                    try {
                        doa.doInTransaction(new ITransactionCallback<Object>() {

                            @Override
                            public Object performOperation()
                                    throws Exception {
                                IArtifact deployedArtifact =
                                        StandaloneDeploymentListener.this.artifactDeployed(file.getName(),
                                                file);
                                if (deployedArtifact == null) {
                                    LOG.warn("Could not create IArtifact object!");
                                    return null;
                                }

                                LOG.debug(MessageFormat.format("Deployment of artifact [{0}.{1}] complete!",
                                        deployedArtifact.getGroupId(), deployedArtifact.getArtifactId()));
                                return null;
                            }
                        });

                    } catch (Exception ex) {
                        LOG.error("", ex);
                        return;
                    }

                    break;
                }
                case REMOVE: {
                    // TODO implement undeployment
                    break;
                }
                default:
                    break;
            }
    }

    /**
     * Callback method, called after some new artifact file appeared in hot-deploy directory
     *
     * @param name
     * @param artifactFile
     */
    protected IArtifact artifactDeployed(String name, File artifactFile) throws Exception {
        return null;
    }
}

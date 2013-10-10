/*******************************************************************************
 * Copyright 2011 Inhibi Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are
 * permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright 
 * notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright 
 * notice, this list
 *        of conditions and the following disclaimer in the documentation 
 * and/or other materials
 *        provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY INHIBI LTD ``AS IS'' AND ANY 
 * EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * INHIBI LTD OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation 
 * are those of the authors and should not be interpreted as representing 
 * official policies, either expressed or implied, of Inhibi Ltd.
 *
 * Contributors:
 *    Inhibi Ltd - initial API and implementation
 *******************************************************************************/
/**
 *
 */
package pl.doa.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.artifact.IArtifact;
import pl.doa.artifact.IArtifact.Type;
import pl.doa.artifact.deployment.IArtifactManager;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.entity.event.IEntityEventListener;
import pl.doa.entity.startable.IStartableEntity;
import pl.doa.entity.startable.IStartableEntityLogic;
import pl.doa.entity.startable.IStartableEntityManager;
import pl.doa.resource.IStaticResource;
import pl.doa.resource.IStaticResourceStorage;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinitionLogic;
import pl.doa.service.IServicesManager;
import pl.doa.thread.IThreadManager;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * @author activey
 */
public abstract class AbstractBootstrapDOA extends AbstractDOA {

    private final static Logger log = LoggerFactory
            .getLogger(AbstractBootstrapDOA.class);

    private IServicesManager servicesManager;
    private IArtifactManager artifactManager;
    private IStaticResourceStorage resourceStorage;
    private IStartableEntityManager startableManager;
    private IThreadManager threadManager;
    
    public AbstractBootstrapDOA() {
        super(null);
    }

    @Override
    public final long getIdImpl() {
        return 0;
    }

    @Override
    public final IEntitiesContainer getContainerImpl() {
        return null;
    }

    @Override
    public final String getLocationImpl() {
        return "/";
    }

    @Override
    public final boolean removeImpl(boolean forceRemoveContents) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean isStoredImpl() {
        return true;
    }

    @Override
    public final IEntity storeImpl(String location) throws GeneralDOAException {
        throw new UnsupportedOperationException();
    }

    public final void setContainerImpl(IEntitiesContainer container) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean hasEventListenersImpl() {
        return false;
    }

    @Override
    public final List<IEntityEventListener> getEventListenersImpl() {
        return null;
    }

    @Override
    public final boolean isPublicImpl() {
        return false;
    }

    @Override
    public final IArtifact getArtifactImpl() {
        return null;
    }

    @Override
    public final boolean isAutostartImpl() {
        return true;
    }

    @Override
    public final void setAutostartImpl(boolean autostart) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final Date getLastModifiedImpl() {
        return new Date();
    }

    @Override
    public final Date getCreatedImpl() {
        return new Date();
    }

    @Override
    public final IArtifact deployArtifact(String artifactFileName,
                                          byte[] artifactData, Type artifactType) throws GeneralDOAException {
        if (artifactManager == null) {
            log.debug("Manager is not ready yet ...");
            return null;
        }
        return artifactManager.deployArtifact(artifactFileName, artifactData,
                artifactType);
    }

    @Override
    public final IArtifact deployArtifact(String artifactFileName,
                                          InputStream artifactData, Type artifactType)
            throws GeneralDOAException {
        if (artifactManager == null) {
            log.debug("Manager is not ready yet ...");
            return null;
        }
        return artifactManager.deployArtifact(artifactFileName, artifactData,
                artifactType);
    }

    @Override
    public final void undeployArtifact(IArtifact artifact)
            throws GeneralDOAException {
        if (artifactManager == null) {
            log.debug("Manager is not ready yet ...");
            return;
        }
        artifactManager.undeployArtifact(artifact);
    }

    @Override
    public final void executeService(IRunningService runningService,
                                     boolean asynchronous) throws GeneralDOAException {
        if (servicesManager == null) {
            log.debug("Manager is not ready yet ...");
            return;
        }
        servicesManager.executeService(runningService, asynchronous);
    }

    @Override
    public final IServiceDefinitionLogic getRunning(
            IRunningService runningService) {
        if (servicesManager == null) {
            log.debug("Manager is not ready yet ...");
            return null;
        }
        return servicesManager.getRunning(runningService);
    }

    @Override
    public final IServiceDefinitionLogic getRunning(long runningServiceUUID) {
        if (servicesManager == null) {
            log.debug("Manager is not ready yet ...");
            return null;
        }
        return servicesManager.getRunning(runningServiceUUID);
    }

    @Override
    public final IStartableEntityLogic getRunning(
            IStartableEntity startableEntity) {
        if (startableManager == null) {
            log.debug("Manager is not ready yet ...");
            return null;
        }
        return startableManager.getRunning(startableEntity);
    }

    @Override
    public final boolean isRunning(IStartableEntity startableEntity) {
        if (startableManager == null) {
            log.debug("Manager is not ready yet ...");
            return false;
        }
        return startableManager.isRunning(startableEntity);
    }

    @Override
    public final IStartableEntityLogic startup(IStartableEntity startableEntity)
            throws GeneralDOAException {
        return startableManager.startup(startableEntity);
    }

    @Override
    public final void shutdown(IStartableEntity startableEntity)
            throws GeneralDOAException {
        if (startableManager == null) {
            log.debug("Manager is not ready yet ...");
            return;
        }
        startableManager.shutdown(startableEntity);
    }

    public final long storeOrUpdate(IStaticResource resource,
                                    InputStream dataStream) throws Exception {
        if (resourceStorage == null) {
            log.debug("Storage is not ready yet ...");
            return -1;
        }
        return resourceStorage.storeOrUpdate(resource, dataStream);
    }

    public final InputStream retrieve(IStaticResource resource)
            throws Exception {
        if (resourceStorage == null) {
            log.debug("Storage is not ready yet ...");
            return null;
        }
        return resourceStorage.retrieve(resource);
    }

    @Override
    public final boolean removeFileStream(IStaticResource resource)
            throws Exception {
        if (resourceStorage == null) {
            log.debug("Storage is not ready yet ...");
            return false;
        }
        return resourceStorage.remove(resource);
    }

    @Override
    protected final IEntity getAncestorImpl() {
        return null;
    }

    @Override
    public final void executeThread(Runnable runnable) {
        if (threadManager == null) {
            log.debug("Manager is not ready yet ...");
            return;
        }
        threadManager.execute(runnable);
    }

    public void setThreadManager(IThreadManager threadManager) {
        this.threadManager = threadManager;
    }

    public void setStartableManager(IStartableEntityManager startableManager) {
        this.startableManager = startableManager;
    }

    public void setResourceStorage(IStaticResourceStorage resourceStorage) {
        this.resourceStorage = resourceStorage;
    }

    public void setArtifactManager(IArtifactManager artifactManager) {
        this.artifactManager = artifactManager;
    }

    public void setServicesManager(IServicesManager servicesManager) {
        this.servicesManager = servicesManager;
    }
}

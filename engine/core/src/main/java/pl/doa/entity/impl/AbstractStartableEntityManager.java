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
package pl.doa.entity.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.entity.startable.IStartableEntity;
import pl.doa.entity.startable.IStartableEntityLogic;
import pl.doa.entity.startable.IStartableEntityManager;

import static pl.doa.jvm.factory.ObjectFactory.instantiateObjectWithArtifactDependencies;

/**
 * @author activey
 */
public abstract class AbstractStartableEntityManager
        implements
        IStartableEntityManager {

    private final static Logger log = LoggerFactory
            .getLogger(AbstractStartableEntityManager.class);
    private final IDOA doa;

    public AbstractStartableEntityManager(IDOA doa) {
        this.doa = doa;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * pl.doa.entity.startable.IStartableEntityManager#startup(pl.doa.entity
     * .startable.IStartableEntity)
     */
    @Override
    public final IStartableEntityLogic startup(
            final IStartableEntity startableEntity) throws GeneralDOAException {
        long entityId = startableEntity.getId();
        if (entityId == -1) {
            throw new GeneralDOAException("entity isn't stored yet!");
        }

        IStartableEntityLogic entityLogic = getRunning(startableEntity);
        if (entityLogic == null) {
            String logicClass = startableEntity.getLogicClass();
            if (logicClass == null) {
                throw new GeneralDOAException("Logic class name can't be null!");
            }
            try {
                entityLogic = instantiateObjectWithArtifactDependencies(doa, logicClass, startableEntity.getArtifact());
                entityLogic.setDoa(doa);
            } catch (Exception e) {
                throw new GeneralDOAException(e);
            }
            registerRunning(entityId, entityLogic);
        }
        if (entityLogic.isStartedUp()) {
            throw new GeneralDOAException("Entity is already started up!");
        }
        entityLogic.setStartableEntity(startableEntity);
        entityLogic.setDoa(doa);
        entityLogic.startup();
        return entityLogic;
    }

    protected abstract void registerRunning(long entityId,
            IStartableEntityLogic instance) throws GeneralDOAException;

    /*
     * (non-Javadoc)
     *
     * @see
     * pl.doa.entity.startable.IStartableEntityManager#shutdown(pl.doa.entity
     * .startable.IStartableEntity)
     */
    @Override
    public final void shutdown(IStartableEntity startableEntity)
            throws GeneralDOAException {
        long entityId = (long) startableEntity.getId();
        if (entityId == -1) {
            throw new GeneralDOAException("entity isn't stored yet!");
        }
        IStartableEntityLogic entityLogic = getRunning(startableEntity);
        if (entityLogic == null) {
            log.debug("entity isn't started, skipping ...");
            return;
        }
        if (!entityLogic.isStartedUp()) {
            throw new GeneralDOAException("entity isn't started, skipping ...");
        }
        entityLogic.shutdown();
        unregisterRunning(entityId);
    }

    protected abstract void unregisterRunning(long entityId)
            throws GeneralDOAException;

    @Override
    public final boolean isRunning(IStartableEntity startableEntity) {
        IStartableEntityLogic running = getRunning(startableEntity);
        if (running == null) {
            return false;
        }
        return running.isStartedUp();
    }
}

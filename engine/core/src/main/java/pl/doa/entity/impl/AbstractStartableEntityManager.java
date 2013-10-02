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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.artifact.IArtifact;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.entity.startable.IStartableEntity;
import pl.doa.entity.startable.IStartableEntityLogic;
import pl.doa.entity.startable.IStartableEntityManager;

/**
 * @author activey
 */
public abstract class AbstractStartableEntityManager implements
        IStartableEntityManager {

    private final static Logger log = LoggerFactory
            .getLogger(AbstractStartableEntityManager.class);

    /*
     * (non-Javadoc)
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
            entityLogic = null;
            if (logicClass == null) {
                throw new GeneralDOAException("Logic class name can't be null!");
            }
            try {
                final List<IArtifact> classpath = new ArrayList<IArtifact>();

                analyzeDependencies(startableEntity, classpath);

                Object loadedInstance =
                        getDoa().instantiateObject(logicClass, true,
                                new IEntityEvaluator() {

                                    @Override
                                    public boolean isReturnableEntity(
                                            IEntity currentEntity) {
                                        if (!(currentEntity instanceof IArtifact)) {
                                            return false;
                                        }
                                        IArtifact dependency =
                                                (IArtifact) currentEntity;
                                        if (dependency.equals(startableEntity
                                                .getArtifact())) {
                                            return true;
                                        }
                                        boolean oneOf = false;
                                        //System.out.println("-----------------------");
                                        for (IArtifact dependend : classpath) {
                                            /*System.out.println(" dependend > "
                                                    + dependend);
											System.out.println(" dependency > "
													+  dependency);*/
                                            // sprawdzanie, czy artefakt jest jedna z zaleznosci obiektu, ktory uruchamiamy
                                            if (dependend.equals(dependency)) {
                                                oneOf = true;
                                            }
                                        }
                                        //System.out.println("-----------------------");
                                        if (!oneOf) {
                                            return false;
                                        }
                                        log.debug(MessageFormat
                                                .format("Using artifact dependency: [{0}.{1}.{2}]",
                                                        dependency.getGroupId(),
                                                        dependency
                                                                .getArtifactId(),
                                                        dependency.getVersion()));
                                        return true;
                                    }
                                });
                if (loadedInstance == null) {
                    throw new GeneralDOAException(
                            "Unable to instantiate startable entity logic class: [{0}]",
                            logicClass);
                }
                try {
                    entityLogic = (IStartableEntityLogic) loadedInstance;
                } catch (ClassCastException e) {
                    throw new GeneralDOAException(
                            "Wrong startable entity logic class type!");
                }
                entityLogic = (IStartableEntityLogic) loadedInstance;
                entityLogic.setDoa(getDoa());
            } catch (Exception e) {
                throw new GeneralDOAException(e);
            }
            registerRunning(entityId, entityLogic);
        }
        if (entityLogic.isStartedUp()) {
            throw new GeneralDOAException("Entity is already started up!");
        }
        entityLogic.setStartableEntity(startableEntity);
        entityLogic.setDoa(getDoa());
        entityLogic.startup();
        return entityLogic;
    }

    private void analyzeDependencies(IStartableEntity startable,
                                     List<IArtifact> includeArtifacts) {
        IArtifact artifact = startable.getArtifact();
        if (artifact == null) {
            return;
        }
        log.debug(MessageFormat.format(
                "Analyzing dependencies for artifact: [{0}.{1}.{2}]",
                artifact.getGroupId(), artifact.getArtifactId(),
                artifact.getVersion()));
        collectDependencies(includeArtifacts, artifact);
    }

    private void collectDependencies(List<IArtifact> dependencyList,
                                     IArtifact forArtifact) {
        List<IArtifact> dependencies = forArtifact.getDependencies();
        for (IArtifact dependency : dependencies) {
            dependencyList.add(dependency);
            collectDependencies(dependencyList, dependency);
        }
    }

    protected abstract void registerRunning(long entityId,
                                            IStartableEntityLogic instance) throws GeneralDOAException;

    /*
     * (non-Javadoc)
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

    public abstract IDOA getDoa();

    public abstract void setDoa(IDOA doa);

}
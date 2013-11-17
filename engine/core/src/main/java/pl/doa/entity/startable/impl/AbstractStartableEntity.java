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
package pl.doa.entity.startable.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.entity.impl.AbstractEntity;
import pl.doa.entity.startable.IStartableEntity;
import pl.doa.entity.startable.IStartableEntityLogic;
import pl.doa.jvm.factory.EntityArtifactDependenciesEvaluator;

import java.text.MessageFormat;

import static pl.doa.jvm.factory.ObjectFactory.instantiateObject;

/**
 * @author activey
 */
public abstract class AbstractStartableEntity extends AbstractEntity implements
        IStartableEntity {

    private final static Logger log = LoggerFactory
            .getLogger(AbstractStartableEntity.class);

    public AbstractStartableEntity(IDOA doa) {
        super(doa);
    }

    /*
     * (non-Javadoc)
     * @see pl.doa.entity.IStartableEntity#createLogicInstance()
     */
    @Override
    public final IStartableEntityLogic getRunningInstance() throws GeneralDOAException {
        IStartableEntityLogic runningLogic = getDoa().getRunning(this);
        if (runningLogic != null && runningLogic.isStartedUp()) {
            return runningLogic;
        }
        log.debug(MessageFormat.format("Unable to find running instance of [{0}], creating a new one", getLocation()));
        String logicClass = getLogicClass();
        if (logicClass == null) {
            throw new GeneralDOAException("Logic class name can't be null!");
        }
        try {
            return instantiateObject(getDoa(), logicClass, new EntityArtifactDependenciesEvaluator(this));
        } catch (Exception e) {
            throw new GeneralDOAException(e);
        }
    }

    @Override
    public final boolean isStartedUp() {
        IStartableEntityLogic logic = getDoa().getRunning(this);
        if (logic == null) {
            return false;
        }
        return logic.isStartedUp();
    }

    protected abstract boolean isAutostartImpl();

    public final boolean isAutostart() {
        return isAutostartImpl();
    }

    protected abstract void setAutostartImpl(boolean autostart);

    public final void setAutostart(boolean autostart) {
        setAutostartImpl(autostart);
    }

    public void startup() throws GeneralDOAException {
        if (this instanceof IDOA) {
            IDOA doa = (IDOA) this;
            doa.startup(this);
            return;
        }
        getDoa().startup(this);
    }

    public final void shutdown() throws GeneralDOAException {
        if (this instanceof IDOA) {
            IDOA doa = (IDOA) this;
            doa.shutdown(this);
            return;
        }
        getDoa().shutdown(this);
    }

    protected abstract String getLogicClassImpl();

    public final String getLogicClass() {
        return getLogicClassImpl();
    }

    protected abstract void setLogicClassImpl(String logicClass);

    public final void setLogicClass(String logicClass) {
        setLogicClassImpl(logicClass);
    }
}

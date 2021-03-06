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
package pl.doa;

import java.io.Serializable;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import pl.doa.entity.IEntity;
import pl.doa.entity.impl.neo.startable.NeoStartableEntity;
import pl.doa.entity.startable.IStartableEntity;
import pl.doa.entity.startable.IStartableEntityLogic;

/**
 * @author activey
 */

public class NeoStartableEntityDelegator extends NeoEntityDelegator implements
        IStartableEntity, Serializable {

    private NeoStartableEntity delegator = null;

    public NeoStartableEntityDelegator(IDOA doa, GraphDatabaseService neo,
                                       String className, IEntity ancestor) {
        super(doa, neo, className, ancestor);
        this.delegator = new NeoStartableEntity(doa, this);
    }

    public NeoStartableEntityDelegator(IDOA doa, GraphDatabaseService neo,
                                       String className) {
        super(doa, neo, className);
        this.delegator = new NeoStartableEntity(doa, this);
    }

    public NeoStartableEntityDelegator(IDOA doa, Node node) {
        super(doa, node);
        this.delegator = new NeoStartableEntity(doa, node);
    }

    /*
     * (non-Javadoc)
     * @see pl.doa.entity.IStartableEntity#getLogicClass()
     */
    @Override
    public final String getLogicClass() {
        return delegator.getLogicClass();
    }

    /*
     * (non-Javadoc)
     * @see pl.doa.entity.IStartableEntity#setLogicClass(java.lang.String)
     */
    @Override
    public final void setLogicClass(String logicClass) {
        delegator.setLogicClass(logicClass);
    }

    @Override
    public void setAutostart(boolean autostart) {
        delegator.setAutostart(new Boolean(autostart));
    }

    @Override
    public IStartableEntityLogic getRunningInstance()
            throws GeneralDOAException {
        return delegator.getRunningInstance();
    }

    @Override
    public boolean isAutostart() {
        return (Boolean) delegator.isAutostart();
    }

    @Override
    public boolean isStartedUp() {
        return delegator.isStartedUp();
    }

    @Override
    public void shutdown() throws GeneralDOAException {
        delegator.shutdown();
    }

    @Override
    public void startup() throws GeneralDOAException {
        delegator.startup();
    }
}

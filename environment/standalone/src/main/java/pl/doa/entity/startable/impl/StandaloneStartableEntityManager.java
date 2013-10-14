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

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.cache.ILiveObjectCache;
import pl.doa.cache.impl.SimpleObjectCache;
import pl.doa.entity.impl.AbstractStartableEntityManager;
import pl.doa.entity.startable.IStartableEntity;
import pl.doa.entity.startable.IStartableEntityLogic;

/**
 * @author activey
 */
public class StandaloneStartableEntityManager extends
        AbstractStartableEntityManager {

    private final ILiveObjectCache<IStartableEntityLogic> cache;

    public StandaloneStartableEntityManager(IDOA doa) {
        super(doa);
        this.cache = new SimpleObjectCache<IStartableEntityLogic>();
    }

    public IStartableEntityLogic getRunning(IStartableEntity startableEntity) {
        long entityId = startableEntity.getId();
        if (entityId == -1) {
            return null;
        }
        if (!cache.hasObject(entityId)) {
            return null;
        }
        return cache.getObject(entityId);
    }

    @Override
    protected void registerRunning(long entityId, IStartableEntityLogic instance)
            throws GeneralDOAException {
        cache.setObject(instance, entityId);
    }

    @Override
    protected void unregisterRunning(long entityId) throws GeneralDOAException {
        cache.removeObject(entityId);
    }

}
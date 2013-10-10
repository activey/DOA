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
package pl.doa.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.IDOA;
import pl.doa.cache.ILiveObjectCache;
import pl.doa.cache.impl.SimpleObjectCache;
import pl.doa.service.AbstractServiceDefinitionLogic;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinitionLogic;
import pl.doa.service.IServicesManager;

import java.text.MessageFormat;

/**
 * @author activey
 */
public class StandaloneServicesManager extends AbstractServicesManager implements IServicesManager {

    private static final Logger log = LoggerFactory
            .getLogger(StandaloneServicesManager.class);
    private final ILiveObjectCache<IServiceDefinitionLogic> cache;

    static {
        // make sure that the task class is loaded by the classloader that
        // loads the task runner, otherwise it might be loaded several times
        // and result in linkage error exceptions
        @SuppressWarnings("unused")
        Class t = AbstractServiceDefinitionLogic.class;
    }

    public StandaloneServicesManager(IDOA doa) {
        super(doa);
        this.cache = new SimpleObjectCache<IServiceDefinitionLogic>();
    }

    /*
     * (non-Javadoc)
     * @see
     * pl.doa.service.IServicesManager#getRunning(pl.doa.service.IRunningService
     * )
     */
    @Override
    public IServiceDefinitionLogic getRunning(IRunningService runningService) {
        long entityUuid = runningService.getId();
        log.debug(MessageFormat.format(
                "Checking if service is running for id = {0}", entityUuid));
        IServiceDefinitionLogic running = cache.getObject(entityUuid);
        if (running != null) {
            log.debug(MessageFormat.format("Found running instance: {0}",
                    runningService.getId()));
        }
        return running;
    }

    /*
     * (non-Javadoc)
     * @see pl.doa.service.IServicesManager#getRunning(java.lang.String)
     */
    @Override
    public IServiceDefinitionLogic getRunning(long runningServiceUUID) {
        return (IServiceDefinitionLogic) cache.getObject(runningServiceUUID);
    }
}

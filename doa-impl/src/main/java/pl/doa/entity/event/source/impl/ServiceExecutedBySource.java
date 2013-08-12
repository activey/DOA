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
package pl.doa.entity.event.source.impl;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.agent.IAgent;
import pl.doa.entity.IEntity;
import pl.doa.entity.ITransactionCallback;
import pl.doa.entity.event.EntityEventType;
import pl.doa.entity.event.IEntityEventReceiver;
import pl.doa.entity.event.impl.ServiceExecutedByEvent;
import pl.doa.entity.event.source.AsynchronousEventSource;
import pl.doa.service.IServiceDefinition;

/**
 * @author activey
 */
public class ServiceExecutedBySource implements
        AsynchronousEventSource<ServiceExecutedByEvent>, Serializable {

    private static final long serialVersionUID = 1L;

    private final static Logger log = LoggerFactory
            .getLogger(ServiceExecutedBySource.class);
    private String serviceLocation;
    private IServiceDefinition serviceToRun;
    private IAgent agent;

    public ServiceExecutedBySource(String serviceLocation, IAgent agent) {
        this.serviceLocation = serviceLocation;
        this.agent = agent;
    }

    public ServiceExecutedBySource(IServiceDefinition serviceToRun, IAgent agent) {
        this.serviceToRun = serviceToRun;
        this.agent = agent;
    }

    public String getServiceLocation() {
        return serviceLocation;
    }

    @Override
    public void waitForEventAsync(final IDOA doa,
                                  final IEntityEventReceiver eventReceiver)
            throws GeneralDOAException {
        doa.doInTransaction(new ITransactionCallback<IEntity>() {

            @Override
            public IEntity performOperation() throws Exception {
                return doa.createEntityEventListener(serviceToRun,
                        eventReceiver, EntityEventType.SERVICE_EXECUTED_BY).store(
                        "/tmp");

            }

        });
    }

}

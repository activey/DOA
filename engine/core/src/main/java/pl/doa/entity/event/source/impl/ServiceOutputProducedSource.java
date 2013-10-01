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
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.agent.IAgent;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.entity.IEntity;
import pl.doa.entity.ITransactionCallback;
import pl.doa.entity.event.EntityEventType;
import pl.doa.entity.event.IEntityEventReceiver;
import pl.doa.entity.event.impl.ServiceExecutedEvent;
import pl.doa.entity.event.source.AsynchronousEventSource;
import pl.doa.entity.event.source.SynchronousEventSource;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;

/**
 * @author activey
 */
public class ServiceOutputProducedSource implements
        AsynchronousEventSource<ServiceExecutedEvent>,
        SynchronousEventSource<ServiceExecutedEvent>, Serializable {

    private static final long serialVersionUID = 1L;
    private final static Logger log = LoggerFactory
            .getLogger(ServiceOutputProducedSource.class);
    private String serviceLocation;
    private IDocument input;
    private IAgent agent;

    /**
     * TODO !!!
     * <p/>
     * zaimplementowac tworzenie sluchacza !!!!
     *
     * @param serviceLocation
     * @param agent
     */
    public ServiceOutputProducedSource(String serviceLocation, IAgent agent) {
        this.serviceLocation = serviceLocation;
        this.agent = agent;
    }

    public ServiceOutputProducedSource(String serviceLocation, IDocument input) {
        this.serviceLocation = serviceLocation;
        this.input = input;
    }

    public ServiceOutputProducedSource(IServiceDefinition serviceToRun,
                                       IDocument input) {
        this.serviceLocation = serviceToRun.getLocation();
        this.input = input;
    }

    public ServiceOutputProducedSource(IServiceDefinition serviceToRun) {
        this.serviceLocation = serviceToRun.getLocation();
    }

    public ServiceOutputProducedSource(String serviceLocation, IDocument input,
                                       IAgent agent) {
        this.serviceLocation = serviceLocation;
        this.input = input;
        this.agent = agent;
    }

    public ServiceOutputProducedSource(IServiceDefinition serviceToRun,
                                       IDocument input, IAgent agent) {
        this.serviceLocation = serviceToRun.getLocation();
        this.input = input;
        this.agent = agent;
    }

    private IRunningService waitFor(final IDOA doa, boolean asynchronous,
                                    final IEntityEventReceiver eventReceiver)
            throws GeneralDOAException {
        IServiceDefinition serviceToRun = null;
        /*
		 * wyszukiwanie uslugi
		 */
        if (serviceLocation != null) {
            IEntity entity = doa.lookupEntityByLocation(serviceLocation);
            if (entity == null || !(entity instanceof IServiceDefinition)) {
                throw new GeneralDOAException(MessageFormat.format(
                        "Unable to find service under location: {0}",
                        serviceLocation));
            }
            serviceToRun = (IServiceDefinition) entity;
        }
        if (serviceToRun == null) {
            throw new GeneralDOAException(MessageFormat.format(
                    "Unable to find service under location: {0}",
                    serviceLocation));
        }

        IAgent runAsAgent = doa.getAgent();
        if (this.agent != null) {
            runAsAgent = this.agent;
        }
        if (input == null && !allowNullInput()) {
            IDocumentDefinition inputDefinition =
                    serviceToRun.getInputDefinition();
            if (inputDefinition != null) {
                input = inputDefinition.createDocumentInstance();
            }
        }
        if (input != null) {
            onBeforeRun(serviceToRun, input);
        }
        IRunningService runningService =
                serviceToRun.executeService(input, runAsAgent, asynchronous);

        // tworzenie sluchacza uruchomionej uslugi
        if (asynchronous) {
            doa.doInTransaction(new ITransactionCallback<IEntity>() {

                private IRunningService runningService;

                @Override
                public IEntity performOperation() throws Exception {
                    return doa.createEntityEventListener(runningService,
                            eventReceiver, EntityEventType.SERVICE_EXECUTED)
                            .store("/tmp");

                }

                public ITransactionCallback<IEntity> setService(
                        IRunningService runningService) {
                    this.runningService = runningService;
                    return this;
                }
            }.setService(runningService));
        }

        return runningService;
    }

    protected boolean allowNullInput() {
        return true;
    }

    protected void onBeforeRun(IServiceDefinition serviceToRun, IDocument input)
            throws GeneralDOAException {
    }

    public String getServiceLocation() {
        return serviceLocation;
    }

    public void waitForEventAsync(final IDOA doa,
                                  final IEntityEventReceiver eventReceiver)
            throws GeneralDOAException {
		/*doa.doInTransaction(new ITransactionCallback<Object>() {

			@Override
			public Object performOperation() throws Exception {
				waitFor(doa, true, eventReceiver);
				return null;
			}
		});*/
        waitFor(doa, true, eventReceiver);
    }

    @Override
    public ServiceExecutedEvent waitForEvent(IDOA doa)
            throws GeneralDOAException {
        IRunningService service = waitFor(doa, false, null);
        ServiceExecutedEvent event = new ServiceExecutedEvent(service);
        return event;
    }

}

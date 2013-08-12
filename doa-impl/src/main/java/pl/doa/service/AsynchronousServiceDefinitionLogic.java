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
package pl.doa.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.lightwolf.Flow;
import org.lightwolf.FlowMethod;
import org.lightwolf.FlowSignal;
import org.lightwolf.SuspendSignal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.agent.IAgent;
import pl.doa.agent.impl.DetachedAgent;
import pl.doa.container.IEntitiesContainer;
import pl.doa.container.impl.DetachedContainer;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.impl.DetachedDocument;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityProxy;
import pl.doa.entity.ITransactionCallback;
import pl.doa.entity.event.DetachedEvent;
import pl.doa.entity.event.source.AsynchronousEventSource;
import pl.doa.entity.event.source.SynchronousEventSource;
import pl.doa.service.impl.DetachedDocumentDefinition;
import pl.doa.service.impl.DetachedServiceDefinition;

/**
 * @author activey
 */
public abstract class AsynchronousServiceDefinitionLogic extends
        AbstractServiceDefinitionLogic {

    private static final long serialVersionUID = 1L;

    private final static Logger log = LoggerFactory
            .getLogger(AsynchronousServiceDefinitionLogic.class);

    @FlowMethod
    protected abstract void alignAsync() throws GeneralDOAException;

    @FlowMethod
    public final void align() throws GeneralDOAException {
        try {
            alignAsync();
        } catch (final FlowSignal signal) {
            if (signal instanceof SuspendSignal) {
                SuspendSignal waitForSignal = (SuspendSignal) signal;

                // serializowanie flow i zapisywanie stanu w obiekcie IRunningService
                doa.doInTransaction(new ITransactionCallback<Object>() {

                    @Override
                    public Object performOperation() throws Exception {
                        final Flow suspendedFlow = signal.getFlow();

                        ByteArrayOutputStream bytesStream =
                                new ByteArrayOutputStream();
                        ObjectOutputStream oos;
                        try {
                            oos = new ObjectOutputStream(bytesStream) {

                                public ObjectOutputStream enableReplacement()
                                        throws SecurityException {
                                    super.enableReplaceObject(true);
                                    return this;
                                }

                                @Override
                                protected Object replaceObject(Object obj)
                                        throws IOException {
                                    if (obj instanceof IEntity) {
                                        if (obj instanceof IEntityProxy) {
                                            return super.replaceObject(obj);
                                        } else if (obj instanceof IDocument) {
                                            return new DetachedDocument(doa,
                                                    (IDocument) obj);
                                        } else if (obj instanceof IEntitiesContainer) {
                                            return new DetachedContainer(doa,
                                                    (IEntitiesContainer) obj);
                                        } else if (obj instanceof IServiceDefinition) {
                                            return new DetachedServiceDefinition(
                                                    doa,
                                                    (IServiceDefinition) obj);
                                        } else if (obj instanceof IDocumentDefinition) {
                                            return new DetachedDocumentDefinition(
                                                    doa,
                                                    (IDocumentDefinition) obj);
                                        } else if (obj instanceof IAgent) {
                                            return new DetachedAgent(
                                                    doa,
                                                    (IAgent) obj);
                                        }
                                        /* ... */
                                    }

                                    return super.replaceObject(obj);
                                }

                            }.enableReplacement();
                            oos.writeObject(suspendedFlow);
                            oos.close();
                        } catch (IOException e) {
                            throw new GeneralDOAException(e);
                        }
                        getRunningService().serializeState(
                                bytesStream.toByteArray());
                        return null;
                    }
                });

                AsynchronousEventSource<DetachedEvent> eventSource =
                        (AsynchronousEventSource<DetachedEvent>) waitForSignal
                                .getResult();
                eventSource.waitForEventAsync(doa, getRunningService());
            }
            signal.defaultAction();
        }
    }

    @FlowMethod
    public final <T extends DetachedEvent> T waitForEventAsync(
            final AsynchronousEventSource<T> source) {
        if (!getRunningService().isAsynchronous()) {
            log.warn("Unable to proces event in asynchronous mode, "
                    + "parent service is executed synchronously! "
                    + "Trying to run in synchronous mode ...");
            if (source instanceof SynchronousEventSource) {
                log.debug("Running in synchronous mode ...");
                SynchronousEventSource synchronous =
                        (SynchronousEventSource) source;
                try {
                    return (T) synchronous.waitForEvent(doa);
                } catch (GeneralDOAException e) {
                    log.error("", e);
                }
            } else {
                log.warn("Unable to process event source synchronously....");
            }
        }
        return (T) Flow.suspend(source);
    }

}
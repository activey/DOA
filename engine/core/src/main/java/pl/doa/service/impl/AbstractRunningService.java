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

import org.lightwolf.Flow;
import org.lightwolf.FlowMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.IDOA;
import pl.doa.agent.IAgent;
import pl.doa.document.IDocument;
import pl.doa.document.field.impl.detached.DetachedListFieldValue;
import pl.doa.entity.DetachedEntity;
import pl.doa.entity.IEntityProxy;
import pl.doa.entity.ITransactionCallback;
import pl.doa.entity.event.DetachedEvent;
import pl.doa.entity.event.EntityEventType;
import pl.doa.entity.event.IEntityEventDescription;
import pl.doa.entity.event.WrappedEvent;
import pl.doa.entity.event.impl.ServiceExecutedByEvent;
import pl.doa.entity.event.impl.ServiceExecutedEvent;
import pl.doa.entity.impl.AbstractEntity;
import pl.doa.jvm.DOAClassLoader;
import pl.doa.service.AbstractServiceDefinitionLogic;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * Klasa jest odpowiedzialna za wykonanie uslugi na podstawie jej definicji.
 * Definicja uslugi zawiera tylko i wylacznie implementacje logiki uslugi. Klasa
 * DOARunningService czuwa nad calemy procesem wykonania uslugi.
 *
 * @author activey
 */
public abstract class AbstractRunningService extends AbstractEntity implements
        IRunningService {

    private final static Logger log = LoggerFactory
            .getLogger(AbstractRunningService.class);

    public AbstractRunningService(IDOA doa) {
        super(doa);
    }

    protected abstract IServiceDefinition getServiceDefinitionImpl();

    public final IServiceDefinition getServiceDefinition() {
        return getServiceDefinitionImpl();
    }

    protected abstract IAgent getAgentImpl();

    public final IAgent getAgent() {
        return getAgentImpl();
    }

    protected abstract IDocument getInputImpl();

    public final IDocument getInput() {
        return getInputImpl();
    }

    protected abstract IDocument getOutputImpl();

    public final IDocument getOutput() {
        return getOutputImpl();
    }

    protected abstract void setInputImpl(IDocument input);

    public final void setInput(IDocument input) {
        if (input instanceof IEntityProxy) {
            IEntityProxy<IDocument> proxy = (IEntityProxy<IDocument>) input;
            IDocument proxied = proxy.get();
            setInputImpl(proxied);
            return;
        }
        setInputImpl(input);
    }

    @FlowMethod
    public final void handleEvent(IEntityEventDescription eventDescription)
            throws Exception {
        DetachedEvent event = null;
        EntityEventType type = eventDescription.getEventType();
        if (type == EntityEventType.SERVICE_EXECUTED) {
            event = new ServiceExecutedEvent(eventDescription);
        } else if (type == EntityEventType.SERVICE_EXECUTED_BY) {
            event = new ServiceExecutedByEvent(eventDescription);
        } else {
            event = new WrappedEvent(eventDescription);
        }

        // wczytywanie obiektu Flow z bajtow i kontynuowanie procesu
        byte[] stateData = deserializeState();
        if (stateData == null) {
            return;
        }
        ObjectInputStream objectStream =
                new ObjectInputStream(new ByteArrayInputStream(stateData)) {

                    @Override
                    protected Object resolveObject(Object obj)
                            throws IOException {
                        if (obj instanceof DetachedEntity) {
                            DetachedEntity entity = (DetachedEntity) obj;
                            return entity;
                        } else if (obj instanceof AbstractServiceDefinitionLogic) {
                            AbstractServiceDefinitionLogic serviceLogic =
                                    (AbstractServiceDefinitionLogic) obj;
                            serviceLogic.setDoa(getDoa());
                            return serviceLogic;
                        } else if (obj instanceof DetachedListFieldValue) {
                            DetachedListFieldValue list =
                                    (DetachedListFieldValue) obj;
                            list.setDoa(getDoa());
                            return list;
                        }
                        return super.resolveObject(obj);
                    }

                    public ObjectInputStream enableSubstitution(boolean enabled)
                            throws SecurityException {
                        super.enableResolveObject(enabled);
                        return this;
                    }

                    @Override
                    protected Class<?> resolveClass(ObjectStreamClass clazz)
                            throws IOException, ClassNotFoundException {
                        String className = clazz.getName();
                        String logicClass =
                                getServiceDefinition().getLogicClass();
                        if (!className.equals(logicClass)) {
                            return super.resolveClass(clazz);
                        }
                        DOAClassLoader loader =
                                new DOAClassLoader(getDoa(),
                                        AbstractRunningService.class
                                                .getClassLoader());
                        return loader.loadContinuableClass(className);
                    }

                }.enableSubstitution(true);
        Object readObj = objectStream.readObject();
        final Flow flow = (Flow) readObj;

        getDoa().doInTransaction(new ITransactionCallback<Object>() {

            private DetachedEvent event;

            @Override
            public Object performOperation() throws Exception {
                flow.resume(event);
                return null;
            }

            public ITransactionCallback<Object> setEvent(DetachedEvent event) {
                this.event = event;
                return this;
            }
        }.setEvent(event));

    }

    protected abstract void setServiceDefinitionImpl(
            IServiceDefinition serviceDefinition);

    public final void setServiceDefinition(IServiceDefinition serviceDefinition) {
        setServiceDefinitionImpl(serviceDefinition);
    }

    protected abstract void setAgentImpl(IAgent agent);

    public final void setAgent(IAgent agent) {
        setAgentImpl(agent);
    }

    protected abstract void setOutputImpl(IDocument output);

    public final void setOutput(IDocument output) {
        if (output instanceof IEntityProxy<?>) {
            IEntityProxy<IDocument> proxy = (IEntityProxy<IDocument>) output;
            setOutputImpl(proxy.get());
            return;
        }
        setOutputImpl(output);
    }

    protected abstract void setAsynchronousImpl(boolean asynchronous);

    public final void setAsynchronous(boolean asynchronous) {
        setAsynchronousImpl(asynchronous);
    }

    protected abstract boolean isAsynchronousImpl();

    public final boolean isAsynchronous() {
        return isAsynchronousImpl();
    }

}
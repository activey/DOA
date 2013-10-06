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
package pl.doa.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.IDOALogic;
import pl.doa.agent.IAgent;
import pl.doa.artifact.IArtifact;
import pl.doa.artifact.IArtifact.Type;
import pl.doa.channel.IChannel;
import pl.doa.channel.IIncomingChannel;
import pl.doa.channel.IOutgoingChannel;
import pl.doa.container.IEntitiesContainer;
import pl.doa.container.IEntitiesIterator;
import pl.doa.document.DocumentValidationException;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.alignment.IDocumentAligner;
import pl.doa.entity.*;
import pl.doa.entity.event.*;
import pl.doa.entity.sort.IEntitiesSortComparator;
import pl.doa.entity.startable.IStartableEntity;
import pl.doa.entity.startable.IStartableEntityLogic;
import pl.doa.entity.startable.impl.AbstractStartableEntity;
import pl.doa.renderer.IRenderer;
import pl.doa.resource.IStaticResource;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.service.IServiceDefinitionLogic;
import pl.doa.service.annotation.EntityRef;
import pl.doa.utils.PathIterator;
import pl.doa.utils.profile.PerformanceProfiler;
import pl.doa.utils.profile.impl.ProfileAgentAction;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author activey
 */
public abstract class AbstractDOA extends AbstractStartableEntity implements
        IDOA {

    // zmienna watku, przechowuje informacje o agencie, ktory wykonuje usluge
    private final static ThreadLocal<Long> agentId = new ThreadLocal<Long>();
    private final static Logger log = LoggerFactory
            .getLogger(AbstractDOA.class);

    public AbstractDOA(IDOA doa) {
        super(doa);
    }

    public abstract void executeThread(Runnable runnable);

    @Override
    public final void publishEvent(final IEntityEventDescription event) {
        final IEntity eventEntity = event.getSourceEntity();
        // wyciaganie listy sluchaczy dla konkretnego zdarzenia
        final List<IEntityEventListener> listeners = eventEntity
                .getEventListeners(event);
        if (listeners == null || listeners.size() == 0) {
            log.debug(MessageFormat.format(
                    "There are no event listeners for: [{0}][{1}][{2}]",
                    eventEntity.getClass().getName(), eventEntity.getId() + "",
                    eventEntity.getLocation()));
            return;
        }
        final IEntityEvent storedEvent = saveEvent(event);

        // TODO reimplement it!!!
        Runnable publishedEvent = new Runnable() {

            @Override
            public void run() {
                log.debug(MessageFormat
                        .format("publishing entity event type: [{0}], for entity under location: [{1}]",
                                event.getClass().getName(),
                                eventEntity.getLocation()));
                log.debug("Event listeners count: " + listeners.size());
                for (IEntity doaEntity : listeners) {
                    final IEntityEventListener listener = (IEntityEventListener) doaEntity;
                    IEntityEventReceiver receiver = listener.getEventReceiver();
                    try {
                        receiver.handleEvent(event);
                        // TODO usuwanie sluchacza

                        EntityEventType eventType = listener.getEventType();
                        if (eventType.isRemoveAfterProcessing()) {
                            if (log.isTraceEnabled()) {
                                log.debug("Removing event listener ...");
                            }
                            doInTransaction(new ITransactionCallback<Object>() {

                                @Override
                                public Object performOperation()
                                        throws Exception {
                                    listener.remove();
                                    return null;
                                }
                            });

                        }
                        //
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }

                event.getSourceEntity().getDoa()
                        .doInTransaction(new ITransactionCallback() {

                            @Override
                            public Object performOperation() throws Exception {
                                log.debug("Removing published event: "
                                        + event.getEventType());
                                try {
                                    return storedEvent.remove();
                                } catch (Exception ex) {
                                    log.error(ex.getMessage());
                                    return null;
                                }
                            }
                        });
            }
        };
        publishedEvent.run();

        //executeThread(publishedEvent);
    }

    @Override
    public final IAgent profileAgent(IDocument incomingDocument)
            throws GeneralDOAException {
        return profileAgent(incomingDocument, "/agents");
    }

    @Override
    public final IAgent profileAgent(IDocument incomingDocument,
                                     String startLookupLocation) throws GeneralDOAException {
        return PerformanceProfiler.runProfiled(new ProfileAgentAction(this,
                incomingDocument, startLookupLocation));
    }

    protected IDOALogic getLogicInstance() {
        IDOALogic logicInstance;
        try {
            logicInstance = (IDOALogic) getRunningInstance();
            logicInstance.setDoa(this);
        } catch (GeneralDOAException e) {
            log.error("", e);
            return null;
        }
        return logicInstance;
    }

    @Override
    public void iterateEntities(IEntitiesIterator iterator,
                                IEntityEvaluator evaluator) throws GeneralDOAException {
        getLogicInstance().iterateEntities(iterator, evaluator);
    }

    @Override
    public void iterateEntities(IEntitiesIterator iterator)
            throws GeneralDOAException {
        iterateEntities(iterator, null);
    }

    public final IEntity lookupForEntity(IEntityEvaluator evaluator,
                                         boolean lookupDeep) {
        return getLogicInstance().lookupEntityFromLocation(getLocation(),
                evaluator, lookupDeep);
    }

    public Iterable<IEntity> lookupForEntities(IEntityEvaluator evaluator,
                                               boolean lookupDeep) {
        return getLogicInstance().lookupEntitiesFromLocation(getLocation(),
                evaluator, lookupDeep);
    }

    public final IEntity lookup(String startLocation,
                                IEntityEvaluator returnableEvaluator) {
        return getLogicInstance().lookup(startLocation, returnableEvaluator);
    }

    public final Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation, int start, int howMany) {
        return getLogicInstance().lookupEntitiesByLocation(entityLocation,
                start, howMany);
    }

    public final Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation) {
        return getLogicInstance().lookupEntitiesByLocation(entityLocation);
    }

    public final Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation, IEntityEvaluator evaluator) {
        return getLogicInstance().lookupEntitiesByLocation(entityLocation,
                evaluator);
    }

    public final IEntity lookupEntityByLocation(String entityLocation) {
        return getLogicInstance().lookupEntityByLocation(entityLocation);
    }

    @Override
    public IEntity lookupEntityByLocation(PathIterator<String> locationEntries) {
        return getLogicInstance().lookupEntityByLocation(
                locationEntries.getRemainingPath());
    }

    public final IEntity lookupEntityFromLocation(String fromLocation,
                                                  IEntityEvaluator evaluator, boolean lookupDeep) {
        IEntity entity = getLogicInstance().lookupEntityFromLocation(
                fromLocation, evaluator, lookupDeep);
        return entity;
    }

    public Iterable<IEntity> lookupEntitiesFromLocation(String fromLocation,
                                                        IEntityEvaluator evaluator, boolean lookupDeep) {
        return getLogicInstance().lookupEntitiesFromLocation(fromLocation,
                evaluator, lookupDeep);
    }

    public final IRenderer lookupRenderer(String rendererName) {
        return getLogicInstance().lookupRenderer(rendererName);
    }

    public final IRenderer lookupRendererByMime(String mimeType) {
        return getLogicInstance().lookupRendererByMime(mimeType);
    }

    public final IRenderer lookupRendererByMime(String startLocation,
                                                String mimeType) {
        return getLogicInstance().lookupRendererByMime(startLocation, mimeType);
    }

    public final IDocumentAligner lookupAligner(
            IDocumentDefinition fromDefinition, IDocumentDefinition toDefinition) {
        return getLogicInstance().lookupAligner(fromDefinition, toDefinition);
    }

    public final IRenderer lookupRenderer(String startLocation,
                                          String rendererName) {
        return getLogicInstance().lookupRenderer(startLocation, rendererName);
    }

    public IAgent createAgent(String name, IEntitiesContainer container)
            throws GeneralDOAException {
        return getLogicInstance().createAgent(name, container);
    }

    public final IAgent createAgent(String name) throws GeneralDOAException {
        return getLogicInstance().createAgent(name);
    }

    public final IChannel createChannel(String name, String logicClass,
                                        IEntitiesContainer container) throws GeneralDOAException {
        return getLogicInstance().createChannel(name, logicClass, container);
    }

    public final IChannel createChannel(String name, String logicClass)
            throws GeneralDOAException {
        return getLogicInstance().createChannel(name, logicClass);
    }

    public final IOutgoingChannel createOutgoingChannel(String name,
                                                        String logicClass, IEntitiesContainer container)
            throws GeneralDOAException {
        return getLogicInstance().createOutgoingChannel(name, logicClass,
                container);
    }

    public final IOutgoingChannel createOutgoingChannel(String name,
                                                        String logicClass) throws GeneralDOAException {
        return getLogicInstance().createOutgoingChannel(name, logicClass);
    }

    public final IIncomingChannel createIncomingChannel(String name,
                                                        String logicClass) throws GeneralDOAException {
        return getLogicInstance().createIncomingChannel(name, logicClass);
    }

    public final IIncomingChannel createIncomingChannel(String name,
                                                        String logicClass, IEntitiesContainer container)
            throws GeneralDOAException {
        return getLogicInstance().createIncomingChannel(name, logicClass,
                container);
    }

    public final IEntitiesContainer createContainer(String name,
                                                    IEntitiesContainer container) throws GeneralDOAException {
        return getLogicInstance().createContainer(name, container);
    }

    public final IEntitiesContainer createContainer(String name)
            throws GeneralDOAException {
        return getLogicInstance().createContainer(name);
    }

    public final IDocumentDefinition createDocumentDefinition(String name,
                                                              IEntitiesContainer container) throws GeneralDOAException {
        return getLogicInstance().createDocumentDefinition(name, container);
    }

    @Override
    public IDocumentDefinition createDocumentDefinition(String name,
                                                        IDocumentDefinition ancestor) throws GeneralDOAException {
        return getLogicInstance().createDocumentDefinition(name, ancestor);
    }

    @Override
    public IDocumentDefinition createDocumentDefinition(String name,
                                                        IEntitiesContainer container, IDocumentDefinition ancestor)
            throws GeneralDOAException {
        return getLogicInstance().createDocumentDefinition(name, container,
                ancestor);
    }

    public final IDocumentDefinition createDocumentDefinition(String name)
            throws GeneralDOAException {
        return getLogicInstance().createDocumentDefinition(name);
    }

    public final IDocument createDocument(String name,
                                          IDocumentDefinition definition, IEntitiesContainer container)
            throws GeneralDOAException {
        return getLogicInstance().createDocument(name, definition, container);
    }

    public final IDocumentAligner createDocumentAligner(String name,
                                                        IDocumentDefinition fromDefinition,
                                                        IDocumentDefinition toDefinition, IEntitiesContainer container)
            throws GeneralDOAException {
        return getLogicInstance().createDocumentAligner(name, fromDefinition,
                toDefinition, container);
    }

    public final IDocumentAligner createDocumentAligner(String name,
                                                        IDocumentDefinition fromDefinition, IDocumentDefinition toDefinition)
            throws GeneralDOAException {
        return getLogicInstance().createDocumentAligner(name, fromDefinition,
                toDefinition);
    }

    public final IDocument createDocument(String name,
                                          IDocumentDefinition definition) throws GeneralDOAException {
        return getLogicInstance().createDocument(name, definition);
    }

    public final IDocument createDocument(IDocumentDefinition definition)
            throws GeneralDOAException {
        return getLogicInstance().createDocument(definition);
    }

    public final IRenderer createRenderer(String name, String logicClass,
                                          String mimeType, IEntitiesContainer container)
            throws GeneralDOAException {
        return getLogicInstance().createRenderer(name, logicClass, mimeType,
                container);
    }

    public final IRenderer createRenderer(String name, String logicClass,
                                          String mimeType) throws GeneralDOAException {
        return getLogicInstance().createRenderer(name, logicClass, mimeType);
    }

    public final IStaticResource createStaticResource(String name,
                                                      String mimeType, IEntitiesContainer container)
            throws GeneralDOAException {
        return getLogicInstance().createStaticResource(name, mimeType,
                container);
    }

    public final IStaticResource createStaticResource(String name,
                                                      String mimeType) throws GeneralDOAException {
        return getLogicInstance().createStaticResource(name, mimeType);
    }

    @Override
    public final IStaticResource createStaticResource(String mimeType,
                                                      IEntitiesContainer container) throws GeneralDOAException {
        return getLogicInstance().createStaticResource(mimeType, container);
    }

    @Override
    public final IStaticResource createStaticResource(String mimeType)
            throws GeneralDOAException {
        return getLogicInstance().createStaticResource(mimeType);
    }

    public final IServiceDefinition createServiceDefinition(
            IEntitiesContainer container, IServiceDefinition ancestor,
            String name) throws GeneralDOAException {
        return getLogicInstance().createServiceDefinition(container, ancestor,
                name);
    }

    public final IServiceDefinition createServiceDefinition(
            IServiceDefinition ancestor, String name)
            throws GeneralDOAException {
        return getLogicInstance().createServiceDefinition(ancestor, name);
    }

    public final IServiceDefinition createServiceDefinition(String name,
                                                            String logicClass, IEntitiesContainer container)
            throws GeneralDOAException {
        return getLogicInstance().createServiceDefinition(name, logicClass,
                container);
    }

    public final IServiceDefinition createServiceDefinition(String name,
                                                            String logicClass) throws GeneralDOAException {
        return getLogicInstance().createServiceDefinition(name, logicClass);
    }

    public final IArtifact createArtifact(String name, Type type)
            throws GeneralDOAException {
        return getLogicInstance().createArtifact(name, type);
    }

    public final IRunningService createRunningService(
            IServiceDefinition serviceDefinition) throws GeneralDOAException {
        return getLogicInstance().createRunningService(serviceDefinition);
    }

    public final IRunningService createRunningService(
            IServiceDefinition serviceDefinition, IEntitiesContainer container)
            throws GeneralDOAException {
        return getLogicInstance().createRunningService(serviceDefinition,
                container);
    }

    @Override
    public final IEntityReference createReference(String referenceName,
                                                  IEntity entity) throws GeneralDOAException {
        return getLogicInstance().createReference(referenceName, entity);
    }

    @Override
    public final IEntityEvent createEntityEvent(IEntity sourceEntity,
                                                EntityEventType eventType) {
        if (sourceEntity instanceof IEntityProxy) {
            sourceEntity = ((IEntityProxy) sourceEntity).get();
        }
        return getLogicInstance().createEntityEvent(sourceEntity, eventType);
    }

    @Override
    public final IEntityEventListener createEntityEventListener(
            IEntity sourceEntity, IEntityEventReceiver receiver,
            EntityEventType eventType) {
        return getLogicInstance().createEntityEventListener(sourceEntity,
                receiver, eventType);
    }

    @Override
    public IDOA createDOA(String name, String logicClass)
            throws GeneralDOAException {
        return getLogicInstance().createDOA(name, logicClass);
    }

    @Override
    public final IDocument createExceptionDocument(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        if (throwable instanceof GeneralDOAException) {
            GeneralDOAException doaException = (GeneralDOAException) throwable;
            String exceptionDefinition = doaException.getDefinitionLocation();
            if (exceptionDefinition == null) {
                log.error("Unable to find exception document definition!");
                return null;
            }
            IDocumentDefinition def = (IDocumentDefinition) getDoa()
                    .lookupEntityByLocation(exceptionDefinition);
            if (def == null) {
                log.error("Unable to find exception document definition!");
                return null;
            }
            IDocument exceptionDoc;
            try {
                exceptionDoc = def.createDocumentInstance();
            } catch (GeneralDOAException e) {
                log.error("", e);
                return null;
            }
            try {
                doaException.buildExceptionDocument(exceptionDoc);
            } catch (Exception e) {
                log.error("", e);
            }
            return exceptionDoc;
        }

        return null;

    }

    @Override
    public final IDocument createExceptionDocument(String message,
                                                   Throwable throwable) {
        return createExceptionDocument(new GeneralDOAException(message,
                throwable));
    }

    @Override
    public final IDocument createExceptionDocument(String template,
                                                   Object... params) {
        return createExceptionDocument(MessageFormat.format(template, params));
    }

    @Override
    public final IDocument createExceptionDocument(String message) {
        return createExceptionDocument(message, (Throwable) null);
    }

    public final Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation, int start, int howMany,
            IEntitiesSortComparator comparator, IEntityEvaluator customEvaluator) {
        return getLogicInstance().lookupEntitiesByLocation(entityLocation,
                start, howMany, comparator, customEvaluator);
    }

    public final Object instantiateObject(String className) {
        return instantiateObject(className, true);
    }

    public final Object instantiateObject(String className,
                                          boolean separateClassLoader) {
        return instantiateObject(className, separateClassLoader, null);
    }

    @Override
    public final Object instantiateObject(String className,
                                          boolean separateClassLoader, boolean useContinuations) {
        try {
            if (getDoa().isRunning(this)) {
                Class<?> clazz = getLogicInstance().loadClass(className,
                        separateClassLoader, useContinuations);
                Object obj = clazz.newInstance();
                preprocessClass(clazz, obj);
                return obj;
            }

            log.error(MessageFormat
                    .format("DOA with id = [{0}] is not started yet, using system classloader",
                            getId()));
            Class<?> loadedClass = getClass().getClassLoader().loadClass(
                    className);
            Object createdObject = loadedClass.newInstance();
            preprocessClass(loadedClass, createdObject);
            return createdObject;
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    @Override
    public final Object instantiateObject(String className,
                                          boolean separateClassLoader, IEntityEvaluator artifactEvaluator) {
        try {
            if (getDoa().isRunning(this)) {
                Class<?> clazz = getLogicInstance().loadClass(className,
                        separateClassLoader, artifactEvaluator);
                Object obj = clazz.newInstance();
                preprocessClass(clazz, obj);
                return obj;
            }

            log.error(MessageFormat
                    .format("DOA with id = [{0}] is not started yet, using system classloader",
                            getId()));

            Class<?> loadedClass = Class.forName(className, false, Thread
                    .currentThread().getContextClassLoader());
            Object createdObject = loadedClass.newInstance();
            preprocessClass(loadedClass, createdObject);
            return createdObject;
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    private void preprocessClass(Class<?> clazz, Object obj) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(EntityRef.class)) {
                EntityRef refDefinition = field.getAnnotation(EntityRef.class);
                String location = refDefinition.location();
                if (location == null) {
                    continue;
                }
                IEntity found = lookupEntityByLocation(location);
                if (found == null) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                    field.set(obj, found);
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }
    }

    @Override
    public final IEntity store(IEntity entity) throws GeneralDOAException {
        return store(entity.getLocation(), entity, false, false);
    }

    @Override
    public final IEntity store(String location, IEntity entity)
            throws GeneralDOAException {
        return store(location, entity, false, false);
    }

    @Override
    public final IEntity store(String location, IEntity entity,
                               boolean forceCreateTree, boolean overwrite)
            throws GeneralDOAException {
        IEntity foundEntity = lookupEntityByLocation(location);
        if (foundEntity instanceof IEntitiesContainer) {
            IEntitiesContainer destContainer = (IEntitiesContainer) foundEntity;
            if (destContainer == null) {
                return null;
            }
            return destContainer.addEntity(entity);
        }
        return null;
    }

    @Override
    public void addURL(URL url) {
        getLogicInstance().addURL(url);
    }

    @Override
    public void removeURL(URL url) {
        getLogicInstance().removeURL(url);
    }

    @Override
    public final <T> T doInTransaction(ITransactionCallback<T> callback) {
        String txId = new Date().getTime() + "_" + Math.random();
        // log.debug("Starting transaction with id: " + txId);
        T obj = getLogicInstance().doInTransaction(callback);
        // log.debug("Finished transaction with id: " + txId);
        final List<IEntityEvent> eventsList = getTransactionEvents(txId);
        for (IEntityEvent event : eventsList) {
            publishEvent(event);
        }
        return obj;
    }

    @Override
    public <T extends Object> T doInTransaction(
            ITransactionCallback<T> callback,
            ITransactionErrorHandler errorHandler) {
        String txId = new Date().getTime() + "_" + Math.random();
        log.debug("Starting transaction with id: " + txId);
        T obj = getLogicInstance().doInTransaction(callback, errorHandler);
        log.debug("Finished transaction with id: " + txId);
        for (IEntityEvent event : getTransactionEvents(txId)) {
            publishEvent(event);
        }
        return obj;
    }

    public List<IEntityEventDescription> getAllEvents() {
        List<IEntityEventDescription> events = new ArrayList<IEntityEventDescription>();
        IEntityEvaluator evaluator = new IEntityEvaluator() {

            @Override
            public boolean isReturnableEntity(IEntity currentEntity) {
                if (currentEntity instanceof IEntityEvent) {
                    return true;
                }
                return false;
            }
        };
        for (IEntity event : lookupEntitiesByLocation(EVENTS_CONTAINER,
                evaluator)) {
            events.add((IEntityEventDescription) event);
        }
        return events;
    }

    private List<IEntityEvent> getTransactionEvents(final String txId) {
        List<IEntityEvent> events = new ArrayList<IEntityEvent>();
        IEntityEvaluator evaluator = new IEntityEvaluator() {

            @Override
            public boolean isReturnableEntity(IEntity currentEntity) {
                if (!(currentEntity instanceof IEntityEvent)) {
                    return false;
                }
                IEntityEvent event = (IEntityEvent) currentEntity;
                String eventTxId = (String) event
                        .getAttribute(IEntityEventDescription.EVENT_TX_ID);
                if (eventTxId == null) {
                    return false;
                }
                return txId.equals(eventTxId);
            }
        };
        for (IEntity event : lookupEntitiesByLocation(EVENTS_CONTAINER,
                evaluator)) {
            events.add((IEntityEvent) event);
        }
        return events;
    }

    private IEntityEvent saveEvent(IEntityEventDescription event) {
        try {
            IEntityEvent entityEvent = ((DetachedEvent) event).buildEvent(this);
            IEntity foundEntity = lookupEntityByLocation(EVENTS_CONTAINER);
            if (foundEntity instanceof IEntitiesContainer) {
                IEntitiesContainer destContainer = (IEntitiesContainer) foundEntity;
                if (destContainer != null) {
                    destContainer.addEntity(entityEvent, false);
                }
            }
            return entityEvent;
        } catch (GeneralDOAException e) {
            log.error("Unable to store event: " + event.getEventType(), e);
            return null;
        }
    }

    public final IEntity lookupByUUID(long entityId) {
        if (entityId == 0) {
            return this;
        }
        return getLogicInstance().lookupByUUID(entityId);
    }

    public final Iterable<? extends IEntity> getEntities() {
        return lookupEntitiesByLocation("/", IEntityEvaluator.ALL);
    }

    public final int countEntities() {
        return getLogicInstance().countEntities();
    }

    @Override
    public int countEntities(IEntityEvaluator evaluator) {
        return getLogicInstance().countEntities(evaluator);
    }

    @Override
    public int countEntities(IEntityEvaluator evaluator, boolean deep) {
        return getLogicInstance().countEntities(evaluator, deep);
    }

    public final Iterable<? extends IEntity> getEntities(
            IEntityEvaluator evaluator) {
        return lookupEntitiesByLocation("/", evaluator);
    }

    @Override
    public final Iterable<? extends IEntity> getEntities(int start,
                                                         int howMany, IEntitiesSortComparator comparator,
                                                         IEntityEvaluator evaluator) {
        return lookupEntitiesByLocation("/", start, howMany, comparator,
                evaluator);
    }

    @Override
    public Iterable<? extends IEntity> getEntities(int start, int howMany,
                                                   IEntitiesSortComparator comparator, IEntityEvaluator evaluator,
                                                   boolean deep) {
        return getLogicInstance().lookupEntitiesByLocation("/", start, howMany,
                comparator, evaluator, deep);
    }

    public final IEntity addEntity(IEntity doaEntity)
            throws GeneralDOAException {
        log.debug("Adding entity: " + doaEntity.getName());
        return getLogicInstance().addEntity(doaEntity);
    }

    @Override
    public void purge(IEntityEvaluator evaluator) {
        getLogicInstance().purge(evaluator);
    }

    public final IEntity addEntity(IEntity doaEntity, boolean publishEvent)
            throws GeneralDOAException {
        log.debug("Adding entity: " + doaEntity.getName());
        // TODO publishEvent should be used
        return getLogicInstance().addEntity(doaEntity);
    }

    @Override
    public final boolean hasEntity(String entityName) {
        return getLogicInstance().hasEntity(entityName);
    }

    public final IEntity getEntityByName(final String name) {
        return lookupEntityFromLocation("/", new IEntityEvaluator() {

            @Override
            public boolean isReturnableEntity(IEntity currentEntity) {
                return currentEntity.getName().equals(name);
            }
        }, false);
    }

    @Override
    public final <T extends IEntity> T getEntityByName(final String name,
                                                       final Class<T> entityType) {
        IEntity entity = lookupEntityFromLocation("/", new IEntityEvaluator() {

            @Override
            public boolean isReturnableEntity(IEntity currentEntity) {
                return currentEntity.getName().equals(name)
                        && entityType.isAssignableFrom(currentEntity.getClass());
            }
        }, false);
        return (T) entity;
    }

    public boolean hasEntities() {
        return countEntities() > 0;
    }

    @Override
    public final IAgent getAgent() {
        Long agentRefId = agentId.get();
        if (agentRefId == null) {
            return null;
        }
        IAgent agent = (IAgent) lookupByUUID(agentRefId);
        return agent;
    }

    public abstract void executeService(IRunningService runningService,
                                        boolean asynchronous) throws GeneralDOAException;

    private IRunningService createServiceInstance(
            IServiceDefinition definition, IDocument input, IAgent runAs,
            boolean asynchronous) throws GeneralDOAException {
        final IRunningService runningService = createRunningService(definition);
        runningService.setAsynchronous(asynchronous);
        if (asynchronous) {
            runningService.store("/tmp");
        }

        IDocumentDefinition inputDefinition = definition.getInputDefinition();
        if (inputDefinition != null) {
            /*
             * sprawdzanie czy definicja dokumentu wejsciowego jest tozsama z
			 * definicja dokumentu dla uslugi. Jezeli definicje sie nie zgadzaja
			 * - nalezy dokonac alignmentu.
			 */
            if (!input.isDefinedBy(inputDefinition)) {
                IDocument aligned = null;
                try {
                    aligned = input.align(inputDefinition);
                } catch (Exception e) {
                    log.error("", e);
                    runningService.setOutput(createExceptionDocument(e));
                    return runningService;
                }
                if (aligned == null) {
                    log.error("Aligner returned null document");
                    runningService
                            .setOutput(createExceptionDocument("Aligner returned null document"));
                    return runningService;
                } else {
                    input = aligned;
                }
            }
            runningService.setInput(input);

            log.debug("validating document fields ...");
            try {
                input.validateDocument();
            } catch (DocumentValidationException e) {
                runningService.setOutput(createExceptionDocument(e));
                return runningService;
            }
        }

        // analizowanie agenta
        if (runAs == null) {
            try {
                runAs = profileAgent(input, "/agents");
            } catch (GeneralDOAException e) {
                log.error("", e);
                runningService.setOutput(createExceptionDocument(e));
                return runningService;
            }
        }
        if (runAs != null) {
            runningService.setAgent(runAs);
        }

        return runningService;
    }

    @Override
    public IRunningService executeService(final IServiceDefinition definition,
                                          final IDocument input, final boolean asynchronous,
                                          final IAgent runAs) throws GeneralDOAException {
        IAgent callingAgent = runAs;

        IRunningService runningService = getDoa().doInTransaction(
                new ITransactionCallback<IRunningService>() {

                    @Override
                    public IRunningService performOperation() throws Exception {
                        return createServiceInstance(definition, input, runAs,
                                asynchronous);
                    }
                });
        if (callingAgent == null) {
            callingAgent = runningService.getAgent();
        }

        log.debug(MessageFormat
                .format("executing service [{0}] as agent: [{1}] in mode [{2}]",
                        definition.getLocation(),
                        ((callingAgent == null || callingAgent.isAnonymous()) ? "<anonymous>"
                                : callingAgent.getName()),
                        ((asynchronous) ? "asynchronous" : "synchronous")));

		/*
         * jezeli jest tryb synchroniczny - zawsze uruchamiany usluge bez
		 * sprawdzania czy instancja jest juz uruchomiona
		 */
        if (!asynchronous) {
            try {
                // ustawianie agenta w watku przed uruchomieniem uslugi
                if (runAs != null) {
                    agentId.set(runAs.getId());
                }

                executeService(runningService, asynchronous);

                // wylogowywanie uzytkownika po wykonaniu akcji
                if (runAs != null) {
                    agentId.remove();
                }
            } catch (GeneralDOAException e) {
                log.error("", e);
                runningService.setOutput(createExceptionDocument(e));
            }
            return runningService;
        }
        try {
            executeService(runningService, asynchronous);
            return runningService;
        } catch (GeneralDOAException e) {
            log.error("", e);
            return runningService;
        }
    }

    @Override
    public final IEntity lookupForEntity(IEntityEvaluator evaluator,
                                         boolean lookupDeep, IEntitiesContainer... lookupContainers) {
        for (IEntitiesContainer lookupContainer : lookupContainers) {
            IEntity foundEntity = lookupContainer.lookupForEntity(evaluator,
                    lookupDeep);
            if (foundEntity != null) {
                return foundEntity;
            }
        }
        return null;
    }

    @Override
    public final IEntity lookupEntityFromLocation(String fromLocation,
                                                  IEntityEvaluator evaluator, boolean lookupDeep,
                                                  IEntitiesContainer... lookupContainers) {
        for (IEntitiesContainer lookupContainer : lookupContainers) {
            IEntity foundEntity = lookupContainer.lookupEntityFromLocation(
                    fromLocation, evaluator, lookupDeep);
            if (foundEntity != null) {
                return foundEntity;
            }
        }
        return null;
    }

    @Override
    public IArtifact deployArtifact(String artifactFileName,
                                    byte[] artifactData, Type artifactType) throws GeneralDOAException {
        if (doa != null) {
            return doa.deployArtifact(artifactFileName, artifactData,
                    artifactType);
        }
        return null;
    }

    @Override
    public IArtifact deployArtifact(String artifactFileName,
                                    InputStream artifactData, Type artifactType)
            throws GeneralDOAException {
        if (doa != null) {
            return doa.deployArtifact(artifactFileName, artifactData,
                    artifactType);
        }
        return null;
    }

    @Override
    public final Iterable<IArtifact> getArtifacts() {
        return getArtifacts(null);
    }

    @Override
    public final Iterable<IArtifact> getArtifacts(IEntityEvaluator evaluator) {
        return getLogicInstance().getArtifacts(evaluator);
    }

    @Override
    public void undeployArtifact(IArtifact artifact) throws GeneralDOAException {
        if (doa != null) {
            doa.undeployArtifact(artifact);
            return;
        }
    }

    @Override
    public IServiceDefinitionLogic getRunning(IRunningService runningService) {
        if (doa != null) {
            return doa.getRunning(runningService);
        }
        return null;
    }

    @Override
    public IServiceDefinitionLogic getRunning(String runningServiceUUID) {
        if (doa != null) {
            return doa.getRunning(runningServiceUUID);
        }
        return null;
    }

    @Override
    public IStartableEntityLogic startup(IStartableEntity startableEntity)
            throws GeneralDOAException {
        if (doa != null) {
            return doa.startup(startableEntity);
        }
        return null;
    }

    @Override
    public void shutdown(IStartableEntity startableEntity)
            throws GeneralDOAException {
        if (doa != null) {
            doa.shutdown(startableEntity);
            return;
        }
    }

    @Override
    public IStartableEntityLogic getRunning(IStartableEntity startableEntity) {
        if (doa != null) {
            return doa.getRunning(startableEntity);
        }
        return null;
    }

    @Override
    public boolean isRunning(IStartableEntity startableEntity) {
        if (doa != null) {
            return doa.isRunning(startableEntity);
        }
        return false;
    }

    @Override
    public long storeOrUpdate(IStaticResource resource, InputStream dataStream)
            throws Exception {
        if (doa != null) {
            return doa.storeOrUpdate(resource, dataStream);
        }
        return 0;
    }

    @Override
    public boolean removeFileStream(IStaticResource resource) throws Exception {
        if (doa != null) {
            return doa.removeFileStream(resource);
        }
        return false;
    }

    @Override
    public InputStream retrieve(IStaticResource resource) throws Exception {
        if (doa != null) {
            return doa.retrieve(resource);
        }
        return null;
    }
}
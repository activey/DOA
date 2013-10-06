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
import pl.doa.artifact.IArtifact;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.impl.DetachedDocument;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.entity.IEntityReference;
import pl.doa.entity.ITransactionCallback;
import pl.doa.entity.evaluator.EntityTypeEvaluator;
import pl.doa.entity.sort.IEntitiesSortComparator;
import pl.doa.entity.startable.IStartableEntity;
import pl.doa.jvm.DOAClassLoader;
import pl.doa.resource.IStaticResource;
import pl.doa.resource.impl.DetachedStaticResource;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.service.impl.DetachedRunningService;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author activey
 */
public abstract class AbstractDOALogic implements IDOALogic {

    private final static Logger log = LoggerFactory
            .getLogger(AbstractDOALogic.class);

    private DOAClassLoader classLoader;

    protected IDOA doa;

    @Override
    public final IRunningService createRunningService(
            IServiceDefinition serviceDefinition) throws GeneralDOAException {
        if (serviceDefinition == null) {
            throw new GeneralDOAException("Service definition is required!");
        }
        IRunningService detached =
                new DetachedRunningService(serviceDefinition, doa);
        return detached;
    }

    public final IRunningService createRunningService(
            IServiceDefinition serviceDefinition, IEntitiesContainer container)
            throws GeneralDOAException {
        if (serviceDefinition == null) {
            throw new GeneralDOAException("Service definition is required!");
        }
        if (container == null) {
            return createRunningService(serviceDefinition);
        }
        IRunningService service = createRunningServiceImpl(serviceDefinition);
        container.addEntity(service);
        return service;
    }

    protected abstract IRunningService createRunningServiceImpl(
            IServiceDefinition serviceDefinition);

    protected abstract IDocument createDocumentImpl(String name,
                                                    IDocumentDefinition definition) throws GeneralDOAException;

    public final IDocument createDocument(String name,
                                          IDocumentDefinition definition, IEntitiesContainer container)
            throws GeneralDOAException {
        if (definition == null) {
            throw new GeneralDOAException("Document definition is required!");
        }
        if (container == null) {
            return createDocument(name, definition);
        }
        IDocument newDoc = createDocumentImpl(name, definition);
        if (container != null) {
            container.addEntity(newDoc);
        }
        return newDoc;
    }

    public IDocument createDocument(String name, IDocumentDefinition definition)
            throws GeneralDOAException {
        DetachedDocument doc = new DetachedDocument(doa, definition);
        doc.setName(name);
        return doc;
    }

    public final IDocument createDocument(IDocumentDefinition definition)
            throws GeneralDOAException {
        return createDocument(null, definition);
    }

    @Override
    public final Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation, int start, int howMany) {
        EntityLocationIterator locationIterator =
                new EntityLocationIterator(entityLocation);
        return lookupEntitiesByLocation(locationIterator, start, howMany);
    }

    protected abstract Iterable<? extends IEntity> lookupEntitiesByLocation(
            EntityLocationIterator locationIterator, int start, int howMany);

    @Override
    public Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation, int start, int howMany, boolean deep) {
        EntityLocationIterator locationIterator =
                new EntityLocationIterator(entityLocation);
        return lookupEntitiesByLocation(locationIterator, start, howMany, deep);
    }

    protected abstract Iterable<? extends IEntity> lookupEntitiesByLocation(
            EntityLocationIterator entityLocation, int start, int howMany,
            boolean deep);

    @Override
    public Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation, int start, int howMany,
            IEntitiesSortComparator<? extends IEntity> comparator,
            IEntityEvaluator customEvaluator, boolean deep) {
        EntityLocationIterator locationIterator =
                new EntityLocationIterator(entityLocation);
        return lookupEntitiesByLocation(locationIterator, start, howMany,
                comparator, customEvaluator, deep);
    }

    protected abstract Iterable<? extends IEntity> lookupEntitiesByLocation(
            EntityLocationIterator entityLocation, int start, int howMany,
            IEntitiesSortComparator<? extends IEntity> comparator,
            IEntityEvaluator customEvaluator, boolean deep);

    @Override
    public Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation, boolean deep) {
        EntityLocationIterator locationIterator =
                new EntityLocationIterator(entityLocation);
        return lookupEntitiesByLocation(locationIterator, deep);
    }

    protected abstract Iterable<? extends IEntity> lookupEntitiesByLocation(
            EntityLocationIterator entityLocation, boolean deep);

    @Override
    public Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation, IEntityEvaluator evaluator, boolean deep) {
        EntityLocationIterator locationIterator =
                new EntityLocationIterator(entityLocation);
        return lookupEntitiesByLocation(locationIterator, evaluator, deep);
    }

    protected abstract Iterable<? extends IEntity> lookupEntitiesByLocation(
            EntityLocationIterator entityLocation, IEntityEvaluator evaluator,
            boolean deep);

    @Override
    public final Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation, int start, int howMany,
            IEntitiesSortComparator<? extends IEntity> comparator,
            IEntityEvaluator customEvaluator) {
        EntityLocationIterator locationIterator =
                new EntityLocationIterator(entityLocation);
        return lookupEntitiesByLocation(locationIterator, start, howMany,
                comparator, customEvaluator);
    }

    protected abstract Iterable<? extends IEntity> lookupEntitiesByLocation(
            EntityLocationIterator locationIterator, int start, int howMany,
            IEntitiesSortComparator<? extends IEntity> comparator,
            IEntityEvaluator customEvaluator);

    @Override
    public final IStaticResource createStaticResource(String name,
                                                      String mimeType) throws GeneralDOAException {
        DetachedStaticResource resource =
                new DetachedStaticResource(doa, name, mimeType);
        return resource;
    }

    @Override
    public final IStaticResource createStaticResource(String mimeType) {
        DetachedStaticResource resource =
                new DetachedStaticResource(doa, mimeType);
        return resource;
    }

    @Override
    public final Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation) {
        EntityLocationIterator locationIterator =
                new EntityLocationIterator(entityLocation);
        return lookupEntitiesByLocation(locationIterator);
    }

    protected abstract Iterable<? extends IEntity> lookupEntitiesByLocation(
            EntityLocationIterator locationIterator);

    @Override
    public final Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation, IEntityEvaluator evaluator) {
        EntityLocationIterator locationIterator =
                new EntityLocationIterator(entityLocation);
        return lookupEntitiesByLocation(locationIterator, evaluator);
    }

    protected abstract Iterable<? extends IEntity> lookupEntitiesByLocation(
            EntityLocationIterator locationIterator, IEntityEvaluator evaluator);

    @Override
    public final IEntity lookupEntityByLocation(String entityLocation) {
        EntityLocationIterator locationIterator =
                new EntityLocationIterator(entityLocation);
        IEntity found = lookupEntityByLocation(locationIterator);
        if (locationIterator.hasNext() && found != null) {
            if (found instanceof IEntitiesContainer) {
                IEntitiesContainer otherContainer = (IEntitiesContainer) found;
                return otherContainer.lookupEntityByLocation(locationIterator);
            }
        }
        return found;
    }

    protected abstract IEntity lookupEntityByLocation(
            EntityLocationIterator entityLocation);

    @Override
    public final IEntity lookupEntityFromLocation(String fromLocation,
                                                  IEntityEvaluator evaluator, boolean deep) {
        EntityLocationIterator locationIterator =
                new EntityLocationIterator(fromLocation);
        return lookupEntityFromLocation(locationIterator, evaluator, deep);
    }

    protected abstract IEntity lookupEntityFromLocation(
            EntityLocationIterator fromLocation, IEntityEvaluator evaluator,
            boolean deep);

    @Override
    public final Iterable<IEntity> lookupEntitiesFromLocation(
            String fromLocation, IEntityEvaluator evaluator, boolean deep) {
        EntityLocationIterator locationIterator =
                new EntityLocationIterator(fromLocation);
        return lookupEntitiesFromLocation(locationIterator, evaluator, deep);
    }

    protected abstract Iterable<IEntity> lookupEntitiesFromLocation(
            EntityLocationIterator fromLocation, IEntityEvaluator evaluator,
            boolean deep);

    @Override
    public final IEntity lookup(String startLocation,
                                IEntityEvaluator returnableEvaluator) {
        EntityLocationIterator locationIterator =
                new EntityLocationIterator(startLocation);
        return lookup(locationIterator, returnableEvaluator);
    }

    protected abstract IEntity lookup(EntityLocationIterator startLocation,
                                      IEntityEvaluator returnableEvaluator);

    protected abstract void startupImpl() throws Exception;

    @Override
    public final void startup() throws GeneralDOAException {
        long start = System.currentTimeMillis();
        try {
            startupImpl();
        } catch (Exception e) {
            throw new GeneralDOAException(e);
        }
        this.classLoader = new DOAClassLoader(doa, getClass().getClassLoader());
        if (needsTreeInitialization()) {
            log.debug("Starting DOA for the first time, initializing base tree ...");
            doa.doInTransaction(new ITransactionCallback<Object>() {

                @Override
                public Object performOperation() throws Exception {
                    IEntitiesContainer container =
                            doa.createContainer("resources", doa);
                    container = doa.createContainer("artifacts", doa);
                    container = doa.createContainer("channels", doa);
                    container = doa.createContainer("agents", doa);
                    container = doa.createContainer("autostart", doa);

                    container = doa.createContainer("documents", doa);
                    IEntitiesContainer systemDocuments =
                            doa.createContainer("system", container);
                    IDocumentDefinition waitingForDefinition =
                            doa.createDocumentDefinition(
                                    "waiting_for_document", systemDocuments);
                    waitingForDefinition.addField("message",
                            DocumentFieldDataType.string, true, false);

                    IDocumentDefinition exceptionDefinition =
                            doa.createDocumentDefinition("exception",
                                    systemDocuments);
                    exceptionDefinition.addField("message",
                            DocumentFieldDataType.string);
                    exceptionDefinition.addField("stackTrace",
                            DocumentFieldDataType.list);
                    exceptionDefinition.addField("date",
                            DocumentFieldDataType.date, true, false);

                    IDocumentDefinition validationErrorsDefinition =
                            doa.createDocumentDefinition("validation_errors",
                                    systemDocuments);
                    validationErrorsDefinition.addField("errors",
                            DocumentFieldDataType.list);

                    container = doa.createContainer("renderers", doa);
                    container = doa.createContainer("aligners", doa);

                    container = doa.createContainer("services", doa);
                    doa.createContainer("listeners", container);

                    container = doa.createContainer("tmp", doa);
                    container = doa.createContainer("applications", doa);
                    container = doa.createContainer("events", doa);
                    return null;
                }
            });
        } else {
            // wczytywanie artefaktow
            log.debug("Registering artifacts ...");
            Iterable<IArtifact> artifacts = getArtifacts(null);
            for (IArtifact artifact : artifacts) {
                String artifactUrl =
                        MessageFormat.format("doa:{0}/{1}",
                                IDOA.ARTIFACTS_CONTAINER, artifact.getName());
                log.debug(MessageFormat.format(
                        "Registering Class Loader artifact: [{0}.{1}.{2}]",
                        artifact.getArtifactId(), artifact.getGroupId(),
                        artifact.getVersion()));
                try {
                    addURL(new URL(artifactUrl));
                } catch (MalformedURLException e) {
                    log.error("", e);
                }
            }

            // uruchamianie obiektow typu startable
            log.debug("Running startable entities ...");
            Iterable<IEntity> startableEntities =
                    lookupEntitiesFromLocation(IDOA.AUTOSTART_CONTAINER,
                            new IEntityEvaluator() {
                                @Override
                                public boolean isReturnableEntity(IEntity entity) {
                                    if (entity instanceof IEntityReference) {
                                        IEntityReference reference =
                                                (IEntityReference) entity;
                                        IEntity referencedEntity =
                                                reference.getEntity();
                                        if (referencedEntity instanceof IStartableEntity) {
                                            entity = referencedEntity;
                                        }
                                    }
                                    return entity instanceof IStartableEntity;
                                }
                            }, true);
            if (startableEntities != null) {
                for (IEntity startableEntity : startableEntities) {
                    IStartableEntity startableCandidate = null;
                    if (startableEntity instanceof IEntityReference) {
                        IEntityReference reference =
                                (IEntityReference) startableEntity;
                        IEntity referencedEntity = reference.getEntity();
                        if (referencedEntity instanceof IStartableEntity) {
                            startableCandidate =
                                    (IStartableEntity) referencedEntity;
                        }
                    } else {
                        startableCandidate = (IStartableEntity) startableEntity;
                    }

                    final IStartableEntity startable = startableCandidate;
                    if (!startable.isAutostart()) {
                        log.debug(MessageFormat
                                .format("Startable entity [{0}] is not marked as autostart, skipping ...",
                                        startable.getLocation()));
                        continue;
                    }
                    log.debug(MessageFormat.format("Starting up entity: [{0}]",
                            startable.getLocation()));

                    doa.doInTransaction(new ITransactionCallback() {
                        @Override
                        public Object performOperation() throws Exception {

                            startable.startup();
                            return null;
                        }
                    });
                }
            }

            // publikacja zdarzen
            // TODO implement publishing all remaining events
        }
        long end = System.currentTimeMillis();
        long totalMs = end - start;
        double totalSec = totalMs / 1000;
        log.debug(MessageFormat.format(
                "DOA initialized in {0} sec [{1,number,#} ms] ...", totalSec,
                totalMs));
    }

    @Override
    public final Class<?> loadClass(String className,
                                    boolean separateClassLoader, boolean useContinuations)
            throws Exception {
        try {
            if (separateClassLoader) {
                DOAClassLoader loader =
                        new DOAClassLoader(doa, getClass().getClassLoader());
                if (useContinuations) {
                    return loader.loadContinuableClass(className);
                } else {
                    return Class.forName(className, false, loader);
                }
            }
            return Class.forName(className, false, classLoader);
        } catch (ClassNotFoundException e) {
            log.error("", e);
            return null;
        }
    }

    @Override
    public Class<?> loadClass(String className, boolean separateClassLoader,
                              IEntityEvaluator artifactEvaluator) throws Exception {
        return loadClass(className, separateClassLoader, false);
    }

    @Override
    public Class<?> loadClass(String className, boolean separateClassLoader)
            throws Exception {
        return loadClass(className, separateClassLoader, null);
    }

    @Override
    public Class<?> loadClass(String className) {
        try {
            return Class.forName(className, false, classLoader);
        } catch (ClassNotFoundException e) {
            log.error("", e);
            return null;
        }
    }

    @Override
    public void addURL(URL url) {
        classLoader.addURL(url);
    }

    @Override
    public void removeURL(URL url) {
        classLoader.removeURL(url);
    }

    @Override
    public final void setDoa(IDOA doa) {
        this.doa = doa;
    }

    public abstract boolean needsTreeInitialization();

    @Override
    public Iterable<IArtifact> getArtifacts(IEntityEvaluator evaluator) {
        List<IArtifact> artifacts = new ArrayList<IArtifact>();
        for (IEntity entity : lookupEntitiesByLocation(
                IDOA.ARTIFACTS_CONTAINER, new EntityTypeEvaluator(
                IArtifact.class, evaluator))) {
            IArtifact artifact = (IArtifact) entity;
            artifacts.add(artifact);
        }
        return artifacts;
    }

    @Override
    public void purge(IEntityEvaluator evaluator) {
        Iterable<IEntity> entities =
                lookupEntitiesFromLocation("/", evaluator, true);
        for (IEntity entity : entities) {
            entity.remove(true);
        }
    }
}

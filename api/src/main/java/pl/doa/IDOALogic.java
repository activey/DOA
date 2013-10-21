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

import pl.doa.agent.IAgent;
import pl.doa.artifact.IArtifact;
import pl.doa.channel.IChannel;
import pl.doa.channel.IIncomingChannel;
import pl.doa.channel.IOutgoingChannel;
import pl.doa.container.IEntitiesContainer;
import pl.doa.container.IEntitiesIterator;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.alignment.IDocumentAligner;
import pl.doa.entity.*;
import pl.doa.entity.event.EntityEventType;
import pl.doa.entity.event.IEntityEvent;
import pl.doa.entity.event.IEntityEventListener;
import pl.doa.entity.event.IEntityEventReceiver;
import pl.doa.entity.sort.IEntitiesSortComparator;
import pl.doa.entity.startable.IStartableEntityLogic;
import pl.doa.renderer.IRenderer;
import pl.doa.resource.IStaticResource;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;

import java.net.URL;

/**
 * @author activey
 */
public interface IDOALogic extends IStartableEntityLogic {

    public IAgent createAgent(String name, IEntitiesContainer container)
            throws GeneralDOAException;

    public IAgent createAgent(String name) throws GeneralDOAException;

    public IChannel createChannel(String name, String logicClass, IEntitiesContainer container)
            throws GeneralDOAException;

    public IChannel createChannel(String name, String logicClass) throws GeneralDOAException;

    public IOutgoingChannel createOutgoingChannel(String name, String logicClass, IEntitiesContainer container)
            throws GeneralDOAException;

    public IOutgoingChannel createOutgoingChannel(String name, String logicClass) throws GeneralDOAException;

    public IIncomingChannel createIncomingChannel(String name, String logicClass, IEntitiesContainer container)
            throws GeneralDOAException;

    public IIncomingChannel createIncomingChannel(String name, String logicClass) throws GeneralDOAException;

    public IEntitiesContainer createContainer(String name, IEntitiesContainer container) throws GeneralDOAException;

    public IEntitiesContainer createContainer(String name) throws GeneralDOAException;

    public IDocumentDefinition createDocumentDefinition(String name, IEntitiesContainer container)
            throws GeneralDOAException;

    public IDocumentDefinition createDocumentDefinition(String name)
            throws GeneralDOAException;

    public IDocumentDefinition createDocumentDefinition(String name, IEntitiesContainer container,
            IDocumentDefinition ancestor)
            throws GeneralDOAException;

    public IDocumentDefinition createDocumentDefinition(String name, IDocumentDefinition ancestor)
            throws GeneralDOAException;

    public IDocument createDocument(String name, IDocumentDefinition definition, IEntitiesContainer container)
            throws GeneralDOAException;

    public IDocumentAligner createDocumentAligner(String name, IDocumentDefinition fromDefinition,
            IDocumentDefinition toDefinition, IEntitiesContainer container) throws GeneralDOAException;

    public IDocumentAligner createDocumentAligner(String name, IDocumentDefinition fromDefinition,
            IDocumentDefinition toDefinition)
            throws GeneralDOAException;

    public IDocument createDocument(String name, IDocumentDefinition definition)
            throws GeneralDOAException;

    public IDocument createDocument(IDocumentDefinition definition)
            throws GeneralDOAException;

    public IRenderer createRenderer(String name, String logicClass,
            String mimeType, IEntitiesContainer container)
            throws GeneralDOAException;

    public IRenderer createRenderer(String name, String logicClass,
            String mimeType) throws GeneralDOAException;

    public IStaticResource createStaticResource(String name, String mimeType,
            IEntitiesContainer container) throws GeneralDOAException;

    public IStaticResource createStaticResource(String name, String mimeType)
            throws GeneralDOAException;

    public IStaticResource createStaticResource(String mimeType,
            IEntitiesContainer container) throws GeneralDOAException;

    public IStaticResource createStaticResource(String mimeType);

    public IServiceDefinition createServiceDefinition(String name,
            String logicClass, IEntitiesContainer container)
            throws GeneralDOAException;

    public IServiceDefinition createServiceDefinition(String name,
            String logicClass) throws GeneralDOAException;

    public IServiceDefinition createServiceDefinition(
            IServiceDefinition ancestor, String name)
            throws GeneralDOAException;

    public IServiceDefinition createServiceDefinition(
            IEntitiesContainer container, IServiceDefinition ancestor,
            String name) throws GeneralDOAException;

    public IArtifact createArtifact(String name) throws GeneralDOAException;

    public IEntityEvent createEntityEvent(IEntity sourceEntity,
            EntityEventType eventType);

    public IEntityEventListener createEntityEventListener(IEntity sourceEntity,
            IEntityEventReceiver receiver, EntityEventType eventType);

    public IRunningService createRunningService(
            IServiceDefinition serviceDefinition) throws GeneralDOAException;

    public IRunningService createRunningService(
            IServiceDefinition serviceDefinition, IEntitiesContainer container)
            throws GeneralDOAException;

    public IEntityReference createReference(String referenceName, IEntity entity)
            throws GeneralDOAException;

    public IDOA createDOA(String name, String logicClass)
            throws GeneralDOAException;

    public void iterateEntities(IEntitiesIterator iterator,
            IEntityEvaluator evaluator) throws GeneralDOAException;

    public Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation, int start, int howMany);

    public Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation, int start, int howMany, boolean deep);

    public Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation, int start, int howMany,
            IEntitiesSortComparator<? extends IEntity> comparator,
            IEntityEvaluator customEvaluator);

    public Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation, int start, int howMany,
            IEntitiesSortComparator<? extends IEntity> comparator,
            IEntityEvaluator customEvaluator, boolean deep);

    public Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation);

    public Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation, boolean deep);

    public Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation, IEntityEvaluator evaluator);

    public Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation, IEntityEvaluator evaluator, boolean deep);

    public IEntity lookupEntityByLocation(String entityLocation);

    public IEntity lookupEntityFromLocation(String fromLocation,
            IEntityEvaluator evaluator, boolean deep);

    public Iterable<IEntity> lookupEntitiesFromLocation(String fromLocation,
            IEntityEvaluator evaluator, boolean deep);

    public IEntity lookup(String startLocation,
            IEntityEvaluator returnableEvaluator);

    public IRenderer lookupRenderer(String rendererName);

    public IRenderer lookupRendererByMime(String mimeType);

    public IRenderer lookupRendererByMime(String startLocation,
            final String mimeType);

    public IRenderer lookupRenderer(String startLocation,
            final String rendererName);

    public IDocumentAligner lookupAligner(IDocumentDefinition from,
            IDocumentDefinition to);

    public <T> T doInTransaction(ITransactionCallback<T> callback);

    public <T> T doInTransaction(ITransactionCallback<T> callback,
            ITransactionErrorHandler errorHandler);

    public Class<?> loadClass(String className, boolean separateClassLoader,
            IEntityEvaluator artifactEvaluator) throws Exception;

    public Class<?> loadClass(String className, boolean separateClassLoader)
            throws Exception;

    public Class<?> loadClass(String className, boolean separateClassLoader,
            boolean useContinuations) throws Exception;

    public Class<?> loadClass(String className);

    public void addURL(URL url);

    public IEntity lookupByUUID(long entityId);

    public int countEntities();

    public int countEntities(IEntityEvaluator evaluator);

    public int countEntities(IEntityEvaluator evaluator, boolean deep);

    public IEntity addEntity(IEntity doaEntity) throws GeneralDOAException;

    public boolean hasEntity(String entityName);

    public Iterable<IArtifact> getArtifacts(IEntityEvaluator evaluator);

    public void purge(IEntityEvaluator evaluator);

}

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
package pl.doa;

import pl.doa.agent.IAgent;
import pl.doa.artifact.IArtifact;
import pl.doa.artifact.IArtifact.Type;
import pl.doa.channel.IChannel;
import pl.doa.channel.IIncomingChannel;
import pl.doa.channel.IOutgoingChannel;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.alignment.IDocumentAligner;
import pl.doa.entity.*;
import pl.doa.entity.event.*;
import pl.doa.entity.startable.IStartableEntity;
import pl.doa.entity.startable.IStartableEntityLogic;
import pl.doa.renderer.IRenderer;
import pl.doa.resource.IStaticResource;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.service.IServiceDefinitionLogic;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public interface IDOA extends IEntitiesContainer, IStartableEntity {

	public static String EVENTS_CONTAINER = "/events";

	public final String ARTIFACTS_CONTAINER = "/artifacts";

	public final String AUTOSTART_CONTAINER = "/autostart";

	public IAgent createAgent(String name, IEntitiesContainer container)
			throws GeneralDOAException;

	public IAgent createAgent(String name) throws GeneralDOAException;

	public IChannel createChannel(String name, String logicClass,
			IEntitiesContainer container) throws GeneralDOAException;

	public IOutgoingChannel createOutgoingChannel(String name,
			String logicClass, IEntitiesContainer container)
			throws GeneralDOAException;

	public IOutgoingChannel createOutgoingChannel(String name, String logicClass)
			throws GeneralDOAException;

	public IIncomingChannel createIncomingChannel(String name,
			String logicClass, IEntitiesContainer container)
			throws GeneralDOAException;

	public IIncomingChannel createIncomingChannel(String name, String logicClass)
			throws GeneralDOAException;

	public IChannel createChannel(String name, String logicClass)
			throws GeneralDOAException;

	public IEntitiesContainer createContainer(String name,
			IEntitiesContainer container) throws GeneralDOAException;

	public IEntitiesContainer createContainer(String name)
			throws GeneralDOAException;

	public IDocumentDefinition createDocumentDefinition(String name,
			IEntitiesContainer container) throws GeneralDOAException;

	public IDocumentDefinition createDocumentDefinition(String name)
			throws GeneralDOAException;

	public IDocumentDefinition createDocumentDefinition(String name,
			IDocumentDefinition ancestor) throws GeneralDOAException;

	public IDocumentDefinition createDocumentDefinition(String name,
			IEntitiesContainer container, IDocumentDefinition ancestor)
			throws GeneralDOAException;

	public IDocument createDocument(String name,
			IDocumentDefinition definition, IEntitiesContainer container)
			throws GeneralDOAException;

	public IDocumentAligner createDocumentAligner(String name,
			IDocumentDefinition fromDefinition,
			IDocumentDefinition toDefinition, IEntitiesContainer container)
			throws GeneralDOAException;

	public IDocumentAligner createDocumentAligner(String name,
			IDocumentDefinition fromDefinition, IDocumentDefinition toDefinition)
			throws GeneralDOAException;

	public IDocument createDocument(IDocumentDefinition definition)
			throws GeneralDOAException;

	public IDocument createDocument(String name, IDocumentDefinition definition)
			throws GeneralDOAException;

	public IRenderer createRenderer(String name, String logicClass,
			String mimeType, IEntitiesContainer container)
			throws GeneralDOAException;

	public IRenderer createRenderer(String name, String logicClass,
			String mimeType) throws GeneralDOAException;

	public IStaticResource createStaticResource(String name, String mimeType,
			IEntitiesContainer container) throws GeneralDOAException;

	public IStaticResource createStaticResource(String mimeType,
			IEntitiesContainer container) throws GeneralDOAException;

	public IStaticResource createStaticResource(String mimeType)
			throws GeneralDOAException;

	public IStaticResource createStaticResource(String name, String mimeType)
			throws GeneralDOAException;

	public IServiceDefinition createServiceDefinition(String name,
			String logicClass, IEntitiesContainer container)
			throws GeneralDOAException;

	public IServiceDefinition createServiceDefinition(String name,
			String logicClass) throws GeneralDOAException;

	public IServiceDefinition createServiceDefinition(
			IEntitiesContainer container, IServiceDefinition ancestor,
			String name) throws GeneralDOAException;

	public IServiceDefinition createServiceDefinition(
			IServiceDefinition ancestor, String name)
			throws GeneralDOAException;

	public IArtifact createArtifact(String name, Type type)
			throws GeneralDOAException;

	public IRunningService createRunningService(
			IServiceDefinition serviceDefinition) throws GeneralDOAException;

	public IRunningService createRunningService(
			IServiceDefinition serviceDefinition, IEntitiesContainer container)
			throws GeneralDOAException;

	public IDOA createDOA(String name, String logicClass)
			throws GeneralDOAException;

	public IDocument createExceptionDocument(String template, Object... params);

	public IDocument createExceptionDocument(String message);

	public IDocument createExceptionDocument(Throwable throwable);

	public IDocument createExceptionDocument(String message, Throwable throwable);

	public IEntityReference createReference(String referenceName, IEntity entity)
			throws GeneralDOAException;

	public IEntityEvent createEntityEvent(IEntity sourceEntity,
			EntityEventType eventType);

	public IEntityEventListener createEntityEventListener(IEntity sourceEntity,
			IEntityEventReceiver eventReceiver, EntityEventType eventType);

	public void publishEvent(IEntityEventDescription eventDescription);

	public Object instantiateObject(String className);
	
	public Object instantiateObject(String className,
			boolean separateClassLoader);
	
	public Object instantiateObject(String className,
			boolean separateClassLoader, boolean useContinuations);

	public Object instantiateObject(String className,
			boolean separateClassLoader, IEntityEvaluator artifactEvaluator);


	public void addURL(URL url);

	public void removeURL(URL url);

	public <T extends IEntity> T store(T entity) throws GeneralDOAException;

	public <T extends IEntity> T store(String location, T entity)
			throws GeneralDOAException;

	public <T extends IEntity> T store(String location, T entity,
			boolean forceCreateTree, boolean overwrite)
			throws GeneralDOAException;

	public IEntity lookupByUUID(long entityId);

	public <T> T doInTransaction(ITransactionCallback<T> callback);

	public <T> T doInTransaction(ITransactionCallback<T> iTransactionCallback,
			ITransactionErrorHandler iTransactionErrorHandler);

	public IArtifact deployArtifact(String artifactFileName,
			byte[] artifactData, IArtifact.Type artifactType)
			throws GeneralDOAException;

	public IArtifact deployArtifact(String artifactFileName,
			InputStream artifactData, IArtifact.Type artifactType)
			throws GeneralDOAException;

	public Iterable<IArtifact> getArtifacts();

	public Iterable<IArtifact> getArtifacts(IEntityEvaluator evaluator);

	public void undeployArtifact(IArtifact artifact) throws GeneralDOAException;

	public IServiceDefinitionLogic getRunning(IRunningService runningService);

	public IServiceDefinitionLogic getRunning(String runningServiceUUID);

	public IStartableEntityLogic startup(IStartableEntity startableEntity)
			throws GeneralDOAException;

	public void shutdown(IStartableEntity startableEntity)
			throws GeneralDOAException;

	public IStartableEntityLogic getRunning(IStartableEntity startableEntity);

	public boolean isRunning(IStartableEntity startableEntity);

	public IAgent getAgent();

	public IRunningService executeService(IServiceDefinition definition,
			IDocument input, boolean asynchronous, IAgent runAs)
			throws GeneralDOAException;

	public IAgent profileAgent(IDocument incomingDocument,
			String startLookupLocation) throws GeneralDOAException;

	public IAgent profileAgent(IDocument incomingDocument)
			throws GeneralDOAException;

	public IEntity lookupForEntity(IEntityEvaluator evaluator,
			boolean lookupDeep, IEntitiesContainer... lookupContainers);

	public IEntity lookupEntityFromLocation(String fromLocation,
			IEntityEvaluator evaluator, boolean lookupDeep,
			IEntitiesContainer... lookupContainers);

	public IDocumentAligner lookupAligner(IDocumentDefinition fromDefinition,
			IDocumentDefinition toDefinition);

	public long storeOrUpdate(IStaticResource resource, InputStream dataStream)
			throws Exception;

	public boolean removeFileStream(IStaticResource resource) throws Exception;

	public InputStream retrieve(IStaticResource resource) throws Exception;

	public List<IEntityEventDescription> getAllEvents();

}
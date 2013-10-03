package pl.doa.artifact.deploy;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.agent.IAgent;
import pl.doa.artifact.IArtifact;
import pl.doa.channel.IChannel;
import pl.doa.channel.IIncomingChannel;
import pl.doa.channel.IOutgoingChannel;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.alignment.IDocumentAligner;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityReference;
import pl.doa.entity.event.EntityEventType;
import pl.doa.entity.event.IEntityEventListener;
import pl.doa.entity.event.IEntityEventReceiver;
import pl.doa.entity.startable.IStartableEntity;
import pl.doa.renderer.IRenderer;
import pl.doa.resource.IStaticResource;
import pl.doa.service.IServiceDefinition;

import java.io.File;

public interface IDeploymentProcessor {

    public void process(File deployedFile, IEntitiesContainer root) throws Exception;

    public void processingDone();

    public IEntitiesContainer createEntitiesContainer(String name, IEntitiesContainer parent) throws GeneralDOAException;

    public IEntityReference createReference(String name, IEntity entity, IEntitiesContainer parent) throws GeneralDOAException;

    public IAgent createAgent(String name, IEntitiesContainer parent) throws GeneralDOAException;

    public IDocumentDefinition createDocumentDefinition(String name, IEntitiesContainer container, IDocumentDefinition ancestor) throws GeneralDOAException;

    public IDocumentDefinition createDocumentDefinition(String name, IEntitiesContainer container) throws GeneralDOAException;

    public IDOA createDOA(String name, String logicClass, IEntitiesContainer parent) throws GeneralDOAException;

    public IServiceDefinition createServiceDefinition(String name, String logicClass, IEntitiesContainer parent) throws GeneralDOAException;

    public IServiceDefinition createServiceDefinition(IServiceDefinition ancestor, String name, IEntitiesContainer parent) throws GeneralDOAException;

    public IDocumentAligner createDocumentAligner(String name, IDocumentDefinition from, IDocumentDefinition to, IEntitiesContainer parent) throws GeneralDOAException;

    public IRenderer createRenderer(String name, String logicClass, String mimetype, IEntitiesContainer parentContainer) throws GeneralDOAException;

    public IChannel createChannel(String name, String logicClass, IEntitiesContainer parentContainer) throws GeneralDOAException;

    public IIncomingChannel createIncomingChannel(String name, String logicClass, IEntitiesContainer parentContainer) throws GeneralDOAException;

    public IOutgoingChannel createOutgoingChannel(String name, String logicClass, IEntitiesContainer parentContainer) throws GeneralDOAException;

    public IStaticResource createStaticResource(String name, String mimetype, IEntitiesContainer parentContainer) throws GeneralDOAException;

    public IEntityEventListener createEntityEventListener(IEntity sourceEntity, IEntityEventReceiver receiver, EntityEventType eventType, IEntitiesContainer parentContainer) throws GeneralDOAException;

    public void registerAutostartEntity(IStartableEntity entity);

    public void setDoa(IDOA doa);

    public void setArtifact(IArtifact artifact);
}

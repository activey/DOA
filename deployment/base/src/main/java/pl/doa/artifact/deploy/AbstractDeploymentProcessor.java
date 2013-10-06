package pl.doa.artifact.deploy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.agent.IAgent;
import pl.doa.artifact.IArtifact;
import pl.doa.channel.IChannel;
import pl.doa.channel.IIncomingChannel;
import pl.doa.channel.IOutgoingChannel;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author activey
 * @date 02.10.13 20:41
 */
public abstract class AbstractDeploymentProcessor implements IDeploymentProcessor {

    private final static Logger log = LoggerFactory.getLogger(AbstractDeploymentProcessor.class);
    private IArtifact artifact;
    private IDOA doa;
    private List<IStartableEntity> startableEntities = new ArrayList();

     public final IEntitiesContainer createEntitiesContainer(String name, IEntitiesContainer parent) throws GeneralDOAException {
        IEntitiesContainer container = doa.createContainer(name, parent);
        artifact.registerEntity(container);
        return container;
    }

    @Override
    public final IEntityReference createReference(String name, IEntity entity, IEntitiesContainer parent) throws GeneralDOAException {
        IEntityReference reference = doa.createReference(name, entity);
        parent.addEntity(reference);
        artifact.registerEntity(reference);
        return reference;
    }

    @Override
    public final IAgent createAgent(String name, IEntitiesContainer parent) throws GeneralDOAException {
        IAgent agent = doa.createAgent(name, parent);
        artifact.registerEntity(agent);
        return agent;
    }

    @Override
    public IDocumentDefinition createDocumentDefinition(String name, IEntitiesContainer parent) throws GeneralDOAException {
        IDocumentDefinition definition = doa.createDocumentDefinition(name, parent);
        artifact.registerEntity(definition);
        return definition;
    }

    @Override
    public IDocumentDefinition createDocumentDefinition(String name, IEntitiesContainer container, IDocumentDefinition ancestor) throws GeneralDOAException {
        IDocumentDefinition definition = doa.createDocumentDefinition(name, container, ancestor);
        artifact.registerEntity(definition);
        return definition;
    }

    @Override
    public IDOA createDOA(String name, String logicClass, IEntitiesContainer parent) throws GeneralDOAException {
        IDOA newDoa = doa.createDOA(name, logicClass);
        parent.addEntity(newDoa);
        artifact.registerEntity(newDoa);
        return newDoa;
    }

    @Override
    public IDocumentAligner createDocumentAligner(String name, IDocumentDefinition from, IDocumentDefinition to, IEntitiesContainer parent) throws GeneralDOAException {
        IDocumentAligner aligner = doa.createDocumentAligner(name, from, to);
        parent.addEntity(aligner);
        artifact.registerEntity(aligner);
        return aligner;
    }

    @Override
    public IDocument createDocument(IDocumentDefinition definition, String name, IEntitiesContainer parent) throws GeneralDOAException {
        IDocument document = definition.createDocumentInstance(name);
        parent.addEntity(document);
        artifact.registerEntity(document);
        return document;
    }

    @Override
    public IServiceDefinition createServiceDefinition(String name, String logicClass, IEntitiesContainer parent) throws GeneralDOAException {
        IServiceDefinition definition = doa.createServiceDefinition(name, logicClass, parent);
        artifact.registerEntity(definition);
        return definition;
    }

    @Override
    public IServiceDefinition createServiceDefinition(IServiceDefinition ancestor, String name, IEntitiesContainer parent) throws GeneralDOAException {
        IServiceDefinition definition = doa.createServiceDefinition(ancestor, name);
        parent.addEntity(definition);
        artifact.registerEntity(definition);
        return definition;
    }

    @Override
    public IRenderer createRenderer(String name, String logicClass, String mimetype, IEntitiesContainer parent) throws GeneralDOAException {
        IRenderer renderer = doa.createRenderer(name, logicClass, mimetype, parent);
        artifact.registerEntity(renderer);
        return renderer;
    }

    @Override
    public IChannel createChannel(String name, String logicClass, IEntitiesContainer parent) throws GeneralDOAException {
        IChannel channel = doa.createChannel(name, logicClass, parent);
        artifact.registerEntity(channel);
        return channel;
    }

    @Override
    public IIncomingChannel createIncomingChannel(String name, String logicClass, IEntitiesContainer parent) throws GeneralDOAException {
        IIncomingChannel channel = doa.createIncomingChannel(name, logicClass, parent);
        artifact.registerEntity(channel);
        return channel;
    }

    @Override
    public IOutgoingChannel createOutgoingChannel(String name, String logicClass, IEntitiesContainer parent) throws GeneralDOAException {
        IOutgoingChannel channel = doa.createOutgoingChannel(name, logicClass, parent);
        artifact.registerEntity(channel);
        return channel;
    }

    @Override
    public IStaticResource createStaticResource(String name, String mimetype, IEntitiesContainer parent) throws GeneralDOAException {
        IStaticResource resource = doa.createStaticResource(name, mimetype, parent);
        artifact.registerEntity(resource);
        return resource;
    }

    @Override
    public IEntityEventListener createEntityEventListener(IEntity sourceEntity, IEntityEventReceiver receiver, EntityEventType eventType, IEntitiesContainer parent) throws GeneralDOAException {
        IEntityEventListener listener = doa.createEntityEventListener(sourceEntity, receiver, eventType);
        parent.addEntity(listener);
        artifact.registerEntity(listener);
        return listener;
    }

    @Override
    public IEntity lookupEntityByLocation(String location) {
        return doa.lookupEntityByLocation(location);
    }

    @Override
    public void registerAutostartEntity(IStartableEntity entity) {
        startableEntities.add(entity);
    }

    @Override
    public void process(File deployedFile, IEntitiesContainer deploymentRoot) throws Exception {
        artifact.setBaseContainer(deploymentRoot);
        deployArtifact(deployedFile, deploymentRoot);
        processingDone();
    }

    @Override
    public Object instantiateJavaObject(String value) {
        return doa.instantiateObject(value);
    }

    public abstract void deployArtifact(File deployedFile, IEntitiesContainer deploymentRoot) throws Exception;

    private void processingDone() {
        IEntitiesContainer autostart = (IEntitiesContainer) doa.lookupEntityByLocation(IDOA.AUTOSTART_CONTAINER);

        for (IStartableEntity startableEntity : startableEntities) {
            try {
                createReference("startable_" + startableEntity.getId(), startableEntity, autostart);

                log.debug(MessageFormat.format("Starting up entity: {0}",
                        startableEntity.getLocation()));
                startableEntity.startup();
                log.debug(MessageFormat.format(
                        "Entity under location {0} started up ...",
                        startableEntity.getLocation()));
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    @Override
    public void setArtifact(IArtifact artifact) {
        this.artifact = artifact;
    }

    @Override
    public void setDoa(IDOA doa) {
        this.doa = doa;
    }
}

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
package pl.doa.impl.neo4j;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.event.TransactionEventHandler;
import org.neo4j.graphdb.traversal.*;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.INeoObject;
import pl.doa.NeoEntityDelegator;
import pl.doa.agent.IAgent;
import pl.doa.agent.impl.neo.NeoAgent;
import pl.doa.artifact.IArtifact;
import pl.doa.artifact.impl.neo.NeoArtifact;
import pl.doa.channel.IChannel;
import pl.doa.channel.IIncomingChannel;
import pl.doa.channel.IOutgoingChannel;
import pl.doa.channel.impl.neo.NeoChannel;
import pl.doa.channel.impl.neo.NeoIncomingChannel;
import pl.doa.channel.impl.neo.NeoOutgoingChannel;
import pl.doa.container.IEntitiesContainer;
import pl.doa.container.IEntitiesIterator;
import pl.doa.container.impl.neo.NeoEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.alignment.IDocumentAligner;
import pl.doa.document.alignment.impl.neo.NeoDocumentAligner;
import pl.doa.document.impl.neo.NeoDocument;
import pl.doa.document.impl.neo.NeoDocumentDefinition;
import pl.doa.entity.*;
import pl.doa.entity.event.EntityEventType;
import pl.doa.entity.event.IEntityEvent;
import pl.doa.entity.event.IEntityEventListener;
import pl.doa.entity.event.IEntityEventReceiver;
import pl.doa.entity.event.impl.neo.NeoEntityEvent;
import pl.doa.entity.event.impl.neo.NeoEntityEventListener;
import pl.doa.entity.impl.neo.NeoEntityReference;
import pl.doa.entity.impl.neo.NeoReturnableEvaluator;
import pl.doa.entity.sort.IEntitiesSortComparator;
import pl.doa.entity.startable.IStartableEntity;
import pl.doa.impl.AbstractDOALogic;
import pl.doa.impl.EntityLocationIterator;
import pl.doa.neo.NodeDelegate;
import pl.doa.neo.utils.EntitiesListIterable;
import pl.doa.neo.utils.NodeUtils;
import pl.doa.relation.DOARelationship;
import pl.doa.renderer.IRenderer;
import pl.doa.renderer.impl.neo.NeoRenderer;
import pl.doa.resource.IStaticResource;
import pl.doa.resource.impl.neo.NeoStaticResource;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.service.impl.neo.NeoRunningService;
import pl.doa.service.impl.neo.NeoServiceDefinition;
import pl.doa.utils.DynamicInteger;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author activey
 */
public class NeoDOALogic extends AbstractDOALogic {

    private static final String PROP_NEO_STORE_DIR = "neo.store.dir";
    private static final String PROP_NEO_SHELL = "neo.shell";
    private static final String PROP_NEO_SHELL_PORT = "neo.shell.port";
    private static final String PROP_NEO_TRANSACTION_LOGGER = "neo.transaction.logger";
    private final static Logger log = LoggerFactory
            .getLogger(NeoDOALogic.class);
    private EmbeddedGraphDatabase neo;
    private IStartableEntity startableEntity;

    public IAgent createAgent(String name, IEntitiesContainer container)
            throws GeneralDOAException {
        NeoAgent agent = new NeoAgent(doa, neo, name);
        if (container != null) {
            container.addEntity(agent);
        }
        return agent;
    }

    public IAgent createAgent(String name) throws GeneralDOAException {
        return createAgent(name, null);
    }

    public IChannel createChannel(String name, String logicClass,
                                  IEntitiesContainer container) throws GeneralDOAException {
        NeoChannel channel = new NeoChannel(doa, neo, name, logicClass);
        if (container != null) {
            container.addEntity(channel);
        }
        return channel;
    }

    public IChannel createChannel(String name, String logicClass)
            throws GeneralDOAException {
        return createChannel(name, logicClass, null);
    }

    public IOutgoingChannel createOutgoingChannel(String name,
                                                  String logicClass, IEntitiesContainer container)
            throws GeneralDOAException {
        NeoOutgoingChannel outgoingChannel =
                new NeoOutgoingChannel(doa, neo, name, logicClass);
        if (container != null) {
            container.addEntity(outgoingChannel);
        }
        return outgoingChannel;
    }

    public IOutgoingChannel createOutgoingChannel(String name, String logicClass)
            throws GeneralDOAException {
        return createOutgoingChannel(name, logicClass, null);
    }

    public IIncomingChannel createIncomingChannel(String name,
                                                  String logicClass, IEntitiesContainer container)
            throws GeneralDOAException {
        NeoIncomingChannel incomingChannel =
                new NeoIncomingChannel(doa, neo, name, logicClass);
        if (container != null) {
            container.addEntity(incomingChannel);
        }
        return incomingChannel;
    }

    public IIncomingChannel createIncomingChannel(String name, String logicClass)
            throws GeneralDOAException {
        return createIncomingChannel(name, logicClass, null);
    }

    public IEntitiesContainer createContainer(String name,
                                              IEntitiesContainer container) throws GeneralDOAException {
        NeoEntitiesContainer newContainer =
                new NeoEntitiesContainer(doa, neo, name);
        if (container != null) {
            container.addEntity(newContainer);
        }
        return newContainer;
    }

    public IEntitiesContainer createContainer(String name)
            throws GeneralDOAException {
        return createContainer(name, null);
    }

    public IDocumentDefinition createDocumentDefinition(String name,
                                                        IEntitiesContainer container) throws GeneralDOAException {
        NeoDocumentDefinition newDef =
                new NeoDocumentDefinition(doa, neo, name);
        if (container != null) {
            container.addEntity(newDef);
        }
        return newDef;
    }

    public IDocumentDefinition createDocumentDefinition(String name)
            throws GeneralDOAException {
        return createDocumentDefinition(name, (IEntitiesContainer) null);
    }

    @Override
    public IDocumentDefinition createDocumentDefinition(String name,
                                                        IEntitiesContainer container, IDocumentDefinition ancestor)
            throws GeneralDOAException {
        NeoDocumentDefinition newDef =
                new NeoDocumentDefinition(doa, neo, name, ancestor);
        if (container != null) {
            container.addEntity(newDef);
        }
        return newDef;
    }

    @Override
    public IDocumentDefinition createDocumentDefinition(String name,
                                                        IDocumentDefinition ancestor) throws GeneralDOAException {
        return createDocumentDefinition(name, null, ancestor);
    }

    @Override
    public IDocumentAligner createDocumentAligner(String name,
                                                  IDocumentDefinition fromDefinition,
                                                  IDocumentDefinition toDefinition, IEntitiesContainer container)
            throws GeneralDOAException {
        NeoDocumentAligner aligner = new NeoDocumentAligner(doa, neo, name);
        aligner.setFromDefinition(fromDefinition);
        aligner.setToDefinition(toDefinition);
        if (container != null) {
            container.addEntity(aligner);
        }
        return aligner;
    }

    @Override
    public IDocumentAligner createDocumentAligner(String name,
                                                  IDocumentDefinition fromDefinition, IDocumentDefinition toDefinition)
            throws GeneralDOAException {
        NeoDocumentAligner aligner = new NeoDocumentAligner(doa, neo, name);
        aligner.setFromDefinition(fromDefinition);
        aligner.setToDefinition(toDefinition);
        return aligner;
    }

    protected IDocument createDocumentImpl(String name,
                                           IDocumentDefinition definition) throws GeneralDOAException {
        if (definition == null) {
            throw new GeneralDOAException("Document definition is required!");
        }
        return new NeoDocument(doa, neo, definition, name);
    }

    public IRenderer createRenderer(String name, String logicClass,
                                    String mimeType, IEntitiesContainer container)
            throws GeneralDOAException {
        NeoRenderer renderer =
                new NeoRenderer(doa, neo, name, logicClass, mimeType);
        if (container != null) {
            container.addEntity(renderer);
        }
        return renderer;
    }

    public IRenderer createRenderer(String name, String logicClass,
                                    String mimeType) throws GeneralDOAException {
        return createRenderer(name, logicClass, mimeType, null);
    }

    public IStaticResource createStaticResource(String name, String mimeType,
                                                IEntitiesContainer container) throws GeneralDOAException {
        NeoStaticResource resource = new NeoStaticResource(doa, neo, name);
        resource.setMimetype(mimeType);
        if (container != null) {
            container.addEntity(resource);
        }
        return resource;
    }

    @Override
    public IStaticResource createStaticResource(String mimeType,
                                                IEntitiesContainer container) throws GeneralDOAException {
        NeoStaticResource resource = new NeoStaticResource(doa, neo);
        resource.setMimetype(mimeType);
        if (container != null) {
            container.addEntity(resource);
        }
        return resource;
    }

    public IServiceDefinition createServiceDefinition(String name,
                                                      String logicClass, IEntitiesContainer container)
            throws GeneralDOAException {
        NeoServiceDefinition definition =
                new NeoServiceDefinition(doa, neo, name, logicClass);
        if (container != null) {
            container.addEntity(definition);
        }
        return definition;
    }

    @Override
    public IServiceDefinition createServiceDefinition(
            IServiceDefinition ancestor, String name)
            throws GeneralDOAException {
        NeoServiceDefinition definition =
                new NeoServiceDefinition(doa, neo, name, ancestor);
        return definition;
    }

    @Override
    public IServiceDefinition createServiceDefinition(
            IEntitiesContainer container, IServiceDefinition ancestor,
            String name) throws GeneralDOAException {
        NeoServiceDefinition definition =
                new NeoServiceDefinition(doa, neo, name, ancestor);
        if (container != null) {
            container.addEntity(definition);
        }
        return definition;
    }

    public IServiceDefinition createServiceDefinition(String name,
                                                      String logicClass) throws GeneralDOAException {
        return createServiceDefinition(name, logicClass,
                (IEntitiesContainer) null);
    }

    @Override
    protected IRunningService createRunningServiceImpl(
            IServiceDefinition serviceDefinition) {
        NeoRunningService running = new NeoRunningService(doa, neo);
        running.setServiceDefinition(serviceDefinition);
        return running;
    }

    @Override
    public IArtifact createArtifact(String name)
            throws GeneralDOAException {
        NeoArtifact artifact = new NeoArtifact(doa, neo, name);
        return artifact;
    }

    @Override
    public IEntityReference createReference(String referenceName, IEntity entity)
            throws GeneralDOAException {
        NeoEntityReference reference = new NeoEntityReference(doa, neo);
        reference.setName(referenceName);
        reference.setEntity(entity);
        return reference;
    }

    @Override
    public IEntityEvent createEntityEvent(IEntity sourceEntity,
                                          EntityEventType eventType) {
        NeoEntityEvent event =
                new NeoEntityEvent(doa, neo, sourceEntity, eventType);
        return event;
    }

    @Override
    public IDOA createDOA(String name, String logicClass)
            throws GeneralDOAException {
        NeoDOA embeddedDoa = new NeoDOA(doa, neo, name, logicClass);
        return embeddedDoa;
    }

    @Override
    public IEntityEventListener createEntityEventListener(IEntity sourceEntity,
                                                          IEntityEventReceiver receiver, EntityEventType eventType) {
        NeoEntityEventListener listener =
                new NeoEntityEventListener(doa, neo, sourceEntity, receiver,
                        eventType);
        return listener;
    }

    public Iterable<? extends IEntity> lookupEntitiesByLocation(
            EntityLocationIterator entityLocation, int start, int howMany) {
        return lookupEntitiesByLocation(entityLocation, start, howMany, null,
                null);
    }

    @Override
    protected Iterable<? extends IEntity> lookupEntitiesByLocation(
            EntityLocationIterator entityLocation, int start, int howMany,
            boolean deep) {
        return lookupEntitiesByLocation(entityLocation, start, howMany, null,
                null, deep);
    }

    @Override
    protected Iterable<? extends IEntity> lookupEntitiesByLocation(
            EntityLocationIterator entityLocation, boolean deep) {
        return lookupEntitiesByLocation(entityLocation, 0, 0, null, null, deep);
    }

    @Override
    protected Iterable<? extends IEntity> lookupEntitiesByLocation(
            EntityLocationIterator entityLocation, IEntityEvaluator evaluator,
            boolean deep) {
        return lookupEntitiesByLocation(entityLocation, 0, 0, null, evaluator,
                deep);
    }

    @Override
    protected Iterable<? extends IEntity> lookupEntitiesByLocation(
            EntityLocationIterator locationIterator, int start, int howMany,
            IEntitiesSortComparator<? extends IEntity> comparator,
            IEntityEvaluator customEvaluator) {
        return lookupEntitiesByLocation(locationIterator, 0, 0, comparator,
                customEvaluator, false);
    }

    public Iterable<? extends IEntity> lookupEntitiesByLocation(
            EntityLocationIterator entityLocation, final int start,
            final int howMany,
            IEntitiesSortComparator<? extends IEntity> comparator,
            final IEntityEvaluator customEvaluator, boolean deep) {
        IEntity entity = lookupEntityByLocation(entityLocation);
        if (entity == null) {
            return new ArrayList<IEntity>();
        }
        if (!(entity instanceof IEntitiesContainer)) {
            return new ArrayList<IEntity>();
        }
        IEntitiesContainer container = (IEntitiesContainer) entity;
        if (entityLocation.isRoot()) {
            Traverser traverser =
                    Traversal
                            .description()
                            .evaluator(Evaluators.excludeStartPosition())
                            .breadthFirst()
                            .evaluator(
                                    new NeoReturnableEvaluator(doa,
                                            customEvaluator, deep))
                            .evaluator(
                                    (deep) ? Evaluators.all() : Evaluators
                                            .atDepth(1))
                            .relationships(DOARelationship.HAS_ENTITY,
                                    Direction.OUTGOING)
                            .traverse(neo.getReferenceNode());
            return new EntitiesListIterable(doa, traverser.iterator(), start,
                    howMany);
        } else {
            if (container instanceof IDOA) {
                entityLocation.trim();
                IDOA nestedDoa = (IDOA) container;
                return nestedDoa.lookupEntitiesByLocation(
                        entityLocation.toString(), start, howMany, comparator,
                        customEvaluator);
            }
        }
        return container.getEntities(start, howMany, comparator,
                customEvaluator);

    }

    public Iterable<? extends IEntity> lookupEntitiesByLocation(
            EntityLocationIterator entityLocation) {
        return lookupEntitiesByLocation(entityLocation, 0, 0);
    }

    public Iterable<? extends IEntity> lookupEntitiesByLocation(
            EntityLocationIterator entityLocation, IEntityEvaluator evaluator) {
        return lookupEntitiesByLocation(entityLocation, 0, 0, null, evaluator);
    }

    @Override
    public IEntity lookupEntityByLocation(EntityLocationIterator entityLocation) {
        Node foundNode =
                NodeUtils.lookupForNodeByPath(neo.getReferenceNode(),
                        entityLocation);
        if (foundNode == null) {
            return null;
        }
        if ("/".equals(entityLocation) && (foundNode instanceof IEntity)) {
            return (IEntity) foundNode;
        }
        // TODO
        if (!foundNode.hasProperty(NodeDelegate.PROP_CLASS_NAME)) {
            return doa;
        }
        IEntity entity =
                (IEntity) NeoEntityDelegator.createEntityInstance(doa,
                        foundNode);
        return entity;
    }

    @Override
    public IEntity lookupEntityFromLocation(
            EntityLocationIterator fromLocation, IEntityEvaluator evaluator,
            boolean deep) {
        Node foundNode =
                NodeUtils.lookupForNodeByPath(neo.getReferenceNode(),
                        fromLocation);
        if (foundNode == null) {
            return null;
        }

        // szukanie od konkretnego node
        Traverser traverser =
                Traversal
                        .description()
                        .depthFirst()
                        .evaluator(
                                (deep) ? Evaluators.fromDepth(1) : Evaluators
                                        .atDepth(1))
                        .evaluator(Evaluators.excludeStartPosition())
                        .uniqueness(Uniqueness.NODE_PATH)
                        .relationships(DOARelationship.HAS_ENTITY,
                                Direction.OUTGOING)
                        .evaluator(
                                new NeoReturnableEvaluator(doa, evaluator, deep))
                        .traverse(foundNode);
        Node resultNode = null;
        for (Path path : traverser) {
            if (resultNode == null) {
                resultNode = path.endNode();
            }
        }
        if (resultNode == null) {
            return null;
        }
        if (resultNode instanceof IEntity) {
            return (IEntity) foundNode;
        }
        IEntity entity =
                NeoEntityDelegator.createEntityInstance(doa, resultNode);
        return entity;
    }

    @Override
    public Iterable<IEntity> lookupEntitiesFromLocation(
            EntityLocationIterator fromLocation, IEntityEvaluator evaluator,
            boolean deep) {
        Node foundNode =
                NodeUtils.lookupForNodeByPath(neo.getReferenceNode(),
                        fromLocation);
        if (foundNode == null) {
            return null;
        }
        // szukanie od konkretnego node
        Traverser traverser =
                Traversal
                        .description()
                        .breadthFirst()
                        .evaluator(
                                new NeoReturnableEvaluator(doa, evaluator, deep))
                        .evaluator(
                                (deep) ? Evaluators.all() : Evaluators
                                        .atDepth(1))
                        .relationships(DOARelationship.HAS_ENTITY,
                                Direction.OUTGOING).traverse(foundNode);
        return new EntitiesListIterable(doa, traverser.iterator());
    }

    public IEntity lookup(EntityLocationIterator startLocation,
                          IEntityEvaluator returnableEvaluator) {
        Node startNode =
                NodeUtils.lookupForNodeByPath(neo.getReferenceNode(),
                        startLocation);
        if (startNode == null) {
            return null;
        }
        Traverser traverser =
                Traversal
                        .description()
                        .breadthFirst()
                        .evaluator(
                                new NeoReturnableEvaluator(doa,
                                        returnableEvaluator, false))
                        .relationships(DOARelationship.HAS_ENTITY,
                                Direction.OUTGOING).traverse(startNode);
        Node result = null;
        for (Path path : traverser) {
            result = path.endNode();
            break;
        }
        return NeoEntityDelegator.createEntityInstance(doa, result);
    }

    public IEntity lookup(EntityLocationIterator startLocation,
                          Evaluator returnableEvaluator, RelationshipType relationType) {
        Node startNode =
                NodeUtils.lookupForNodeByPath(neo.getReferenceNode(),
                        startLocation);
        return lookup(startNode, returnableEvaluator, relationType);
    }

    public IEntity lookup(Node startNode, Evaluator returnableEvaluator,
                          RelationshipType relationType) {
        if (startNode == null) {
            return null;
        }
        Traverser traverser =
                Traversal.description().breadthFirst()
                        .evaluator(returnableEvaluator)
                        .relationships(relationType, Direction.OUTGOING)
                        .traverse(startNode);
        Node result = null;
        for (Path path : traverser) {
            result = path.endNode();
            break;

        }
        return NeoEntityDelegator.createEntityInstance(doa, result);
    }

    public IRenderer lookupRenderer(String rendererName) {
        return lookupRenderer("/renderers", rendererName);
    }

    public IRenderer lookupRendererByMime(String mimeType) {
        return lookupRendererByMime("/renderers", mimeType);
    }

    public IRenderer lookupRendererByMime(String startLocation,
                                          final String mimeType) {
        IRenderer renderer =
                (IRenderer) lookupEntityFromLocation(startLocation,
                        new IEntityEvaluator() {

                            @Override
                            public boolean isReturnableEntity(IEntity entity) {
                                if (!(entity instanceof NeoRenderer)) {
                                    return false;
                                }
                                IRenderer renderer = (IRenderer) entity;
                                if (renderer == null) {
                                    return false;
                                }
                                if (mimeType.equals(renderer.getMimetype())) {
                                    return true;
                                }
                                return false;
                            }
                        }, true);
        return renderer;
    }

    public IRenderer lookupRenderer(String startLocation,
                                    final String rendererName) {

        IRenderer renderer =
                (IRenderer) lookupEntityFromLocation(startLocation,
                        new IEntityEvaluator() {

                            @Override
                            public boolean isReturnableEntity(
                                    IEntity currentEntity) {
                                if (!(currentEntity instanceof IRenderer)) {
                                    return false;
                                }
                                return currentEntity.getName().equals(
                                        rendererName);
                            }
                        }, true);

        return renderer;
    }

    @Override
    public void shutdown() throws GeneralDOAException {
        if (this.neo == null) {
            throw new GeneralDOAException("Neo4j store is already stopped!");
        }
        log.debug("Shutting down Neo4j ...");
        neo.shutdown();
        this.neo = null;
    }

    @Override
    public boolean isStartedUp() {
        if (this.neo == null) {
            return false;
        }
        return true;
    }

    protected void startupImpl() throws GeneralDOAException {
        if (this.neo != null) {
            throw new GeneralDOAException("Neo4j store is already started!");
        }
        log.debug("Initializing Neo4j ...");
        Map<String, String> params = new HashMap<String, String>();

        String shell = startableEntity.getAttribute(PROP_NEO_SHELL);
        if (shell != null) {
            params.put("enable_remote_shell", "true");
            String shellPort =
                    startableEntity.getAttribute(PROP_NEO_SHELL_PORT);
            if (shellPort != null) {
                params.put("enable_remote_shell", "port=" + shellPort);
            }
        }
        String storeDirectory =
                startableEntity.getAttribute(PROP_NEO_STORE_DIR);
        this.neo = new EmbeddedGraphDatabase(storeDirectory, params);

        String transactionLogger = startableEntity.getAttribute(PROP_NEO_TRANSACTION_LOGGER);
        if (transactionLogger != null) {
            log.debug("Registering Neo4j transaction logger: " + transactionLogger);
            try {
                Class<TransactionEventHandler> logger = (Class<TransactionEventHandler>) super
                        .loadClass(transactionLogger, true);
                neo.registerTransactionEventHandler(logger.newInstance());
            } catch (Exception e) {
                log.error("", e);
            }
        }

		/*neo.registerTransactionEventHandler(new TransactionEventHandler<String>() {

			@Override
			public String beforeCommit(TransactionData data) throws Exception {
				Iterable<Node> nodes = data.createdNodes();
				for (Node node : nodes) {
					if (node.hasProperty("_class")) {
						System.out.println(">>>>>>>>>>>> "
								+ node.getProperty("_class"));
					} else {
						System.out.println(">>>>>>>>>>>> " + node.getId());
					}

				}
				return null;
			}

			@Override
			public void afterCommit(TransactionData data, String state) {

			}

			@Override
			public void afterRollback(TransactionData data, String state) {

			}

		});*/
    }

    @Override
    public void setStartableEntity(IStartableEntity startableEntity) {
        this.startableEntity = startableEntity;
    }

    @Override
    public Object doInTransaction(ITransactionCallback callback) {
        //log.debug("Beginning Neo Transaction");
        Transaction tx = neo.beginTx();
        try {
            Object result = callback.performOperation();
            //log.debug("Commiting Neo Transaction");
            tx.success();
            return result;
        } catch (Exception e) {
            log.error("Neo Transaction error", e);
            tx.failure();
            return null;
        } finally {
            //log.debug("Finishing Neo Transaction");
            tx.finish();
        }
    }

    @Override
    public Object doInTransaction(ITransactionCallback callback,
                                  ITransactionErrorHandler errorHandler) {
        //log.debug("Beginning Neo Transaction");
        Transaction tx = neo.beginTx();

        try {
            Object result = callback.performOperation();
            //log.debug("Commiting Neo Transaction");
            tx.success();
            return result;
        } catch (Exception e) {
            log.error("Neo Transaction error", e);
            if (errorHandler == null) {
                return null;
            }
            errorHandler.handleException(e);
            tx.failure();
            return null;
        } finally {
            //log.debug("Finishing Neo Transaction");
            tx.finish();
        }
    }

    @Override
    public IEntity lookupByUUID(long entityId) {
        Node node = neo.getNodeById(entityId);
        return NeoEntityDelegator.createEntityInstance(doa, node);
    }

    @Override
    public IDocumentAligner lookupAligner(IDocumentDefinition from,
                                          IDocumentDefinition to) {
        /*
         * wyszukiwanie alignera
		 */
        log.debug(MessageFormat.format("looking up for aligner: {0} -> {1}",
                from.getLocation(), to.getLocation()));
        INeoObject neoEntity = (INeoObject) from;
        // szukanie alignera
        IDocumentAligner aligner = null;
        Iterable<Relationship> alignerRelations =
                neoEntity.getNode()
                        .getRelationships(DOARelationship.HAS_FROM_DEFINITION,
                                Direction.INCOMING);
        for (Relationship alignerRelation : alignerRelations) {
            Node alignerNode = alignerRelation.getStartNode();
            Node toDefinitionNode =
                    alignerNode.getSingleRelationship(
                            DOARelationship.HAS_TO_DEFINITION,
                            Direction.OUTGOING).getEndNode();
            if (toDefinitionNode.getId() == to.getId()) {

                aligner = new NeoDocumentAligner(doa, alignerNode);
            }
        }
        return aligner;
    }

    @Override
    public int countEntities() {
        final DynamicInteger dyn = new DynamicInteger();
        Iterable<Relationship> relations =
                neo.getReferenceNode().getRelationships(
                        DOARelationship.HAS_ENTITY, Direction.OUTGOING);
        for (Relationship relationship : relations) {
            dyn.modify(1);
        }
        return dyn.getValue();
    }

    @Override
    public int countEntities(final IEntityEvaluator evaluator,
                             final boolean deep) {
        final DynamicInteger dyn = new DynamicInteger();
        TraversalDescription travDesc =
                Traversal
                        .description()
                        .evaluator(Evaluators.excludeStartPosition())
                        .relationships(DOARelationship.HAS_ENTITY,
                                Direction.OUTGOING)
                        .breadthFirst()
                        .evaluator(
                                (deep) ? Evaluators.all() : Evaluators
                                        .toDepth(1)).evaluator(new Evaluator() {
                    int actualIndex = 0;

                    @Override
                    public Evaluation evaluate(Path path) {
                        if (evaluator != null) {
                            return new NeoReturnableEvaluator(doa,
                                    evaluator, deep).evaluate(path);
                        }
                        return Evaluation.INCLUDE_AND_CONTINUE;
                    }
                });
        Traverser nodesTraverser = travDesc.traverse(neo.getReferenceNode());
        for (Path node : nodesTraverser) {
            dyn.modify(1);
        }
        return dyn.getValue();
    }

    @Override
    public int countEntities(final IEntityEvaluator evaluator) {
        return countEntities(evaluator, false);
    }

    @Override
    public IEntity addEntity(IEntity doaEntity) throws GeneralDOAException {
        if (!(doaEntity instanceof INeoObject)) {
            throw new GeneralDOAException("!");
        }
        if (hasEntity(doaEntity.getName())) {
            throw new GeneralDOAException(MessageFormat.format(
                    "entity with name [{0}] already exists in this container!",
                    doaEntity.getName()));
        }

        final INeoObject neoObject = (INeoObject) doaEntity;
        neo.getReferenceNode().createRelationshipTo(neoObject.getNode(),
                DOARelationship.HAS_ENTITY);
        return doaEntity;

    }

    @Override
    public boolean hasEntity(final String entityName) {
        IEntityEvaluator evaluator = new IEntityEvaluator() {

            @Override
            public boolean isReturnableEntity(IEntity entity) {
                return entityName.equals(entity.getName());
            }
        };
        IEntity existingEntity =
                lookupEntityFromLocation("/", evaluator, false);
        return existingEntity != null;
    }

    @Override
    public void iterateEntities(IEntitiesIterator iterator,
                                IEntityEvaluator evaluator) throws GeneralDOAException {
        Traverser traverser =
                Traversal
                        .description()
                        .evaluator(Evaluators.excludeStartPosition())
                        .breadthFirst()
                        .evaluator(
                                new NeoReturnableEvaluator(doa, evaluator,
                                        false))
                        .evaluator(Evaluators.atDepth(1))
                        .relationships(DOARelationship.HAS_ENTITY,
                                Direction.OUTGOING)
                        .traverse(neo.getReferenceNode());
        Iterable<IEntity> entities =
                new EntitiesListIterable(doa, traverser.iterator());
        for (IEntity entity : entities) {
            iterator.next(entity);
        }
    }

    @Override
    public boolean needsTreeInitialization() {
        if (!neo.getReferenceNode().hasProperty(NeoEntityDelegator.PROP_NAME)) {
            doa.doInTransaction(new ITransactionCallback() {

                @Override
                public Object performOperation() throws Exception {
                    neo.getReferenceNode().setProperty(
                            NeoEntityDelegator.PROP_NAME, "/");
                    return null;
                }
            });
            return true;
        }
        return false;
    }

}

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

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.Traverser.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.artifact.IArtifact;
import pl.doa.artifact.impl.neo.NeoArtifact;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityAttribute;
import pl.doa.entity.event.IEntityEventListener;
import pl.doa.entity.event.impl.neo.NeoEntityEventListener;
import pl.doa.entity.impl.AbstractEntity;
import pl.doa.entity.impl.neo.NeoEntityAttribute;
import pl.doa.neo.NodeDelegate;
import pl.doa.relation.DOARelationship;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * @author activey
 */

public class NeoEntityDelegator extends AbstractEntity implements
        INeoObject {

    public final static String PROP_NAME = "name";
    private final static Logger log = LoggerFactory
            .getLogger(NeoEntityDelegator.class);
    private final NodeDelegate delegator;

    public NeoEntityDelegator(IDOA doa, Node node) {
        super(doa);
        this.delegator = new NodeDelegate(node);
    }

    public NeoEntityDelegator(IDOA doa, GraphDatabaseService neo,
            String className) {
        super(doa);
        this.delegator = new NodeDelegate(neo, className);

    }

    public NeoEntityDelegator(IDOA doa, GraphDatabaseService neo,
            String className, IEntity ancestor) {
        super(doa);
        this.delegator = new NodeDelegate(neo, className);
        INeoObject neoEntity = (INeoObject) ancestor;
        delegator.createRelationshipTo(neoEntity.getNode(), DOARelationship.HAS_ANCESTOR);
    }

    public final static String getName(Node nodeDelegate) {
        return (String) nodeDelegate.getProperty(PROP_NAME);
    }

    public final static IEntity createEntityInstance(IDOA doa, Node node) {
        if (node == null) {
            return null;
        }
        if (!node.hasProperty(NodeDelegate.PROP_CLASS_NAME)) {
            /*
             * log.debug(MessageFormat.format(
			 * "node className is null, name = {0}", node.hasProperty(PROP_NAME)
			 * ? node.getProperty(PROP_NAME) : null));
			 */
            return null;
        }
        String className = (String) node.getProperty(NodeDelegate.PROP_CLASS_NAME);
        Class<? extends NeoEntityDelegator> clazz;
        try {

            clazz = (Class<? extends NeoEntityDelegator>) Thread
                    .currentThread().getContextClassLoader()
                    .loadClass(className);
        } catch (Throwable t) {
            log.error("", t);
            return null;
        }
        if (!IEntity.class.isAssignableFrom(clazz)) {
            return null;
        }
        try {
            Constructor<? extends NeoEntityDelegator> constructor = clazz
                    .getConstructor(IDOA.class, Node.class);
            return constructor.newInstance(doa, node);
        } catch (Throwable e) {
            log.error("", e);
            return null;
        }
    }

    @Override
    protected long getIdImpl() {
        return delegator.getId();
    }

    public final String getNameImpl() {
        return (String) delegator.getProperty(PROP_NAME);
    }

    public final void setNameImpl(String name) {
        delegator.setProperty(PROP_NAME, name);
    }

    /**
     * Metoda setContainer zapewnia ze moze istniec najwyzej jeden parent.
     */
    public final IEntitiesContainer getContainerImpl() {
        Traverser traverser = delegator.traverse(Order.DEPTH_FIRST,
                StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                if (currentPos.isStartNode()) {
                    return false;
                }
                return true;
            }
        }, DOARelationship.HAS_ENTITY, Direction.INCOMING);
        for (Node node : traverser) {
            if ("/".equals(node.getProperty("name"))) {
                return getDoa();
            }
            return (IEntitiesContainer) NeoEntityDelegator.createEntityInstance(getDoa(), node);
        }
        return null;
    }

    public final void setContainerImpl(IEntitiesContainer container) {
        if (container == null) {
            return;
        }
        if (delegator.hasRelationship(DOARelationship.HAS_ENTITY, Direction.INCOMING)) {
            Relationship containerRel = delegator.getSingleRelationship(
                    DOARelationship.HAS_ENTITY, Direction.INCOMING);
            Node containerNode = containerRel.getStartNode();
            IEntitiesContainer exisingContainer = (IEntitiesContainer) NeoEntityDelegator
                    .createEntityInstance(getDoa(), containerNode);
            if (!exisingContainer.equals(container)) {
                containerRel.delete();
            } else {
                return;
            }
        }
        try {
            for (Relationship rel : delegator.getRelationships(
                    DOARelationship.HAS_ENTITY, Direction.INCOMING)) {
                rel.delete();
            }
            container.addEntity(this);
        } catch (GeneralDOAException e) {
            log.error("", e);
        }
    }

    public final String getLocationImpl() {
        Traverser traverser = delegator.traverse(Order.BREADTH_FIRST,
                StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {

            @Override
            public final boolean isReturnableNode(
                    TraversalPosition traversalPosition) {
                Node node = traversalPosition.currentNode();
                return node.hasRelationship(DOARelationship.HAS_ENTITY,
                        Direction.INCOMING);
            }

        }, DOARelationship.HAS_ENTITY, Direction.INCOMING);
        String location = "";
        for (Node node : traverser) {
            location = "/" + node.getProperty(NeoEntityDelegator.PROP_NAME)
                    + location;
        }
        return location;
    }

    @Override
    public boolean hasAttributesImpl() {
        return delegator.hasRelationship(Direction.OUTGOING,
                DOARelationship.HAS_ATTRIBUTE);
    }

    public final Collection<String> getAttributeNamesImpl() {
        List<String> names = new ArrayList<String>();
        Traverser fieldsNodesTraverser = delegator.traverse(Order.BREADTH_FIRST,
                StopEvaluator.DEPTH_ONE,
                ReturnableEvaluator.ALL_BUT_START_NODE,
                DOARelationship.HAS_ATTRIBUTE, Direction.OUTGOING);
        for (Node node : fieldsNodesTraverser) {
            names.add((String) node.getProperty(NeoEntityAttribute.PROP_NAME));
        }
        return names;
    }

    /**
     * Metoda zwraca wartosc atrybutu.
     *
     * @param attrName Nazwa atrybutu.
     * @return Wartosc atrybutu.
     */
    public final String getAttributeImpl(final String attrName) {
        Node foundNode = delegator.lookupForNode(DOARelationship.HAS_ATTRIBUTE,
                new ReturnableEvaluator() {

                    @Override
                    public final boolean isReturnableNode(
                            TraversalPosition position) {
                        Node currentNode = position.currentNode();
                        return attrName.equals(currentNode
                                .getProperty(PROP_NAME));
                    }

                });
        if (foundNode == null) {
            return null;
        }
        return (String) new NeoEntityAttribute(foundNode).getValue();
    }

    /**
     * Method returns attribute object. Used in deploy/undeploy artifact.
     *
     * @param attrName attribute name
     * @return DOAEntityAttribute with given name
     */
    public final IEntityAttribute getAttributeObjectImpl(final String attrName) {
        Node foundNode = delegator.lookupForNode(DOARelationship.HAS_ATTRIBUTE,
                new ReturnableEvaluator() {

                    @Override
                    public final boolean isReturnableNode(
                            TraversalPosition position) {
                        Node currentNode = position.currentNode();
                        return attrName.equals(currentNode
                                .getProperty(PROP_NAME));
                    }

                });
        return new NeoEntityAttribute(foundNode);
    }

    /**
     * Metoda dodaje nowy atrybut lub zmienia wartosc istniejacego,
     *
     * @param attrName  Nazwa atrybutu.
     * @param attrValue Wartosc atrybutu.
     */
    public final void setAttributeImpl(String attrName, String attrValue) {
        NeoEntityAttribute newAttr = new NeoEntityAttribute(delegator.getGraphDatabase()
                .createNode());
        newAttr.setName(attrName);
        newAttr.setValue(attrValue);
        delegator.createRelationshipTo(newAttr, DOARelationship.HAS_ATTRIBUTE);
    }

    public final void setAttributeImpl(IEntityAttribute attributte) {
        delegator.createRelationshipTo((Node) attributte, DOARelationship.HAS_ATTRIBUTE);
    }

    public final void removeAttributesImpl() {
        for (Relationship relation : delegator.getRelationships(
                DOARelationship.HAS_ATTRIBUTE, Direction.OUTGOING)) {
            Node node = relation.getEndNode();
            relation.delete();
            if (node.hasRelationship()) {
                for (Relationship rel : node.getRelationships()) {
                    rel.delete();
                }
            }
            node.delete();
        }
    }

    public final boolean removeImpl(boolean forceRemoveContents) {
        Iterable<Relationship> relations = delegator.getRelationships();
        for (Relationship relation : relations) {
            relation.delete();
        }
        delegator.delete();
        return true;
    }

    public final boolean removeImpl() {
        return remove(false);
    }

    public final boolean isStoredImpl() {
        boolean relation = delegator.hasRelationship(DOARelationship.HAS_ENTITY,
                Direction.INCOMING);
        return relation && (this.getName() != null);
    }

    public final IEntity storeImpl(String location) throws GeneralDOAException {
        if (getDoa() == null) {
            throw new GeneralDOAException("DOA is null! set it first!");
        }
        return getDoa().store(location, this);
    }

    /**
     * laczy nowo dodane elementy drzewa z istniejacym modulem (w przypadku gdy nadpisywany jest ten sam modul)
     *
     * @param node
     * @param module
     */
    private void spreadModule(Node node, Node module) {
        for (Relationship rel : node.getRelationships(
                DOARelationship.HAS_ENTITY, Direction.OUTGOING)) {
            module.createRelationshipTo(rel.getEndNode(),
                    DOARelationship.HAS_ARTIFACT_ENTITY);
            spreadModule(rel.getEndNode(), module);
        }
    }

    public final boolean hasEventListenersImpl() {
        return delegator.hasRelationship(DOARelationship.HAS_LISTENER_EVENT_SOURCE,
                Direction.INCOMING);
    }

    public final List<IEntityEventListener> getEventListenersImpl() {
        if (!delegator.hasRelationship(DOARelationship.HAS_LISTENER_EVENT_SOURCE,
                Direction.INCOMING)) {
            return null;
        }
        List<IEntityEventListener> listeners = new ArrayList<IEntityEventListener>();
        Traverser traverser = delegator.traverse(Traverser.Order.BREADTH_FIRST,
                StopEvaluator.DEPTH_ONE,
                ReturnableEvaluator.ALL_BUT_START_NODE,
                DOARelationship.HAS_LISTENER_EVENT_SOURCE, Direction.INCOMING);
        for (Node node : traverser) {
            listeners.add(new NeoEntityEventListener(getDoa(), node));
        }
        return listeners;
    }

    public final IEntityEventListener getAwaitedEventListener() {
        if (!delegator.hasRelationship(DOARelationship.HAS_EVENT_RECEIVER,
                Direction.INCOMING)) {
            return null;
        }

        IEntityEventListener listener = null;

        Traverser traverser = delegator.traverse(Traverser.Order.BREADTH_FIRST,
                StopEvaluator.DEPTH_ONE,
                ReturnableEvaluator.ALL_BUT_START_NODE,
                DOARelationship.HAS_EVENT_RECEIVER, Direction.INCOMING);

        for (Node node : traverser) {
            listener = new NeoEntityEventListener(getDoa(), node);
            break;
        }

        return listener;
    }

    public final boolean isPublicImpl() {
        return !delegator.hasRelationship(DOARelationship.HAS_ENTITY, Direction.INCOMING);
    }

    public final IArtifact getArtifactImpl() {
        if (!delegator.hasRelationship(DOARelationship.HAS_ARTIFACT_ENTITY,
                Direction.INCOMING)) {
            return null;
        }
        Relationship relation = delegator.getSingleRelationship(
                DOARelationship.HAS_ARTIFACT_ENTITY, Direction.INCOMING);
        Node node = relation.getStartNode();
        return new NeoArtifact(getDoa(), node);
    }

    @Override
    protected final Date getLastModifiedImpl() {
        return delegator.getLastModified();
    }

    @Override
    protected final Date getCreatedImpl() {
        return delegator.getCreated();
    }

    @Override
    protected final void setAttributesImpl(Map<String, String> attributes) {
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            setAttribute(entry.getKey(), entry.getValue());
        }
    }

    protected final IEntity getAncestorImpl() {
        if (!delegator.hasRelationship(DOARelationship.HAS_ANCESTOR, Direction.OUTGOING)) {
            return null;
        }
        return NeoEntityDelegator.createEntityInstance(
                getDoa(), delegator.getSingleRelationship(DOARelationship.HAS_ANCESTOR,
                Direction.OUTGOING).getEndNode());
    }

    @Override
    public final Node getNode() {
        return delegator;
    }

    public Node lookupForNode(RelationshipType relationshipType, ReturnableEvaluator returnableEvaluator) {
        return delegator.lookupForNode(relationshipType, returnableEvaluator);
    }

    public boolean hasNode(RelationshipType relationshipType, ReturnableEvaluator returnableEvaluator) {
        return delegator.hasNode(relationshipType, returnableEvaluator);
    }
}
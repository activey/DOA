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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.artifact.IArtifact;
import pl.doa.artifact.impl.neo.NeoArtifact;
import pl.doa.container.IEntitiesContainer;
import pl.doa.container.impl.neo.NeoEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityAttachRule;
import pl.doa.entity.IEntityAttribute;
import pl.doa.entity.event.IEntityEventListener;
import pl.doa.entity.event.impl.neo.NeoEntityEventListener;
import pl.doa.entity.impl.AbstractEntity;
import pl.doa.entity.impl.neo.NeoEntityAttribute;
import pl.doa.neo.NodeDelegate;
import pl.doa.relation.DOARelationship;
import pl.doa.renderer.IRenderer;
import pl.doa.resource.IStaticResource;

/**
 * @author activey
 * 
 */

public class NeoEntityDelegator extends NodeDelegate implements IEntity,
		INeoObject {

	private final static Logger log = LoggerFactory
			.getLogger(NeoEntityDelegator.class);

	public final static String PROP_NAME = "name";

	protected IDOA doa;

	public NeoEntityDelegator(IDOA doa, Node node) {
		super(node);
		if (doa != null) {
			this.doa = doa;
		}
	}

	public NeoEntityDelegator(IDOA doa, GraphDatabaseService neo,
			String className) {
		super(neo, className);
		if (doa != null) {
			this.doa = doa;
		}
	}

	public NeoEntityDelegator(IDOA doa, GraphDatabaseService neo,
			String className, IEntity ancestor) {
		super(neo, className);
		if (doa != null) {
			this.doa = doa;
		}
		INeoObject neoEntity = (INeoObject) ancestor;
		createRelationshipTo(neoEntity.getNode(), DOARelationship.HAS_ANCESTOR);
	}

	@Override
	public final boolean equals(IEntity entity) {
		if (entity == null) {
			return false;
		}
		if (this.getId() == entity.getId()) {
			return true;
		}
		return false;
	}

	public final IDOA getDoa() {
		return doa;
	}

	public final String getName() {
		return (String) getProperty(PROP_NAME);
	}

	public final static String getName(Node nodeDelegate) {
		return (String) nodeDelegate.getProperty(PROP_NAME);
	}

	public final void setName(String name) {
		setProperty(PROP_NAME, name);
	}

	/**
	 * Metoda setContainer zapewnia ze moze istniec najwyzej jeden parent.
	 */
	public final IEntitiesContainer getContainer() {
		Traverser traverser = this.traverse(Order.DEPTH_FIRST,
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
				return doa;
			}
			return (IEntitiesContainer) NeoEntityDelegator
					.createEntityInstance(doa, node);
		}
		return null;
	}

	public final String getLocation() {
		Traverser traverser = this.traverse(Order.BREADTH_FIRST,
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
	public boolean hasAttributes() {
		return hasRelationship(Direction.OUTGOING,
				DOARelationship.HAS_ATTRIBUTE);
	}

	public final List<String> getAttributeNames() {
		List<String> names = new ArrayList<String>();
		Traverser fieldsNodesTraverser = traverse(Order.BREADTH_FIRST,
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
	 * @param attrName
	 *            Nazwa atrybutu.
	 * @return Wartosc atrybutu.
	 */
	public final String getAttribute(final String attrName) {
		Node foundNode = lookupForNode(DOARelationship.HAS_ATTRIBUTE,
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

	public final String getAttribute(String attrName, String defaultValue) {
		String value = getAttribute(attrName);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	/**
	 * Method returns attribute object. Used in deploy/undeploy artifact.
	 * 
	 * @param attrName
	 *            attribute name
	 * @return DOAEntityAttribute with given name
	 */
	public final IEntityAttribute getAttributeObject(final String attrName) {
		Node foundNode = lookupForNode(DOARelationship.HAS_ATTRIBUTE,
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
	 * @param attrName
	 *            Nazwa atrybutu.
	 * @param attrValue
	 *            Wartosc atrybutu.
	 */
	public final void setAttribute(String attrName, String attrValue) {
		NeoEntityAttribute newAttr = new NeoEntityAttribute(getGraphDatabase()
				.createNode());
		newAttr.setName(attrName);
		newAttr.setValue(attrValue);
		createRelationshipTo(newAttr, DOARelationship.HAS_ATTRIBUTE);
	}

	public final void setAttribute(IEntityAttribute attributte) {
		createRelationshipTo((Node) attributte, DOARelationship.HAS_ATTRIBUTE);
	}

	public final void removeAttributes() {
		for (Relationship relation : this.getRelationships(
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

	public final boolean remove(boolean forceRemoveContents) {
		removeAttributes();
		Iterable<Relationship> relations = this.getRelationships();
		for (Relationship relation : relations) {
			relation.delete();
		}
		this.delete();
		return true;
	}

	public final boolean remove() {
		return remove(false);
	}

	public final boolean isStored() {
		boolean relation = hasRelationship(DOARelationship.HAS_ENTITY,
				Direction.INCOMING);
		return relation && (this.getName() != null);
	}

	public final IEntity store(String location) throws GeneralDOAException {
		if (doa == null) {
			throw new GeneralDOAException("DOA is null! set it first!");
		}
		return doa.store(location, this);
	}

	public final void setContainer(IEntitiesContainer container) {
		if (container == null) {
			return;
		}
		if (hasRelationship(DOARelationship.HAS_ENTITY, Direction.INCOMING)) {
			Relationship containerRel = getSingleRelationship(
					DOARelationship.HAS_ENTITY, Direction.INCOMING);
			Node containerNode = containerRel.getStartNode();
			IEntitiesContainer exisingContainer = (IEntitiesContainer) NeoEntityDelegator
					.createEntityInstance(doa, containerNode);
			if (!exisingContainer.equals(container)) {
				containerRel.delete();
			} else {
				return;
			}
		}
		try {
			for (Relationship rel : this.getRelationships(
					DOARelationship.HAS_ENTITY, Direction.INCOMING)) {
				rel.delete();
			}
			container.addEntity(this);
		} catch (GeneralDOAException e) {
			log.error("", e);
		}
	}

	/**
	 * laczy nowo dodane elementy drzewa z istniejacym modulem (w przypadku gdy
	 * nadpisywany jest ten sam modul)
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

	public final static IEntity createEntityInstance(IDOA doa, Node node) {
		if (node == null) {
			return null;
		}
		if (!node.hasProperty(PROP_CLASS_NAME)) {
			/*
			 * log.debug(MessageFormat.format(
			 * "node className is null, name = {0}", node.hasProperty(PROP_NAME)
			 * ? node.getProperty(PROP_NAME) : null));
			 */
			return null;
		}
		String className = (String) node.getProperty(PROP_CLASS_NAME);
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

	public void copyRelations(NeoEntityDelegator entity) {
		for (Relationship rel : entity.getRelationships(
				DOARelationship.HAS_ENTITY_REFERENCE, Direction.INCOMING)) {
			Node startNode = rel.getStartNode();
			startNode.createRelationshipTo(this, rel.getType());
			rel.delete();
		}
	}

	public final boolean hasEventListeners() {
		return hasRelationship(DOARelationship.HAS_LISTENER_EVENT_SOURCE,
				Direction.INCOMING);
	}

	public final List<IEntityEventListener> getEventListeners() {
		if (!hasRelationship(DOARelationship.HAS_LISTENER_EVENT_SOURCE,
				Direction.INCOMING)) {
			return null;
		}
		List<IEntityEventListener> listeners = new ArrayList<IEntityEventListener>();
		Traverser traverser = traverse(Traverser.Order.BREADTH_FIRST,
				StopEvaluator.DEPTH_ONE,
				ReturnableEvaluator.ALL_BUT_START_NODE,
				DOARelationship.HAS_LISTENER_EVENT_SOURCE, Direction.INCOMING);
		for (Node node : traverser) {
			listeners.add(new NeoEntityEventListener(doa, node));
		}
		return listeners;
	}

	public final IEntityEventListener getAwaitedEventListener() {
		if (!hasRelationship(DOARelationship.HAS_EVENT_RECEIVER,
				Direction.INCOMING)) {
			return null;
		}

		IEntityEventListener listener = null;

		Traverser traverser = traverse(Traverser.Order.BREADTH_FIRST,
				StopEvaluator.DEPTH_ONE,
				ReturnableEvaluator.ALL_BUT_START_NODE,
				DOARelationship.HAS_EVENT_RECEIVER, Direction.INCOMING);

		for (Node node : traverser) {
			listener = new NeoEntityEventListener(doa, node);
			break;
		}

		return listener;
	}

	public final boolean isPublic() {
		return !hasRelationship(DOARelationship.HAS_ENTITY, Direction.INCOMING);
	}

	public final IArtifact getArtifact() {
		if (!hasRelationship(DOARelationship.HAS_ARTIFACT_ENTITY,
				Direction.INCOMING)) {
			return null;
		}
		Relationship relation = getSingleRelationship(
				DOARelationship.HAS_ARTIFACT_ENTITY, Direction.INCOMING);
		Node node = relation.getStartNode();
		return new NeoArtifact(doa, node);
	}

	public final IEntity redeploy(IEntity newEntity) throws GeneralDOAException {
		if (!(newEntity instanceof INeoObject)) {
			throw new GeneralDOAException("wrong entity type!");
		}
		NeoEntityDelegator neoEntity = (NeoEntityDelegator) ((INeoObject) newEntity)
				.getNode();
		Relationship artifactRel = neoEntity
				.getRelationships(DOARelationship.HAS_ARTIFACT_ENTITY,
						Direction.INCOMING).iterator().next();
		if (artifactRel != null) {
			Node artifactNode = artifactRel.getStartNode();
			if (artifactNode != null) {
				artifactNode.createRelationshipTo(this,
						DOARelationship.HAS_ARTIFACT_ENTITY);
			}
		}
		return redeployInternal(newEntity);
	}

	/**
	 * 
	 * przypiecie starego entity do artefaktu, zmiana nazwy starego entity,
	 * wrzucenie nowego
	 * 
	 * @param neoEntity
	 * @return
	 * @throws GeneralDOAException
	 */
	protected IEntity redeployInternal(IEntity neoEntity)
			throws GeneralDOAException {
		NeoEntityDelegator newEntity = (NeoEntityDelegator) ((INeoObject) neoEntity)
				.getNode();
		if (!newEntity.getClass().equals(this.getClass())) {
			log.error("Wrong class type while redeploying"
					+ newEntity.getName());
			throw new GeneralDOAException("Wrong class type while redeploying"
					+ newEntity.getName());
		}
		log.debug("Reploying entity: " + this.getName());
		// tylko Container jest traktowany inaczej
		if (NeoEntitiesContainer.class.getName().equals(
				newEntity.getProperty(PROP_CLASS_NAME))) {
			this.removeAttributes();
			for (String attrName : newEntity.getAttributeNames()) {
				this.setAttribute(attrName, newEntity.getAttribute(attrName));
			}
			return this;
		}
		String version = "" + new Date().getTime();
		this.setName(this.getName() + version);
		if (newEntity.hasRelationship(DOARelationship.HAS_ENTITY,
				Direction.INCOMING)) {
			for (Relationship rels : newEntity.getRelationships(
					DOARelationship.HAS_ENTITY, Direction.INCOMING)) {
				rels.delete();
			}
		}
		doa.store(getContainer().getLocation(), newEntity);
		return neoEntity;
	}

	@Override
	public final void setAttributes(Map<String, String> attributes) {
		for (Map.Entry<String, String> entry : attributes.entrySet()) {
			setAttribute(entry.getKey(), entry.getValue());
		}
	}

	public final IEntity getAncestor() {
		if (!hasRelationship(DOARelationship.HAS_ANCESTOR, Direction.OUTGOING)) {
			return null;
		}
		return NeoEntityDelegator.createEntityInstance(
				doa,
				getSingleRelationship(DOARelationship.HAS_ANCESTOR,
						Direction.OUTGOING).getEndNode());
	}

	@Override
	public boolean isInside(IEntitiesContainer container) {
		return false;
	}

	@Override
	public NeoEntityDelegator getNode() {
		return this;
	}

	@Override
	public boolean isDescendantOf(IEntity ancestor) {
		// TODO !!!
		return false;
	}

	@Override
	public <T extends IEntity> T attach(IEntityAttachRule<T> rule) {
		return null;
	}

	@Override
	public IStaticResource render(String mimeType) throws GeneralDOAException {
		return AbstractEntity.render(this, mimeType, doa, null);
	}

	@Override
	public IStaticResource render(IRenderer renderer)
			throws GeneralDOAException {
		return AbstractEntity.render(this, renderer, null);
	}

	@Override
	public IStaticResource render(String mimeType, IStaticResource template)
			throws GeneralDOAException {
		return AbstractEntity.render(this, mimeType, doa, null);
	}

	@Override
	public IStaticResource render(IRenderer renderer, IStaticResource template)
			throws GeneralDOAException {
		return AbstractEntity.render(this, renderer, template);
	}

}
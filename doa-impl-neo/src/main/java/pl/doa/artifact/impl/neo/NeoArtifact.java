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
package pl.doa.artifact.impl.neo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.INeoObject;
import pl.doa.NeoEntityDelegator;
import pl.doa.artifact.IArtifact;
import pl.doa.artifact.impl.AbstractArtifact;
import pl.doa.container.IEntitiesContainer;
import pl.doa.container.impl.neo.NeoEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityAttribute;
import pl.doa.entity.event.IEntityEventListener;
import pl.doa.relation.DOARelationship;
import pl.doa.resource.IStaticResource;
import pl.doa.resource.impl.neo.NeoStaticResource;

public class NeoArtifact extends AbstractArtifact implements INeoObject,
		Serializable {

	private final static Logger log = LoggerFactory
			.getLogger(NeoArtifact.class);

	public static final String PROP_VERSION = "version";
	public static final String PROP_DESCRIPTION = "description";
	public static final String PROP_ARTIFACT_ID = "artifactId";
	public static final String PROP_GROUP_ID = "groupId";
	public static final String PROP_ARTIFACT_FILE = "artifactFile";
	public static final String PROP_TYPE = "type";
	public static final String OLD_ARTIFACT = "jar";

	private NeoEntityDelegator delegator;

	public NeoArtifact(IDOA doa, Node underlyingNode) {
		super(doa);
		this.delegator = new NeoEntityDelegator(doa, underlyingNode);
	}

	public NeoArtifact(IDOA doa, GraphDatabaseService neo, String name) {
		super(doa);
		this.delegator =
				new NeoEntityDelegator(doa, neo, this.getClass().getName());
		setName(name);
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.artifact.impl.neo.IArtifact#setGroupId(java.lang.String)
	 */
	@Override
	public void setGroupId(String groupId) {
		if (groupId == null) {
			if (delegator.hasProperty(PROP_GROUP_ID)) {
				delegator.removeProperty(PROP_GROUP_ID);
			}
			return;
		}
		delegator.setProperty(PROP_GROUP_ID, groupId);
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.artifact.impl.neo.IArtifact#getGroupId()
	 */
	@Override
	public String getGroupId() {
		return (String) delegator.getProperty(PROP_GROUP_ID);
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.artifact.impl.neo.IArtifact#setArtifactId(java.lang.String)
	 */
	@Override
	public void setArtifactId(String artifactId) {
		if (artifactId == null) {
			if (delegator.hasProperty(PROP_ARTIFACT_ID)) {
				delegator.removeProperty(PROP_ARTIFACT_ID);
			}
			return;
		}
		delegator.setProperty(PROP_ARTIFACT_ID, artifactId);
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.artifact.impl.neo.IArtifact#getArtifactId()
	 */
	@Override
	public String getArtifactId() {
		return (String) delegator.getProperty(PROP_ARTIFACT_ID);
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.artifact.impl.neo.IArtifact#getVersion()
	 */
	@Override
	public String getVersion() {
		return (String) delegator.getProperty(PROP_VERSION);
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.artifact.impl.neo.IArtifact#setVersion(java.lang.String)
	 */
	@Override
	public void setVersion(String version) {
		if (version == null) {
			if (delegator.hasProperty(PROP_VERSION)) {
				delegator.removeProperty(PROP_VERSION);
			}
			return;
		}
		delegator.setProperty(PROP_VERSION, version);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * pl.doa.artifact.impl.neo.IArtifact#setArtifactFileName(java.lang.String)
	 */
	@Override
	public void setArtifactFileName(String fileName) {
		if (fileName == null) {
			if (delegator.hasProperty(PROP_ARTIFACT_FILE)) {
				delegator.removeProperty(PROP_ARTIFACT_FILE);
			}
			return;
		}
		delegator.setProperty(PROP_ARTIFACT_FILE, fileName);
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.artifact.impl.neo.IArtifact#getArtifactFileName()
	 */
	@Override
	public String getArtifactFileName() {
		return (String) delegator.getProperty(PROP_ARTIFACT_FILE);
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.artifact.impl.neo.IArtifact#setType(java.lang.String)
	 */
	@Override
	public void setType(IArtifact.Type type) {
		if (type == null) {
			if (delegator.hasProperty(PROP_TYPE)) {
				delegator.removeProperty(PROP_TYPE);
			}
			return;
		}
		delegator.setProperty(PROP_TYPE, type.name());
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.artifact.impl.neo.IArtifact#getType()
	 */
	@Override
	public IArtifact.Type getType() {
		String type = (String) delegator.getProperty(PROP_TYPE);
		return IArtifact.Type.valueOf(type);
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.artifact.impl.neo.IArtifact#getDescription()
	 */
	@Override
	public String getDescription() {
		return (String) delegator.getProperty(PROP_DESCRIPTION);
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.artifact.impl.neo.IArtifact#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(String description) {
		if (description == null) {
			if (delegator.hasProperty(PROP_DESCRIPTION)) {
				delegator.removeProperty(PROP_DESCRIPTION);
			}
			return;
		}
		delegator.setProperty(PROP_DESCRIPTION, description);
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.artifact.impl.neo.IArtifact#getArtifactResource()
	 */
	@Override
	public IStaticResource getArtifactResource() {
		if (!delegator.hasRelationship(DOARelationship.HAS_ARTIFACT_RESOURCE,
				Direction.OUTGOING)) {
			return null;
		}
		Relationship hasFileRelation =
				delegator.getSingleRelationship(
						DOARelationship.HAS_ARTIFACT_RESOURCE,
						Direction.OUTGOING);
		Node resourceNode = hasFileRelation.getEndNode();
		return new NeoStaticResource(doa, resourceNode);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * pl.doa.artifact.impl.neo.IArtifact#registerEntity(pl.doa.entity.impl.
	 * neo.NeoEntity)
	 */
	@Override
	protected void registerEntityImpl(IEntity entity) {
		INeoObject neoEntity = (INeoObject) entity;
		delegator.createRelationshipTo(neoEntity.getNode(),
				DOARelationship.HAS_ARTIFACT_ENTITY);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * pl.doa.artifact.impl.neo.IArtifact#registerNode(org.neo4j.graphdb.Node)
	 */
	public void registerNode(Node node) {
		delegator.createRelationshipTo(node,
				DOARelationship.HAS_ARTIFACT_ENTITY);
	}

	// public void createExistedEntities() {
	// existedEnitities = new ArrayList<String>();
	// for (Relationship relation : this.getRelationships(
	// DOARelationship.HAS_ARTIFACT_ENTITY, Direction.OUTGOING)) {
	// if (((String) relation.getEndNode().getProperty(
	// NeoEntity.PROP_CLASS_NAME))
	// .compareTo(NeoEntityAttribute.class.getCanonicalName()) != 0) {
	// existedEnitities.add(NeoEntity.createEntityInstance(doa,
	// relation.getEndNode()).getLocation());
	// }
	// }
	// }

	/*
	 * (non-Javadoc)
	 * @see
	 * pl.doa.artifact.impl.neo.IArtifact#removeExistedEntity(java.lang.String)
	 */
	// @Override
	// public void removeExistedEntity(String name) {
	// if (this.existedEnitities.contains(name)) {
	// this.existedEnitities.remove(this.existedEnitities.indexOf(name));
	// }
	// }

	// public boolean validate(NeoArtifact newArtifact) {
	// if (this.existedEnitities == null || this.existedEnitities.size() == 0) {
	// // delete relations with not added module
	// for (Relationship rel : newArtifact.getRelationships(
	// DOARelationship.HAS_ARTIFACT_ENTITY, Direction.OUTGOING)) {
	// rel.delete();
	// }
	// return true;
	// }
	// for (String entityLocation : existedEnitities) {
	// NeoEntity entity = (NeoEntity)
	// doa.lookupEntityByLocation(entityLocation);
	// if (entity == null) {
	// return false;
	// }
	// entity.remove();
	// this.existedEnitities.set(
	// this.existedEnitities.indexOf(entityLocation), null);
	// }
	// for (String entityLocation : existedEnitities) {
	// if (entityLocation != null) {
	// return false;
	// }
	// }
	// // delete relations with not added module
	// for (Relationship rel : newArtifact.getRelationships(
	// DOARelationship.HAS_ARTIFACT_ENTITY, Direction.OUTGOING)) {
	// rel.delete();
	// }
	// return true;
	// }

	/*
	 * (non-Javadoc)
	 * @see
	 * pl.doa.artifact.impl.neo.IArtifact#setArtifactResource(pl.doa.resource
	 * .DOAStaticResource)
	 */
	@Override
	public void setArtifactResource(IStaticResource resource) {
		if (resource == null) {
			if (delegator.hasRelationship(
					DOARelationship.HAS_ARTIFACT_RESOURCE, Direction.OUTGOING)) {
				Relationship relation =
						delegator.getSingleRelationship(
								DOARelationship.HAS_ARTIFACT_RESOURCE,
								Direction.OUTGOING);
				relation.delete();
				return;
			}
			return;
		}
		delegator.createRelationshipTo(((INeoObject) resource).getNode(),
				DOARelationship.HAS_ARTIFACT_RESOURCE);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * pl.doa.artifact.impl.neo.IArtifact#setArtifactResourceStream(java.io.
	 * InputStream)
	 */
	@Override
	public void setArtifactResourceStream(InputStream fileStream,
			long contentSize) throws GeneralDOAException {
		StringBuffer mimeType = new StringBuffer();
		switch (getType()) {
		case XML:
			mimeType.append("text/xml");
			break;
		case JAR:
			mimeType.append("application/x-jar");
			break;
		default:
			break;
		}
		IStaticResource resource;
		try {
			resource =
					doa.createStaticResource(getName() + "."
							+ getType().name().toLowerCase(),
							mimeType.toString(), getContainer());
		} catch (GeneralDOAException e) {
			log.error("", e);
			return;
		}
		resource.setContentFromStream(fileStream, contentSize);
		setArtifactResource(resource);
	}

	@Override
	public void setArtifactResourceBytes(byte[] fileContent)
			throws GeneralDOAException {
		setArtifactResourceStream(new ByteArrayInputStream(fileContent),
				fileContent.length);
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.artifact.impl.neo.IArtifact#getArtifactFileStream()
	 */
	@Override
	public InputStream getArtifactFileStream() throws GeneralDOAException {
		return getArtifactResource().getContentStream();
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.artifact.impl.neo.IArtifact#setDependencies(java.util.List)
	 */
	@Override
	public void setDependencies(List<IArtifact> dependendArtifacts) {
		for (IArtifact artifact : dependendArtifacts) {
			addDependency(artifact);
		}
	}

	private void addDependency(IArtifact artifact) {
		INeoObject neoEntity = (INeoObject) artifact;
		delegator.createRelationshipTo(neoEntity.getNode(),
				DOARelationship.HAS_ARTIFACT_DEPENDENCY);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * pl.doa.artifact.impl.neo.IArtifact#setBaseContainer(pl.doa.container.
	 * impl.neo.NeoEntitiesContainer)
	 */
	@Override
	public void setBaseContainer(IEntitiesContainer container) {
		INeoObject neoEntity = (INeoObject) container;
		if (container == null) {
			if (delegator.hasRelationship(
					DOARelationship.HAS_ARTIFACT_BASE_CONTAINER,
					Direction.OUTGOING)) {
				Relationship relation =
						delegator.getSingleRelationship(
								DOARelationship.HAS_ARTIFACT_BASE_CONTAINER,
								Direction.OUTGOING);
				relation.delete();
				return;
			}
			return;
		}
		delegator.createRelationshipTo(neoEntity.getNode(),
				DOARelationship.HAS_ARTIFACT_BASE_CONTAINER);
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.artifact.impl.neo.IArtifact#getBaseContainer()
	 */
	@Override
	public IEntitiesContainer getBaseContainer() {
		if (!delegator
				.hasRelationship(DOARelationship.HAS_ARTIFACT_BASE_CONTAINER,
						Direction.OUTGOING)) {
			return null;
		}
		Relationship hasBaseContainerRelation =
				delegator.getSingleRelationship(
						DOARelationship.HAS_ARTIFACT_BASE_CONTAINER,
						Direction.OUTGOING);
		Node conainerNode = hasBaseContainerRelation.getEndNode();
		return new NeoEntitiesContainer(doa, conainerNode);
	}

	@Override
	public List<IArtifact> getDependencies() {
		List<IArtifact> dependencies = new ArrayList<IArtifact>();
		for (Relationship relation : delegator.getRelationships(
				DOARelationship.HAS_ARTIFACT_DEPENDENCY, Direction.OUTGOING)) {
			IArtifact dependency = new NeoArtifact(doa, relation.getEndNode());
			dependencies.add(dependency);
		}
		return dependencies;
	}

	@Override
	public List<IEntity> getRegisteredEntities() {
		List<IEntity> entities = new ArrayList<IEntity>();
		for (Relationship relation : delegator.getRelationships(
				DOARelationship.HAS_ARTIFACT_ENTITY, Direction.OUTGOING)) {
			// TODO create instance of entity depends on class
			IEntity entity =
					NeoEntityDelegator.createEntityInstance(doa,
							relation.getEndNode());
			entities.add(entity);
		}
		return entities;
	}

	@Override
	public boolean isParentDependent() {
		if (delegator.hasRelationship(DOARelationship.HAS_ARTIFACT_DEPENDENCY,
				Direction.INCOMING)) {
			return true;
		}
		return false;
	}

	@Override
	public void removeDependency(IArtifact dependency) {
		for (Relationship relation : delegator.getRelationships(
				DOARelationship.HAS_ARTIFACT_DEPENDENCY, Direction.OUTGOING)) {
			if (relation.getEndNode().getId() == dependency.getId()) {
				relation.delete();
				break;
			}
		}

	}

	@Override
	public void unregisterEntity(IEntity entity) {
		for (Relationship relation : delegator.getRelationships(
				DOARelationship.HAS_ARTIFACT_ENTITY, Direction.OUTGOING)) {
			if (relation.getEndNode().getId() == entity.getId()) {
				relation.delete();
				break;
			}
		}
	}

	@Override
	public NeoEntityDelegator getNode() {
		return this.delegator;
	}

	@Override
	protected void setAttributeImpl(String attrName, String attrValue) {
		delegator.setAttribute(attrName, attrValue);
	}

	@Override
	protected void setAttributeImpl(IEntityAttribute attributte) {
		delegator.setAttribute(attributte);
	}

	@Override
	protected void removeAttributesImpl() {
		delegator.removeAttributes();
	}

	@Override
	protected boolean removeImpl(boolean forceRemoveContents) {
		if (delegator.hasRelationship(DOARelationship.HAS_ENTITY,
				Direction.OUTGOING)) {
			return false;
		}
		getArtifactResource().remove();
		return delegator.remove();
	}

	@Override
	protected boolean isStoredImpl() {
		return delegator.isStored();
	}

	@Override
	protected long getIdImpl() {
		return delegator.getId();
	}

	@Override
	protected String getNameImpl() {
		return delegator.getName();
	}

	@Override
	protected void setNameImpl(String name) {
		delegator.setName(name);
	}

	@Override
	protected IEntitiesContainer getContainerImpl() {
		return delegator.getContainer();
	}

	@Override
	protected String getLocationImpl() {
		return delegator.getLocation();
	}

	@Override
	protected boolean hasAttributesImpl() {
		return delegator.hasAttributes();
	}
	
	@Override
	protected List<String> getAttributeNamesImpl() {
		return delegator.getAttributeNames();
	}

	@Override
	protected String getAttributeImpl(String attrName) {
		return delegator.getAttribute(attrName);
	}

	@Override
	protected String getAttributeImpl(String attrName, String defaultValue) {
		return delegator.getAttribute(attrName, defaultValue);
	}

	@Override
	protected IEntityAttribute getAttributeObjectImpl(String attrName) {
		return delegator.getAttributeObject(attrName);
	}

	@Override
	protected IEntity storeImpl(String location) throws Throwable {
		return delegator.store(location);
	}

	@Override
	protected void setContainerImpl(IEntitiesContainer container) {
		delegator.setContainer(container);
	}

	@Override
	protected void setAttributesImpl(Map<String, String> attributes) {
		delegator.setAttributes(attributes);
	}

	@Override
	protected boolean hasEventListenersImpl() {
		return delegator.hasEventListeners();
	}

	@Override
	protected List<IEntityEventListener> getEventListenersImpl() {
		return delegator.getEventListeners();
	}

	@Override
	protected boolean isPublicImpl() {
		return delegator.isPublic();
	}

	@Override
	protected IArtifact getArtifactImpl() {
		return delegator.getArtifact();
	}

	@Override
	protected Date getLastModifiedImpl() {
		return delegator.getLastModified();
	}

	@Override
	protected Date getCreatedImpl() {
		return delegator.getCreated();
	}

	@Override
	protected IEntity redeployImpl(IEntity newEntity) throws Throwable {
		if (newEntity instanceof INeoObject) {
			delegator = (NeoEntityDelegator) delegator.redeploy(newEntity);
			return this;
		}
		throw new GeneralDOAException("Not INeoObject");
	}

	@Override
	protected IEntity getAncestorImpl() {
		return delegator.getAncestor();
	}

}

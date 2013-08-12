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
package pl.doa.service.impl.neo;

import java.io.Serializable;
import java.text.MessageFormat;
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
import pl.doa.agent.IAgent;
import pl.doa.agent.impl.neo.NeoAgent;
import pl.doa.artifact.IArtifact;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.impl.neo.NeoDocument;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityAttribute;
import pl.doa.entity.event.IEntityEventListener;
import pl.doa.relation.DOARelationship;
import pl.doa.service.IServiceDefinition;
import pl.doa.service.impl.AbstractRunningService;

/**
 * Klasa jest odpowiedzialna za wykonanie uslugi na podstawie jej definicji.
 * Definicja uslugi zawiera tylko i wylacznie implementacje logiki uslugi. Klasa
 * DOARunningService czuwa nad calemy procesem wykonania uslugi.
 * 
 * @author activey
 * 
 */
public class NeoRunningService extends AbstractRunningService implements
		INeoObject, Serializable {

	private final static Logger log = LoggerFactory
			.getLogger(NeoRunningService.class);

	public static final String PROP_ASYNCHRONOUS = "asynchronous";

	public static final String PROP_SERIALIZED_STATE = "serializedState";

	private NeoEntityDelegator delegator;

	public NeoRunningService(IDOA doa, Node underlyingNode) {
		super(doa);
		this.delegator = new NeoEntityDelegator(doa, underlyingNode);
	}

	public NeoRunningService(IDOA doa, GraphDatabaseService neo, String name) {
		super(doa);
		this.delegator = new NeoEntityDelegator(doa, neo, this.getClass()
				.getName());
		setName(name);
	}

	public NeoRunningService(IDOA doa, GraphDatabaseService neo) {
		super(doa);
		this.delegator = new NeoEntityDelegator(doa, neo, this.getClass()
				.getName());
		setName(delegator.getId() + "");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.doa.service.impl.neo.IRunningService#getServiceDefinition()
	 */
	@Override
	protected NeoServiceDefinition getServiceDefinitionImpl() {
		Relationship relation = delegator.getSingleRelationship(
				DOARelationship.HAS_RUNNING_SERVICE, Direction.INCOMING);
		if (relation == null) {
			log.error("HAS_RUNNING_SERVICE relation doesn't exist!");
			return null;
		}
		Node serviceDefNode = relation.getStartNode();
		return new NeoServiceDefinition(doa, serviceDefNode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.doa.service.impl.neo.IRunningService#getAgent()
	 */
	@Override
	protected IAgent getAgentImpl() {
		if (!delegator.hasRelationship(DOARelationship.IS_STARTED_BY,
				Direction.OUTGOING)) {
			return null;
		}
		Node agentNode = delegator.getSingleRelationship(
				DOARelationship.IS_STARTED_BY, Direction.OUTGOING).getEndNode();
		return new NeoAgent(doa, agentNode);

	}

	@Override
	protected IDocument getInputImpl() {
		if (!delegator.hasRelationship(DOARelationship.HAS_INPUT,
				Direction.OUTGOING)) {
			return null;
		}
		Node inputNode = delegator.getSingleRelationship(
				DOARelationship.HAS_INPUT, Direction.OUTGOING).getEndNode();
		return new NeoDocument(doa, inputNode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.doa.service.impl.neo.IRunningService#setInput(pl.doa.document.impl
	 * .neo.NeoDocument)
	 */
	@Override
	protected void setInputImpl(IDocument input) {
		INeoObject neoEntity = (INeoObject) input;
		if (input == null) {
			if (delegator.hasRelationship(DOARelationship.HAS_INPUT,
					Direction.OUTGOING)) {
				delegator.removeRelationship(DOARelationship.HAS_INPUT,
						Direction.OUTGOING);
				return;
			}
			return;
		}
		delegator.createRelationshipTo(neoEntity.getNode(),
				DOARelationship.HAS_INPUT);
	}

	/**
	 * Metoda zwraca dokument wynikowy uslugi. W przypadku gdy usluga jest w
	 * trybie oczekiwania na dokument wynikowy innej uslugi, nalezy zwrocic
	 * dokument, ktory bedzie o tym informowal agenta.
	 * 
	 * @return
	 */

	@Override
	protected IDocument getOutputImpl() {
		if (!delegator.hasRelationship(DOARelationship.HAS_OUTPUT,
				Direction.OUTGOING)) {
			// return createWaitingDocument();
			return null;
		}
		Node outputNode = delegator.getSingleRelationship(
				DOARelationship.HAS_OUTPUT, Direction.OUTGOING).getEndNode();
		if (outputNode == null) {
			return createWaitingDocument();
		}
		IDocument doc = new NeoDocument(doa, outputNode);
		return doc;
	}

	protected final IDocument createWaitingDocument() {
		try {
			IDocumentDefinition def = (IDocumentDefinition) doa
					.lookupEntityByLocation("/documents/system/waiting_for_document");
			IDocument doc = def.createDocumentInstance();
			// sprawdzanie, czy uruchomoina usluga jest skojarzona z jakims
			// sluchaczem

			// TODO !!!
			doc.setFieldValue("message", "proces trwa");

			return doc;
		} catch (GeneralDOAException e) {
			log.error("", e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.doa.service.impl.neo.IRunningService#setServiceDefinition(pl.doa.service
	 * .impl.neo.NeoServiceDefinition)
	 */
	@Override
	protected void setServiceDefinitionImpl(IServiceDefinition serviceDefinition) {
		serviceDefinition.addRunning(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.doa.service.impl.neo.IRunningService#setAgent(pl.doa.agent.impl.neo
	 * .NeoAgent)
	 */
	@Override
	protected void setAgentImpl(IAgent agent) {
		if (agent == null) {
			if (delegator.hasRelationship(DOARelationship.IS_STARTED_BY,
					Direction.OUTGOING)) {
				Relationship rel = delegator.getSingleRelationship(
						DOARelationship.IS_STARTED_BY, Direction.OUTGOING);
				rel.delete();
				return;
			}
			return;
		}
		INeoObject neoEntity = (INeoObject) agent;
		delegator.createRelationshipTo(neoEntity.getNode(),
				DOARelationship.IS_STARTED_BY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.doa.service.impl.neo.IRunningService#setOutput(pl.doa.document.impl
	 * .neo.NeoDocument)
	 */
	@Override
	protected void setOutputImpl(IDocument output) {
		if (output == null) {
			if (delegator.hasRelationship(DOARelationship.HAS_OUTPUT,
					Direction.OUTGOING)) {
				delegator.getSingleRelationship(DOARelationship.HAS_OUTPUT,
						Direction.OUTGOING).delete();
				return;
			}
			return;
		}
		if (delegator.hasRelationship(DOARelationship.HAS_OUTPUT,
				Direction.OUTGOING)) {
			log.debug(MessageFormat.format(
					"service with id = {0} has already output set!", getId()));
			return;
		}
		INeoObject neoEntity = (INeoObject) output;
		delegator.createRelationshipTo(neoEntity.getNode(),
				DOARelationship.HAS_OUTPUT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.doa.service.impl.neo.IRunningService#setAsynchronous(boolean)
	 */
	@Override
	protected void setAsynchronousImpl(boolean asynchronous) {
		delegator.setProperty(PROP_ASYNCHRONOUS, asynchronous);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.doa.service.impl.neo.IRunningService#isAsynchronous()
	 */
	@Override
	protected boolean isAsynchronousImpl() {
		return (Boolean) delegator.getProperty(PROP_ASYNCHRONOUS);
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
		if (delegator.hasRelationship(DOARelationship.IS_STARTED_BY)) {
			return false;
		}
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
			return delegator.redeploy(newEntity);
		}
		throw new GeneralDOAException("Not INeoObject");
	}

	@Override
	protected IEntity getAncestorImpl() {
		return delegator.getAncestor();
	}

	@Override
	public NeoEntityDelegator getNode() {
		return this.delegator;
	}

	@Override
	public void serializeState(byte[] stateData) {
		delegator.setProperty(PROP_SERIALIZED_STATE, stateData);
	}

	@Override
	public byte[] deserializeState() {
		if (!delegator.hasProperty(PROP_SERIALIZED_STATE)) {
			return null;
		}
		return (byte[]) delegator.getProperty(PROP_SERIALIZED_STATE);
	}

	@Override
	public IEntityEventListener getAwaitedEventListener() {
		return delegator.getAwaitedEventListener();
	}

}
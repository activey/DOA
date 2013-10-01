/**
 * 
 */
package pl.doa.channel.impl.neo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.INeoObject;
import pl.doa.NeoEntityDelegator;
import pl.doa.NeoStartableEntityDelegator;
import pl.doa.artifact.IArtifact;
import pl.doa.channel.impl.AbstractIncomingChannel;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityAttribute;
import pl.doa.entity.event.IEntityEventListener;

/**
 * @author Damian
 *
 */
public class NeoIncomingChannel extends AbstractIncomingChannel implements
		INeoObject, Serializable {

	private final static Logger log = LoggerFactory.getLogger(NeoChannel.class);

	private NeoStartableEntityDelegator delegator;

	public NeoIncomingChannel(IDOA doa, Node underlyingNode) {
		super(doa);
		this.delegator = new NeoStartableEntityDelegator(doa, underlyingNode);
	}

	public NeoIncomingChannel(IDOA doa, GraphDatabaseService neo, String name,
			String logicClass) {
		super(doa);
		this.delegator =
				new NeoStartableEntityDelegator(doa, neo, this.getClass()
						.getName());
		setName(name);
		setLogicClass(logicClass);
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
		if(newEntity instanceof INeoObject){
			return delegator.redeploy(newEntity);
		}
		throw new GeneralDOAException("Not INeoObject");
	}

	@Override
	protected IEntity getAncestorImpl() {
		return delegator.getAncestor();
	}

	@Override
	protected boolean isAutostartImpl() {
		return delegator.isAutostart();
	}

	@Override
	protected void setAutostartImpl(boolean autostart) {
		delegator.setAutostart(autostart);
	}

	@Override
	protected String getLogicClassImpl() {
		return delegator.getLogicClass();
	}

	@Override
	protected void setLogicClassImpl(String logicClass) {
		delegator.setLogicClass(logicClass);
	}

	@Override
	protected boolean removeImpl(boolean forceRemoveContents) {
		return delegator.remove();
	}

	@Override
	public NeoEntityDelegator getNode() {
		return this.delegator;
	}

}

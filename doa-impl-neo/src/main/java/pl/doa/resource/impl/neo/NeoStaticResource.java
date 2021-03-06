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
package pl.doa.resource.impl.neo;

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
import pl.doa.artifact.IArtifact;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityAttribute;
import pl.doa.entity.event.IEntityEventListener;
import pl.doa.resource.impl.AbstractStaticResource;

/**
 * @author activey
 * 
 */
public class NeoStaticResource extends AbstractStaticResource implements
		INeoObject, Serializable {

	private static final Logger log = LoggerFactory
			.getLogger(NeoStaticResource.class);

	private static final String PROP_MIMETYPE = "mimetype";
	private static final String PROP_CONTENT_SIZE = "contentSize";

	private NeoEntityDelegator delegator = null;

	public NeoStaticResource(IDOA doa, Node underlyingNode) {
		super(doa);
		this.delegator = new NeoEntityDelegator(doa, underlyingNode);
	}

	public NeoStaticResource(IDOA doa, GraphDatabaseService neo, String name) {
		super(doa);
		this.delegator =
				new NeoEntityDelegator(doa, neo, this.getClass().getName());
		if (name != null) {
			setName(name);
		} else {
			setName(getId() + "");
		}
	}

	public NeoStaticResource(IDOA doa, GraphDatabaseService neo) {
		this(doa, neo, null);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * pl.doa.resource.impl.neo.IStaticResource#setMimetype(java.lang.String)
	 */
	@Override
	protected void setMimetypeImpl(String mimetype) {
		if (mimetype == null) {
			if (delegator.hasProperty(PROP_MIMETYPE)) {
				delegator.removeProperty(PROP_MIMETYPE);
			}
			return;
		}
		delegator.setProperty(PROP_MIMETYPE, mimetype);
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.resource.impl.neo.IStaticResource#getMimetype()
	 */
	@Override
	protected String getMimetypeImpl() {
		return (String) delegator.getProperty(PROP_MIMETYPE);
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.resource.impl.neo.IStaticResource#getContentSize()
	 */
	@Override
	protected long getContentSizeImpl() {
		if (!delegator.hasProperty(PROP_CONTENT_SIZE)) {
			return 0;
		}
		return (Long) delegator.getProperty(PROP_CONTENT_SIZE);
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
		try {
			if (removeStreamContent()) {
				return delegator.remove();
			}
		} catch (Exception ex) {
			log.error("", ex);
		}
		return false;
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
	protected void setContentSizeImpl(long contentSize) {
		delegator.setProperty(PROP_CONTENT_SIZE, contentSize);
	}

}

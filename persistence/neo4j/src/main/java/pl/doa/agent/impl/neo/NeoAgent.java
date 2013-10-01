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
package pl.doa.agent.impl.neo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.INeoObject;
import pl.doa.NeoEntityDelegator;
import pl.doa.agent.IAgent;
import pl.doa.artifact.IArtifact;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityAttribute;
import pl.doa.entity.event.IEntityEventListener;
import pl.doa.entity.impl.AbstractEntity;
import pl.doa.relation.DOARelationship;

public class NeoAgent extends AbstractEntity implements IAgent, INeoObject,
        Serializable {

    public static final IAgent ANONYMOUS = null;

    public static final String AGENTS_LOCATION = "/agents";
    public static final String PREDEFINED_DOCUMENTS = "predefinedDocuments";
    public static final String CONTAINER_FINGERPRINTS = "fingerprints";
    private static final String PROP_ANONYMOUS = "anonymous";

    private NeoEntityDelegator delegator = null;

    public NeoAgent(IDOA doa, Node underlyingNode) {
        super(doa);
        this.delegator = new NeoEntityDelegator(doa, underlyingNode);
    }

    public NeoAgent(IDOA doa, GraphDatabaseService neo, String name) {
        super(doa);
        this.delegator = new NeoEntityDelegator(doa, neo, this.getClass()
                .getName());
        setName(name);
    }

    /*
     * (non-Javadoc)
     *
     * @see pl.doa.agent.impl.neo.IAgent#getPredefinedDocuments()
     */
    @Override
    public IEntitiesContainer getPredefinedDocuments()
            throws GeneralDOAException {
        return (IEntitiesContainer) getContainer().getEntityByName(
                PREDEFINED_DOCUMENTS);
    }

    /*
     * (non-Javadoc)
     *
     * @see pl.doa.agent.impl.neo.IAgent#getWaitingDocuments()
     */
    @Override
    public IEntitiesContainer getFingerprintsContainer() {
        return getContainer().getEntityByName(CONTAINER_FINGERPRINTS,
                IEntitiesContainer.class);
    }

    @Override
    public IEntitiesContainer getFingerprintsContainer(boolean createIfNull)
            throws GeneralDOAException {
        IEntitiesContainer agentContainer = getContainer();
        IEntitiesContainer fingerprints = agentContainer.getEntityByName(
                CONTAINER_FINGERPRINTS, IEntitiesContainer.class);
        if (fingerprints == null && createIfNull) {
            fingerprints = doa.createContainer(CONTAINER_FINGERPRINTS,
                    agentContainer);
        }
        return fingerprints;
    }

    /*
     * (non-Javadoc)
     *
     * @see pl.doa.agent.impl.neo.IAgent#getPredefinedDocument(java.lang.String)
     */
    @Override
    public IDocument getPredefinedDocument(String documentName)
            throws GeneralDOAException {
        if (getPredefinedDocuments() == null) {
            return null;
        }
        IEntitiesContainer predefinedDocuments = getPredefinedDocuments();
        return (IDocument) predefinedDocuments.getEntityByName(documentName);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * pl.doa.agent.impl.neo.IAgent#getPredefineFieldValue(java.lang.String,
     * java.lang.String)
     */
    @Override
    public Object getPredefineFieldValue(String documentName, String fieldName)
            throws GeneralDOAException {
        IDocument document = getPredefinedDocument(documentName);
        if (document == null) {
            return null;
        }
        return document.getFieldValue(fieldName);
    }

    public void setAnonymous(boolean outputRetrieved) {
        delegator.setProperty(PROP_ANONYMOUS, outputRetrieved);
    }

    /*
     * (non-Javadoc)
     *
     * @see pl.doa.agent.impl.neo.IAgent#isAnonymous()
     */
    @Override
    public boolean isAnonymous() {
        return delegator.hasProperty(PROP_ANONYMOUS)
                && (Boolean) delegator.getProperty(PROP_ANONYMOUS);
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

}

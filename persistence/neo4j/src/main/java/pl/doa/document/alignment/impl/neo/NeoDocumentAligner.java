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
package pl.doa.document.alignment.impl.neo;

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
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.alignment.impl.AbstractDocumentAligner;
import pl.doa.document.impl.neo.NeoDocumentDefinition;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityAttribute;
import pl.doa.entity.event.IEntityEventListener;
import pl.doa.relation.DOARelationship;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class NeoDocumentAligner extends AbstractDocumentAligner implements
        INeoObject, Serializable {

    private final static Logger log = LoggerFactory
            .getLogger(NeoDocumentAligner.class);
    private static final String PROP_LOGIC_CLASS = "logicClass";
    private AbstractDocumentAligner alignerImpl;

    private NeoEntityDelegator delegator = null;

    public NeoDocumentAligner(IDOA doa, Node underlyingNode) {
        super(doa);
        this.delegator = new NeoEntityDelegator(doa, underlyingNode);
    }

    public NeoDocumentAligner(IDOA doa, GraphDatabaseService neo, String name) {
        super(doa);
        this.delegator =
                new NeoEntityDelegator(doa, neo, this.getClass().getName());
        setName(name);
    }

    /*
     * (non-Javadoc)
     * @see pl.doa.document.alignment.impl.neo.IDocumentAligner#getLogicClass()
     */
    @Override
    public String getLogicClassImpl() {
        return (String) delegator.getNode().getProperty(PROP_LOGIC_CLASS);
    }

    /*
     * (non-Javadoc)
     * @see
     * pl.doa.document.alignment.impl.neo.IDocumentAligner#setLogicClass(java
     * .lang.String)
     */
    @Override
    public void setLogicClassImpl(String logicClass) {
        delegator.getNode().setProperty(PROP_LOGIC_CLASS, logicClass);
    }

    @Override
    public IDocumentDefinition getFromDefinitionImpl() {
        Node node =
                delegator
                        .getNode().getSingleRelationship(
                        DOARelationship.HAS_FROM_DEFINITION,
                        Direction.OUTGOING).getEndNode();
        return new NeoDocumentDefinition(doa, node);
    }

    /*
     * (non-Javadoc)
     * @see
     * pl.doa.document.alignment.impl.neo.IDocumentAligner#setFromDefinition
     * (pl.doa.document.impl.neo.NeoDocumentDefinition)
     */
    @Override
    public void setFromDefinitionImpl(IDocumentDefinition fromDefinition) {
        INeoObject neoEntity = (INeoObject) fromDefinition;
        if (!delegator.getNode().hasRelationship(DOARelationship.HAS_FROM_DEFINITION,
                Direction.OUTGOING)) {
            delegator.getNode().createRelationshipTo(neoEntity.getNode(),
                    DOARelationship.HAS_FROM_DEFINITION);
            return;
        }
        Relationship relation =
                delegator
                        .getNode().getSingleRelationship(
                        DOARelationship.HAS_FROM_DEFINITION,
                        Direction.OUTGOING);
        relation.delete();
        delegator.getNode().createRelationshipTo(neoEntity.getNode(),
                DOARelationship.HAS_FROM_DEFINITION);

    }

    /*
     * (non-Javadoc)
     * @see
     * pl.doa.document.alignment.impl.neo.IDocumentAligner#getToDefinition()
     */
    @Override
    public NeoDocumentDefinition getToDefinitionImpl() {
        Node node =
                delegator.getNode().getSingleRelationship(
                        DOARelationship.HAS_TO_DEFINITION, Direction.OUTGOING)
                        .getEndNode();
        return new NeoDocumentDefinition(doa, node);
    }

    /*
     * (non-Javadoc)
     * @see
     * pl.doa.document.alignment.impl.neo.IDocumentAligner#setToDefinition(pl
     * .doa.document.impl.neo.NeoDocumentDefinition)
     */
    @Override
    public void setToDefinitionImpl(IDocumentDefinition toDefinition) {
        INeoObject neoEntity = (INeoObject) toDefinition;
        if (!delegator.getNode().hasRelationship(DOARelationship.HAS_TO_DEFINITION,
                Direction.OUTGOING)) {
            if (toDefinition == null) {
                log.error(MessageFormat
                        .format("Passed null toDefinition to aligner: [{0}]",
                                getName()));
                return;
            }
            delegator.getNode().createRelationshipTo(neoEntity.getNode(),
                    DOARelationship.HAS_TO_DEFINITION);
            return;
        }
        Relationship relation =
                delegator.getNode().getSingleRelationship(
                        DOARelationship.HAS_TO_DEFINITION, Direction.OUTGOING);
        relation.delete();
        delegator.getNode().createRelationshipTo(neoEntity.getNode(),
                DOARelationship.HAS_TO_DEFINITION);
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
    protected Collection<String> getAttributeNamesImpl() {
        return delegator.getAttributeNames();
    }

    @Override
    protected String getAttributeImpl(String attrName) {
        return delegator.getAttribute(attrName);
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
    protected void setContainerImpl(IEntitiesContainer container) throws GeneralDOAException {
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
    protected IEntity getAncestorImpl() {
        return delegator.getAncestor();
    }

    @Override
    public Node getNode() {
        return delegator.getNode();
    }

}

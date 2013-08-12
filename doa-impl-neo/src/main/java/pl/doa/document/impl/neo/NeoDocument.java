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
/*
 * This file is part of "doa-prototype" project.
 */
package pl.doa.document.impl.neo;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
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

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.INeoObject;
import pl.doa.NeoEntityDelegator;
import pl.doa.NeoNodesIterator;
import pl.doa.artifact.IArtifact;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.impl.neo.NeoDocumentFieldValueDelegator;
import pl.doa.document.impl.AbstractDocument;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityAttachRule;
import pl.doa.entity.IEntityAttribute;
import pl.doa.entity.event.IEntityEventListener;
import pl.doa.relation.DOARelationship;

/**
 * TODO opis
 *
 * @author activey
 */
public class NeoDocument extends AbstractDocument implements INeoObject,
        Serializable {

    private final static Logger log = LoggerFactory
            .getLogger(NeoDocument.class);

    protected final NeoEntityDelegator delegator;

    public NeoDocument(IDOA doa, Node underlyingNode) {
        super(doa);
        this.delegator = new NeoEntityDelegator(doa, underlyingNode);
    }

    public NeoDocument(IDOA doa, GraphDatabaseService neo,
                       IDocumentDefinition definition, String name) {
        super(doa);
        this.delegator =
                new NeoEntityDelegator(doa, neo, this.getClass().getName());
        if (name != null) {
            setName(name);
        } else {
            setName("" + getId());
        }
        setDefinition(definition);
    }

    /*
     * (non-Javadoc)
     * @see pl.doa.document.impl.neo.IDocument#getDefinition()
     */
    @Override
    protected IDocumentDefinition getDefinitionImpl() {
        if (!delegator.hasRelationship(DOARelationship.HAS_DEFINITION,
                Direction.OUTGOING)) {
            return null;
        }
        Node node =
                delegator.getSingleRelationship(DOARelationship.HAS_DEFINITION,
                        Direction.OUTGOING).getEndNode();
        return new NeoDocumentDefinition(doa, node);
    }

    /*
     * (non-Javadoc)
     * @see
     * pl.doa.document.impl.neo.IDocument#isFieldAvailable(java.lang.String)
     */
    @Override
    protected boolean isFieldAvailableImpl(final String fieldName) {
        if (!delegator.hasRelationship(DOARelationship.HAS_FIELD,
                Direction.OUTGOING)) {
            return false;
        }
        return delegator.hasNode(DOARelationship.HAS_FIELD,
                new ReturnableEvaluator() {

                    @Override
                    public boolean isReturnableNode(TraversalPosition position) {
                        Node node = position.currentNode();
                        return fieldName.equals(node
                                .getProperty(NeoDocumentFieldValueDelegator.PROP_NAME));
                    }

                });
    }

    /**
     * Metoda dodaje nowe pole do dokumentu, ale tylko wtedy gdy jest ono
     * uwzglednione przez definicje dokumentu.
     *
     * @param fieldName Nazwa nowego pola.
     * @throws GeneralDOAException
     */
    private void addDocumentField(String fieldName) throws GeneralDOAException {
        IDocumentFieldType fieldType = getDefinition().getFieldType(fieldName);
        if (!isFieldAvailable(fieldName)) {
            return;
        }
        IDocumentFieldValue fieldValue =
                fieldType.createValueInstance(fieldName);
        delegator.createRelationshipTo((Node) fieldValue,
                DOARelationship.HAS_FIELD);
    }

	/*
     * (non-Javadoc)
	 * @see pl.doa.document.impl.neo.IDocument#setFieldValue(java.lang.String,
	 * java.lang.Object)
	 */

    /*
     * (non-Javadoc)
     * @see pl.doa.document.impl.neo.IDocument#getField(java.lang.String)
     */
    @Override
    protected IDocumentFieldValue getFieldImpl(final String fieldName) {
        Iterable<Relationship> fields =
                delegator.getRelationships(DOARelationship.HAS_FIELD,
                        Direction.OUTGOING);
        Node fieldNode = null;
        if (delegator.hasRelationship(DOARelationship.HAS_FIELD,
                Direction.OUTGOING)) {
            for (Relationship relationship : fields) {
                Node endNode = relationship.getEndNode();
                if (!endNode
                        .hasProperty(NeoDocumentFieldValueDelegator.PROP_NAME)) {
                    continue;
                }
                String nodeName =
                        (String) endNode
                                .getProperty(NeoDocumentFieldValueDelegator.PROP_NAME);
                if (nodeName.equals(fieldName)) {
                    fieldNode = endNode;
                    break;
                }
            }
        }
        if (fieldNode == null) {
            return null;
        }
        return NeoDocumentFieldValueDelegator.createFieldValueInstance(doa,
                fieldNode);
    }

    @Override
    protected void addFieldImpl(IDocumentFieldValue fieldValue) {
        delegator.createRelationshipTo(((INeoObject) fieldValue).getNode(),
                DOARelationship.HAS_FIELD);
    }

    /*
     * (non-Javadoc)
     * @see pl.doa.document.impl.neo.IDocument#getFieldValue(java.lang.String)
     */
    @Override
    protected Object getFieldValueImpl(String fieldName) {
        IDocumentFieldValue value = null;
        try {
            value = getField(fieldName, false);
        } catch (GeneralDOAException e) {
        }
        if (value == null) {
            return null;
        }
        return value.getFieldValue();
    }

    /*
     * (non-Javadoc)
     * @see pl.doa.document.impl.neo.IDocument#getFieldsNames()
     */
    @Override
    protected Iterator<String> getFieldsNamesImpl() {
        Traverser fieldsNodesTraverser =
                delegator.traverse(Order.BREADTH_FIRST,
                        StopEvaluator.DEPTH_ONE,
                        ReturnableEvaluator.ALL_BUT_START_NODE,
                        DOARelationship.HAS_FIELD, Direction.OUTGOING);
        return new NeoNodesIterator<String>(fieldsNodesTraverser) {
            @Override
            public String next(Node node) {
                return (String) node
                        .getProperty(NeoDocumentFieldValueDelegator.PROP_NAME);
            }
        };
    }

    /*
     * (non-Javadoc)
     * @see pl.doa.document.impl.neo.IDocument#getFields()
     */
    @Override
    protected Iterator<IDocumentFieldValue> getFieldsImpl() {
        Traverser fieldsNodesTraverser =
                delegator.traverse(Order.BREADTH_FIRST,
                        StopEvaluator.DEPTH_ONE,
                        ReturnableEvaluator.ALL_BUT_START_NODE,
                        DOARelationship.HAS_FIELD, Direction.OUTGOING);
        return new NeoNodesIterator<IDocumentFieldValue>(fieldsNodesTraverser) {
            @Override
            public IDocumentFieldValue next(Node node) {
                return NeoDocumentFieldValueDelegator.createFieldValueInstance(
                        doa, node);
            }
        };
    }

    /*
     * (non-Javadoc)
     * @see
     * pl.doa.document.impl.neo.IDocument#getFieldValueAsString(java.lang.String
     * )
     */
    @Override
    protected String getFieldValueAsStringImpl(String fieldName) {
        IDocumentFieldValue value = null;
        try {
            value = getField(fieldName, false);
        } catch (GeneralDOAException e) {
            e.printStackTrace();
        }
        if (value == null) {
            return null;
        }
        return value.getFieldValueAsString();
    }

    /*
     * (non-Javadoc)
     * @see
     * pl.doa.document.impl.neo.IDocument#setDefinition(pl.doa.document.impl
     * .neo.IDocumentDefinition)
     */
    @Override
    protected void setDefinitionImpl(IDocumentDefinition definition) {
        INeoObject neoEntity = (INeoObject) definition;
        delegator.createRelationshipTo(neoEntity.getNode(),
                DOARelationship.HAS_DEFINITION);
    }

    /*
     * (non-Javadoc)
     * @see pl.doa.document.impl.neo.IDocument#setFields(java.util.List)
     */
    @Override
    protected void setFieldsImpl(List<IDocumentFieldValue> list) {
        for (IDocumentFieldValue documentFieldValue : list) {
            delegator.createRelationshipTo((Node) documentFieldValue,
                    DOARelationship.HAS_FIELD);
        }

    }

	/*
	 * TODO protected void removeNode(Node list) { for (Relationship relation :
	 * list.getRelationships( DOARelationship.HAS_FIELD, Direction.OUTGOING)) {
	 * Node node = relation.getEndNode(); relation.delete(); if (((String)
	 * node.getProperty("className"))
	 * .compareTo(ListDocumentFieldValue.class.getName()) == 0) {
	 * removeNode(node); } else if (((String) node.getProperty("className"))
	 * .compareTo(ReferenceDocumentFieldValue.class.getName()) == 0) { if
	 * (node.hasRelationship(DOARelationship.HAS_ENTITY_REFERENCE)) {
	 * node.getRelationships(DOARelationship.HAS_ENTITY_REFERENCE)
	 * .iterator().next().delete(); } } node.delete(); } }
	 * @Override protected boolean internalRemove() { removeNode(this); return
	 * true; }
	 * @Override public void copyRelations(NeoEntity document) { for
	 * (Relationship rel : document.getRelationships( DOARelationship.HAS_INPUT,
	 * Direction.INCOMING)) { Node startNode = rel.getStartNode();
	 * startNode.createRelationshipTo(this, rel.getType()); rel.delete(); } for
	 * (Relationship rel : document.getRelationships(
	 * DOARelationship.HAS_OUTPUT, Direction.INCOMING)) { Node startNode =
	 * rel.getStartNode(); startNode.createRelationshipTo(this, rel.getType());
	 * rel.delete(); } for (Relationship rel : document.getRelationships(
	 * DOARelationship.HAS_ENTITY_REFERENCE, Direction.INCOMING)) { Node
	 * startNode = rel.getStartNode(); startNode.createRelationshipTo(this,
	 * rel.getType()); rel.delete(); } }
	 * @Override public boolean compare(NeoEntity entity) { IDocument document =
	 * (IDocument) entity; if (this.getName().compareTo(entity.getName()) == 0)
	 * { if (this.getFieldsNames().size() == document.getFieldsNames() .size())
	 * { for (String fieldName : this.getFieldsNames()) { IDocumentFieldValue
	 * field1 = this.getField(fieldName); IDocumentFieldValue field2 =
	 * document.getField(fieldName); if (field1 == null || field2 == null) {
	 * return false; } if (!field1.getFieldType().getClass()
	 * .equals(field2.getFieldType().getClass())) { return false; } } } } return
	 * true; }
	 */

    @Override
    protected boolean removeImpl(boolean forceRemoveContents) {
        // TODO - zrobic usuwanie RunningService po opublikowaniu prze nich output
        //		if (delegator.hasRelationship(DOARelationship.HAS_INPUT)
        //				|| delegator.hasRelationship(DOARelationship.HAS_OUTPUT)) {
        //			return false;
        //		}
        return delegator.remove();
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
        return delegator;
    }
}

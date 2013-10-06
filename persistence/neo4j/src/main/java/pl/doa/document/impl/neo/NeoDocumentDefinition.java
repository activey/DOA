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
package pl.doa.document.impl.neo;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.kernel.Traversal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.INeoObject;
import pl.doa.NeoEntityDelegator;
import pl.doa.artifact.IArtifact;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.impl.neo.NeoDocumentFieldType;
import pl.doa.document.field.impl.neo.NeoDocumentFieldValueDelegator;
import pl.doa.document.impl.AbstractDocumentDefinition;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityAttribute;
import pl.doa.entity.event.IEntityEventListener;
import pl.doa.relation.DOARelationship;

import java.io.Serializable;
import java.util.*;

/**
 * TODO opis
 *
 * @author activey
 */
public class NeoDocumentDefinition extends AbstractDocumentDefinition implements
        IDocumentDefinition, INeoObject, Serializable {

    private static final long serialVersionUID = 1L;

    private final static Logger log = LoggerFactory
            .getLogger(NeoDocumentDefinition.class);

    private NeoEntityDelegator delegator = null;

    public NeoDocumentDefinition(IDOA doa, Node underlyingNode) {
        super(doa);
        this.delegator = new NeoEntityDelegator(doa, underlyingNode);
    }

    public NeoDocumentDefinition(IDOA doa, GraphDatabaseService neo, String name) {
        super(doa);
        this.delegator =
                new NeoEntityDelegator(doa, neo, this.getClass().getName());
        setName(name);
    }

    public NeoDocumentDefinition(IDOA doa, GraphDatabaseService neo,
                                 String name, IEntity ancestor) {
        super(doa);
        this.delegator =
                new NeoEntityDelegator(doa, neo, this.getClass().getName(),
                        ancestor);
        setName(name);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * pl.doa.document.impl.neo.IDocumentDefinition#addField(java.lang.String,
     * java.lang.Class)
     */
    protected IDocumentFieldType addFieldImpl(String fieldName,
                                              DocumentFieldDataType dataType) throws GeneralDOAException {
        return addField(fieldName, dataType, false, false);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * pl.doa.document.impl.neo.IDocumentDefinition#addField(java.lang.String,
     * java.lang.Class, boolean, boolean)
     */
    protected NeoDocumentFieldType addFieldImpl(String fieldName,
                                                DocumentFieldDataType dataType, boolean required,
                                                boolean authorizable) throws GeneralDOAException {
        Node fieldNode = null;
        try {
            fieldNode = getFieldNode(fieldName);
        } catch (Throwable t) {
            log.debug("field with name " + fieldName + " is not registered yet");
        }
        if (fieldNode != null)
            throw new GeneralDOAException("Field with name [" + fieldName
                    + "] is already registered!");
        NeoDocumentFieldType newField =
                new NeoDocumentFieldType(doa, delegator.getNode().getGraphDatabase());
        newField.setName(fieldName);
        newField.setRequired(required);
        newField.setFieldDataType(dataType);
        newField.setAuthorizable(authorizable);
        delegator.getNode().createRelationshipTo(newField,
                DOARelationship.HAS_FIELD_DEFINITION);
        return newField;
    }

    private Node getFieldNode(final String fieldName) {
        boolean hasRel =
                delegator.getNode().hasRelationship(DOARelationship.HAS_FIELD_DEFINITION,
                        Direction.OUTGOING);
        if (!hasRel) {
            return null;
        }
        Node fieldNode = null;
        Iterable<Relationship> fields =
                delegator.getNode().getRelationships(
                        DOARelationship.HAS_FIELD_DEFINITION,
                        Direction.OUTGOING);
        for (Relationship relationship : fields) {
            Node endNode = relationship.getEndNode();
            if (!endNode.hasProperty(NeoDocumentFieldValueDelegator.PROP_NAME)) {
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

		/*
         * Node foundNode = lookupForNode(DOARelationship.HAS_FIELD_DEFINITION,
		 * new ReturnableEvaluator() {
		 * 
		 * @Override public boolean isReturnableNode( TraversalPosition
		 * position) { Node currentNode = position.currentNode(); return
		 * currentNode.getProperty( DocumentFieldType.PROP_NAME).equals(
		 * fieldName); } });
		 */
        return fieldNode;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * pl.doa.document.impl.neo.IDocumentDefinition#modifyField(java.lang.String
     * , java.lang.String, java.lang.Class, boolean, boolean)
     */
    public void modifyFieldImpl(String fieldName, String newName,
                                DocumentFieldDataType dataType, boolean required,
                                boolean authorizable) throws GeneralDOAException {
        Node fieldNode = getFieldNode(fieldName);
        if (fieldNode == null)
            throw new GeneralDOAException("Field with name [" + fieldName
                    + "] is not registered yet!");
        removeField(fieldName);
        addField(fieldName, dataType, required, authorizable);
    }

    public void removeFieldImpl(String fieldName) {
        Iterable<Relationship> fields =
                delegator.getNode().getRelationships(
                        DOARelationship.HAS_FIELD_DEFINITION,
                        Direction.OUTGOING);
        for (Relationship relationship : fields) {
            Node fieldNode = relationship.getEndNode();
            if (fieldName.equals(delegator
                    .getName())) {
                relationship.delete();
                fieldNode.delete();
                return;
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see pl.doa.document.impl.neo.IDocumentDefinition#getFieldNames()
     */
    @Override
    public Iterator<String> getFieldNamesImpl() {
        List<String> names = new LinkedList<String>();
        org.neo4j.graphdb.traversal.Traverser traverser =
                Traversal
                        .description()
                        .evaluator(Evaluators.excludeStartPosition())
                        .relationships(DOARelationship.HAS_FIELD_DEFINITION,
                                Direction.OUTGOING).depthFirst()
                        .sort(new FieldNameComparator()).traverse(delegator.getNode());
        for (Path path : traverser) {
            Node node = path.endNode();
            names.add((String) node.getProperty(NeoDocumentFieldType.PROP_NAME));
        }
        return names.iterator();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * pl.doa.document.impl.neo.IDocumentDefinition#getFieldType(java.lang.String
     * )
     */
    @Override
    public NeoDocumentFieldType getFieldTypeImpl(String fieldName) {
        Node fieldNode = getFieldNode(fieldName);
        if (fieldNode == null) {
            return null;
        }
        return NeoDocumentFieldType.createFieldTypeInstance(doa, fieldNode);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * pl.doa.document.impl.neo.IDocumentDefinition#setDocumentFields(java.util
     * .List)
     */
    @Override
    public void setDocumentFieldsImpl(List<IDocumentFieldType> documentFields) {
        for (IDocumentFieldType documentFieldType : documentFields) {
            delegator.getNode().createRelationshipTo((Node) documentFieldType,
                    DOARelationship.HAS_FIELD_DEFINITION);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see pl.doa.document.impl.neo.IDocumentDefinition#getDocumentFields()
     */
    @Override
    public Iterator<IDocumentFieldType> getDocumentFieldsImpl() {
        org.neo4j.graphdb.traversal.Traverser traverser =
                Traversal
                        .description()
                        .evaluator(Evaluators.excludeStartPosition())
                        .relationships(DOARelationship.HAS_FIELD_DEFINITION,
                                Direction.OUTGOING).depthFirst()
                        .sort(new FieldNameComparator()).traverse(delegator.getNode());
        Set<IDocumentFieldType> fields =
                new LinkedHashSet<IDocumentFieldType>();
        for (Path path : traverser) {
            Node node = path.endNode();
            fields.add(NeoDocumentFieldType.createFieldTypeInstance(doa, node));
        }
        return fields.iterator();
    }

    /*
     * (non-Javadoc)
     *
     * @see pl.doa.document.impl.neo.IDocumentDefinition#getRequiredFields()
     */
    @Override
    public Iterator<IDocumentFieldType> getRequiredFieldsImpl() {
        Traverser traverser =
                delegator.getNode().traverse(
                        Order.BREADTH_FIRST,
                        StopEvaluator.DEPTH_ONE,
                        new ReturnableEvaluator() {

                            @Override
                            public boolean isReturnableNode(
                                    TraversalPosition position) {
                                Node actualNode = position.currentNode();
                                Boolean required =
                                        actualNode
                                                .hasProperty(NeoDocumentFieldType.PROP_REQUIRED)
                                                && (Boolean) actualNode
                                                .getProperty(NeoDocumentFieldType.PROP_REQUIRED);
                                return required;
                            }

                        }, DOARelationship.HAS_FIELD_DEFINITION,
                        Direction.OUTGOING);
        Set<IDocumentFieldType> fields = new HashSet<IDocumentFieldType>();
        for (Node node : traverser) {
            fields.add(NeoDocumentFieldType.createFieldTypeInstance(doa, node));
        }
        return fields.iterator();
    }

    /*
     * (non-Javadoc)
     *
     * @see pl.doa.document.impl.neo.IDocumentDefinition#getAuthorizableFields()
     */
    @Override
    public Iterator<IDocumentFieldType> getAuthorizableFieldsImpl() {
        Traverser traverser =
                delegator.getNode().traverse(
                        Order.BREADTH_FIRST,
                        StopEvaluator.DEPTH_ONE,
                        new ReturnableEvaluator() {

                            @Override
                            public boolean isReturnableNode(
                                    TraversalPosition position) {
                                Node actualNode = position.currentNode();
                                boolean authorizable =
                                        actualNode
                                                .hasProperty(NeoDocumentFieldType.PROP_AUTHORIZABLE)
                                                && actualNode
                                                .getProperty(
                                                        NeoDocumentFieldType.PROP_AUTHORIZABLE)
                                                .equals(true);
                                return authorizable;
                            }

                        }, DOARelationship.HAS_FIELD_DEFINITION,
                        Direction.OUTGOING);
        Set<IDocumentFieldType> fields = new HashSet<IDocumentFieldType>();
        Collection<Node> nodes = traverser.getAllNodes();
        for (Node node : nodes) {
            fields.add(NeoDocumentFieldType.createFieldTypeInstance(doa, node));
        }
        return fields.iterator();
    }


    @Override
    protected boolean removeImpl(boolean forceRemoveContents) {
        if (delegator.getNode().hasRelationship(DOARelationship.HAS_DEFINITION)
                || delegator
                .getNode().hasRelationship(DOARelationship.HAS_FROM_DEFINITION)
                || delegator.getNode().hasRelationship(DOARelationship.HAS_TO_DEFINITION)
                || delegator
                .getNode().hasRelationship(DOARelationship.HAS_INPUT_DEFINITION)
                || delegator
                .getNode().hasRelationship(DOARelationship.HAS_OUTPUT_DEFINITION)) {
            return false;
        }
        for (Relationship relation : delegator.getNode().getRelationships(
                DOARelationship.HAS_FIELD_DEFINITION, Direction.OUTGOING)) {
            Node fieldDefinition = relation.getEndNode();
            for (Relationship fieldRel : fieldDefinition.getRelationships()) {
                fieldRel.delete();
            }
            fieldDefinition.delete();
        }
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


    private class FieldNameComparator implements Comparator<Path> {

        @Override
        public int compare(Path o1, Path o2) {
            Node firstNode = o1.endNode();
            Node secondNode = o2.endNode();

            String firstName =
                    (String) firstNode
                            .getProperty(NeoDocumentFieldType.PROP_NAME);
            String secondName =
                    (String) secondNode
                            .getProperty(NeoDocumentFieldType.PROP_NAME);
            return firstName.compareTo(secondName);
        }

    }

}

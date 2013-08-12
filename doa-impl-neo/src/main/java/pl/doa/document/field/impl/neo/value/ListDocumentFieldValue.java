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
package pl.doa.document.field.impl.neo.value;

import java.util.ArrayList;

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
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.impl.AbstractListDocumentFieldValue;
import pl.doa.document.field.impl.neo.NeoDocumentFieldType;
import pl.doa.document.field.impl.neo.NeoDocumentFieldValueDelegator;
import pl.doa.document.field.impl.neo.value.list.ListDocumentFieldIterable;
import pl.doa.entity.IEntity;
import pl.doa.relation.DOARelationship;
import pl.doa.utils.DynamicLong;

/**
 * @author activey
 * 
 */
public class ListDocumentFieldValue extends AbstractListDocumentFieldValue
		implements INeoObject {

	private final static Logger log = LoggerFactory
			.getLogger(ListDocumentFieldValue.class);
	private NeoDocumentFieldValueDelegator delegator;
	private final IDOA doa;

	public ListDocumentFieldValue(IDOA doa, GraphDatabaseService neo) {
		this.doa = doa;
		this.delegator =
				new NeoDocumentFieldValueDelegator(doa, neo,
						ListDocumentFieldValue.class.getName());
	}

	public ListDocumentFieldValue(IDOA doa, Node node) {
		this.doa = doa;
		this.delegator = new NeoDocumentFieldValueDelegator(doa, node);
	}

	@Override
	public IDocumentFieldValue addStringFieldImpl(String fieldName,
			String fieldValue) throws GeneralDOAException {
		IDocumentFieldValue stringValue =
				addField(fieldName, DocumentFieldDataType.string);
		if (fieldValue != null) {
			stringValue.setFieldValue(fieldValue);
		}
		return stringValue;
	}

	@Override
	public IDocumentFieldValue addStringFieldImpl(String fieldName)
			throws GeneralDOAException {
		return addStringField(fieldName, null);
	}

	@Override
	public IDocumentFieldValue addReferenceFieldImpl(String fieldName)
			throws GeneralDOAException {
		IDocumentFieldValue stringValue =
				addField(fieldName, DocumentFieldDataType.reference);
		return stringValue;
	}

	@Override
	public Iterable<IDocumentFieldValue> iterateFieldsImpl() {
		if (!delegator.hasRelationship(DOARelationship.HAS_FIELD,
				Direction.OUTGOING)) {
			return new ArrayList<IDocumentFieldValue>();
		}
		Traverser traverser =
				delegator.traverse(Order.BREADTH_FIRST,
						StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

							@Override
							public boolean isReturnableNode(
									TraversalPosition currentPos) {
								return (!currentPos.isStartNode());
							}
						}, DOARelationship.HAS_FIELD, Direction.OUTGOING);

		return new ListDocumentFieldIterable(doa, traverser.iterator());
	}

	@Override
	public int compareToImpl(IDocumentFieldValue field) {
		return 0;
	}

	public IDocumentFieldValue addFieldImpl(String fieldName,
			DocumentFieldDataType type) throws GeneralDOAException {
		IDocumentFieldValue existingField = getListField(fieldName);
		if (existingField != null) {
			existingField.remove();
		}

		NeoDocumentFieldType simpleType =
				new NeoDocumentFieldType(doa, delegator.getGraphDatabase());
		simpleType.setFieldDataType(type);

		IDocumentFieldValue simpleField =
				simpleType.createValueInstance(fieldName);
		Node fieldNode = ((INeoObject) simpleField).getNode();

		fieldNode.createRelationshipTo(simpleType,
				DOARelationship.HAS_FIELD_DEFINITION);
		delegator.createRelationshipTo(fieldNode, DOARelationship.HAS_FIELD);
		return simpleField;
	}

	@Override
	public IDocumentFieldValue addReferenceFieldImpl(String fieldName,
			IEntity referenceEntity) throws GeneralDOAException {
		IDocumentFieldValue referenceValue =
				addField(fieldName, DocumentFieldDataType.reference);
		if (referenceValue != null) {
			referenceValue.setFieldValue(referenceEntity);
		}
		return referenceValue;
	}

	@Override
	public boolean isEmptyImpl() {
		return !delegator.hasRelationship(DOARelationship.HAS_FIELD,
				Direction.OUTGOING);
	}

	public IDocumentFieldType getFieldTypeImpl() {
		return delegator.getFieldType();
	}

	public String getFieldNameImpl() {
		return delegator.getFieldName();
	}

	public void setFieldNameImpl(String fieldName) {
		delegator.setFieldName(fieldName);
	}

	@Override
	public IDocumentFieldValue getListField(final String fieldName) {
		if (!delegator.hasRelationship(DOARelationship.HAS_FIELD,
				Direction.OUTGOING)) {
			return null;
		}
		Traverser traverser =
				delegator.traverse(Order.BREADTH_FIRST,
						StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

							@Override
							public boolean isReturnableNode(
									TraversalPosition currentPos) {
								if (currentPos.isStartNode()) {
									return false;
								}
								Node fieldNode = currentPos.currentNode();
								if (!fieldNode
										.hasProperty(NeoDocumentFieldValueDelegator.PROP_NAME)) {
									return false;
								}
								String nodeName =
										(String) fieldNode
												.getProperty(NeoDocumentFieldValueDelegator.PROP_NAME);
								return fieldName.equals(nodeName);
							}
						}, DOARelationship.HAS_FIELD, Direction.OUTGOING);
		return new ListDocumentFieldIterable(doa, traverser.iterator()).next();
	}

	@Override
	protected String getFieldValueAsStringImpl() {
		// TODO zaimplementowac
		return "()";
	}

	@Override
	protected void setFieldValueImpl(Iterable<IDocumentFieldValue> value)
			throws GeneralDOAException {
		// TODO zaimplementowac
	}

	@Override
	public Node getNode() {
		return delegator;
	}

	@Override
	public long countFields() {
		DynamicLong count = new DynamicLong();
		Iterable<Relationship> fields =
				delegator.getRelationships(Direction.OUTGOING,
						DOARelationship.HAS_FIELD);
		for (Relationship relationship : fields) {
			count.modify(1);
		}
		return count.getValue();
	}

	@Override
	public void remove() {
		delegator.delete();
	}

}
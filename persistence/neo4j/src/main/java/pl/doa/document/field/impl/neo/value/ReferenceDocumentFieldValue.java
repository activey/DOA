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

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.INeoObject;
import pl.doa.NeoEntityDelegator;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.impl.AbstractReferenceDocumentFieldValue;
import pl.doa.document.field.impl.neo.NeoDocumentFieldValueDelegator;
import pl.doa.entity.DetachedEntity;
import pl.doa.entity.IEntity;
import pl.doa.relation.DOARelationship;

/**
 * @author activey
 * 
 */
public class ReferenceDocumentFieldValue extends
		AbstractReferenceDocumentFieldValue implements INeoObject {

	private NeoDocumentFieldValueDelegator delegator;

	public ReferenceDocumentFieldValue(IDOA doa, GraphDatabaseService neo) {
		super(doa);
		this.delegator =
				new NeoDocumentFieldValueDelegator(doa, neo,
						ReferenceDocumentFieldValue.class.getName());
	}

	public ReferenceDocumentFieldValue(IDOA doa, Node node) {
		super(doa);
		this.delegator = new NeoDocumentFieldValueDelegator(doa, node);
	}

	@Override
	public IEntity getFieldValueImpl() {
		if (!delegator.hasRelationship(DOARelationship.HAS_ENTITY_REFERENCE,
				Direction.OUTGOING)) {
			return null;
		}
		Node node =
				delegator.getSingleRelationship(
						DOARelationship.HAS_ENTITY_REFERENCE,
						Direction.OUTGOING).getEndNode();
		return NeoEntityDelegator.createEntityInstance(doa, node);

	}

	@Override
	public void setFieldValueImpl(IEntity fieldValue)
			throws GeneralDOAException {
		if (fieldValue == null) {
			if (delegator.hasRelationship(DOARelationship.HAS_ENTITY_REFERENCE,
					Direction.OUTGOING)) {
				Relationship relation =
						delegator.getSingleRelationship(
								DOARelationship.HAS_ENTITY_REFERENCE,
								Direction.OUTGOING);
				relation.delete();
				return;
			}
			return;
		}
		if (delegator.hasRelationship(DOARelationship.HAS_ENTITY_REFERENCE,
				Direction.OUTGOING)) {
			Relationship relation =
					delegator.getSingleRelationship(
							DOARelationship.HAS_ENTITY_REFERENCE,
							Direction.OUTGOING);
			relation.delete();
		}
		if (fieldValue instanceof DetachedEntity) {
			DetachedEntity detached = (DetachedEntity) fieldValue;
			if (!detached.isStored()) {
				throw new GeneralDOAException("Entity has to be stored first!");
			}
			IEntity stored = detached.getStoredEntity();
			delegator.createRelationshipTo(((INeoObject) stored).getNode(),
					DOARelationship.HAS_ENTITY_REFERENCE);
			return;
		}

		delegator.createRelationshipTo(((INeoObject) fieldValue).getNode(),
				DOARelationship.HAS_ENTITY_REFERENCE);
		return;
	}

	@Override
	public int compareToImpl(IEntity fieldValue) {
		// TODO !!!
		return 0;
	}

	@Override
	public String getFieldValueAsStringImpl() {
		IEntity referenced = getFieldValueImpl();
		if (referenced == null) {
			return "";
		}
		return referenced.getId() + "";
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
	public Node getNode() {
		return delegator;
	}

	@Override
	public boolean isEmpty() {
		return delegator.hasProperty(NeoDocumentFieldValueDelegator.PROP_VALUE);
	}

	@Override
	public void remove() {
		Iterable<Relationship> rels = delegator.getRelationships();
		for (Relationship relationship : rels) {
			relationship.delete();
		}
		delegator.delete();
	}
}

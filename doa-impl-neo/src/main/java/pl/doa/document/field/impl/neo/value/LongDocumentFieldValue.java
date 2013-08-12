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

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import pl.doa.IDOA;
import pl.doa.INeoObject;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.impl.AbstractLongDocumentFieldValue;
import pl.doa.document.field.impl.neo.NeoDocumentFieldValueDelegator;

/**
 * @author activey
 * 
 */
public class LongDocumentFieldValue extends AbstractLongDocumentFieldValue
		implements INeoObject {

	private NeoDocumentFieldValueDelegator delegator;

	public LongDocumentFieldValue(IDOA doa, GraphDatabaseService neo) {
		this.delegator =
				new NeoDocumentFieldValueDelegator(doa, neo,
						LongDocumentFieldValue.class.getName());
	}

	public LongDocumentFieldValue(IDOA doa, Node node) {
		this.delegator = new NeoDocumentFieldValueDelegator(doa, node);
	}

	@Override
	public Long getFieldValueImpl() {
		return (Long) delegator
				.getProperty(NeoDocumentFieldValueDelegator.PROP_VALUE);
	}

	@Override
	public void setFieldValueImpl(Long fieldValue) {
		delegator.setProperty(NeoDocumentFieldValueDelegator.PROP_VALUE,
				(Long) fieldValue);
	}

	@Override
	public int compareToImpl(Long fieldValue) {
		return ((Long) delegator
				.getProperty(NeoDocumentFieldValueDelegator.PROP_VALUE))
				.compareTo(fieldValue);
	}

	@Override
	public void copyFromImpl(IDocumentFieldValue otherField) {
		IDocumentFieldValue simpleField = (IDocumentFieldValue) otherField;
		delegator.setProperty(NeoDocumentFieldValueDelegator.PROP_VALUE,
				simpleField.getFieldValue());
	}

	@Override
	public String getFieldValueAsStringImpl() {
		Long longValue = getFieldValueImpl();
		if (longValue == null) {
			return "";
		}
		return longValue.toString();
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
		return !delegator
				.hasProperty(NeoDocumentFieldValueDelegator.PROP_VALUE);
	}

	@Override
	public void remove() {
		delegator.delete();
	}
}

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
package pl.doa.document.field.impl.neo;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
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
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.impl.neo.value.BigDecimalDocumentFieldValue;
import pl.doa.document.field.impl.neo.value.BooleanDocumentFieldValue;
import pl.doa.document.field.impl.neo.value.DateDocumentFieldValue;
import pl.doa.document.field.impl.neo.value.DoubleDocumentFieldValue;
import pl.doa.document.field.impl.neo.value.IntegerDocumentFieldValue;
import pl.doa.document.field.impl.neo.value.ListDocumentFieldValue;
import pl.doa.document.field.impl.neo.value.LongDocumentFieldValue;
import pl.doa.document.field.impl.neo.value.PasswordDocumentFieldValue;
import pl.doa.document.field.impl.neo.value.ReferenceDocumentFieldValue;
import pl.doa.document.field.impl.neo.value.StringDocumentFieldValue;
import pl.doa.document.impl.neo.NeoDocumentDefinition;
import pl.doa.neo.NodeDelegate;
import pl.doa.relation.DOARelationship;

/**
 * 
 * @author activey
 */
public class NeoDocumentFieldType extends NodeDelegate implements Serializable,
		IDocumentFieldType {

	private static final String ATTR_SUFFIX = "attr_";
	private final static Map<DocumentFieldDataType, Class<? extends IDocumentFieldValue>> TYPES_MAPPINGS =
			new HashMap<DocumentFieldDataType, Class<? extends IDocumentFieldValue>>();

	static {
		TYPES_MAPPINGS.put(DocumentFieldDataType.string,
				StringDocumentFieldValue.class);
		TYPES_MAPPINGS.put(DocumentFieldDataType.bool,
				BooleanDocumentFieldValue.class);
		TYPES_MAPPINGS.put(DocumentFieldDataType.doubleprec,
				DoubleDocumentFieldValue.class);
		TYPES_MAPPINGS.put(DocumentFieldDataType.integer,
				IntegerDocumentFieldValue.class);
		TYPES_MAPPINGS.put(DocumentFieldDataType.longinteger,
				LongDocumentFieldValue.class);
		TYPES_MAPPINGS.put(DocumentFieldDataType.bigdecimal,
				BigDecimalDocumentFieldValue.class);
		TYPES_MAPPINGS.put(DocumentFieldDataType.date,
				DateDocumentFieldValue.class);
		TYPES_MAPPINGS.put(DocumentFieldDataType.password,
				PasswordDocumentFieldValue.class);
		TYPES_MAPPINGS.put(DocumentFieldDataType.reference,
				ReferenceDocumentFieldValue.class);
		TYPES_MAPPINGS.put(DocumentFieldDataType.list,
				ListDocumentFieldValue.class);

	}

	private final static Logger log = LoggerFactory
			.getLogger(NeoDocumentFieldType.class);
	public static final String PROP_REQUIRED = "required";
	public static final String PROP_AUTHORIZABLE = "authorizable";
	public static final String PROP_NAME = "name";
	public static final String PROP_DATA_TYPE = "dataType";
	private IDOA doa;

	public NeoDocumentFieldType(IDOA doa, GraphDatabaseService neo) {
		super(neo, NeoDocumentFieldType.class.getName());
		this.doa = doa;
	}

	public NeoDocumentFieldType(IDOA doa, Node node) {
		super(node);
		this.doa = doa;
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.document.field.IDocumentFieldType#getName()
	 */
	@Override
	public String getName() {
		return (String) getProperty(PROP_NAME);
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.document.field.IDocumentFieldType#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		setProperty(PROP_NAME, name);
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.document.field.IDocumentFieldType#getFieldClass()
	 */
	@Override
	public DocumentFieldDataType getFieldDataType() {
		if (!hasProperty(PROP_DATA_TYPE)) {
			return null;
		}
		String dataType = (String) getProperty(PROP_DATA_TYPE);
		return DocumentFieldDataType.valueOf(dataType);
	}

	@Override
	public void setFieldDataType(DocumentFieldDataType fieldDataType) {
		setProperty(PROP_DATA_TYPE, fieldDataType.name());
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.document.field.IDocumentFieldType#isRequired()
	 */
	@Override
	public boolean isRequired() {
		if (!hasProperty(PROP_REQUIRED)) {
			return false;
		}
		return (Boolean) getProperty(PROP_REQUIRED);
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.document.field.IDocumentFieldType#setRequired(boolean)
	 */
	@Override
	public void setRequired(boolean required) {
		setProperty(PROP_REQUIRED, new Boolean(required));
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.document.field.IDocumentFieldType#isAuthorizable()
	 */
	@Override
	public boolean isAuthorizable() {
		if (!hasProperty(PROP_AUTHORIZABLE)) {
			return false;
		}
		return (Boolean) getProperty(PROP_AUTHORIZABLE);
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.document.field.IDocumentFieldType#setAuthorizable(boolean)
	 */
	@Override
	public void setAuthorizable(boolean authorizable) {
		setProperty(PROP_AUTHORIZABLE, new Boolean(authorizable));
	}

	public IDocumentFieldValue createValueInstance() throws GeneralDOAException {
		return createValueInstance(null);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * pl.doa.document.field.IDocumentFieldType#createValueInstance(java.lang
	 * .String, pl.doa.IDOA)
	 */
	@Override
	public IDocumentFieldValue createValueInstance(String fieldName)
			throws GeneralDOAException {
		IDocumentFieldValue value;
		try {
			DocumentFieldDataType dataType = getFieldDataType();
			Class<? extends IDocumentFieldValue> clazz =
					TYPES_MAPPINGS.get(dataType);
			if (clazz == null) {
				throw new GeneralDOAException(
						"Unable to find mapping for type: [{0}]", dataType);
			}
			Constructor<? extends IDocumentFieldValue> constructor =
					clazz.getConstructor(IDOA.class, GraphDatabaseService.class);
			value = constructor.newInstance(doa, getGraphDatabase());
		} catch (Exception e) {
			log.error("", e);
			return null;
		}
		if (fieldName == null) {
			value.setFieldName(getName());
		} else {
			value.setFieldName(fieldName);
		}
		return value;
	}

	public static NeoDocumentFieldType createFieldTypeInstance(IDOA doa,
			Node node) {
		String className =
				(String) node.getProperty(NeoDocumentFieldType.PROP_CLASS_NAME);
		Class<? extends NeoDocumentFieldType> clazz;
		try {
			clazz =
					(Class<? extends NeoDocumentFieldType>) Class
							.forName(className);
		} catch (Throwable t) {
			log.error("", t);
			return null;
		}
		try {
			Constructor<? extends NeoDocumentFieldType> constructor =
					clazz.getConstructor(IDOA.class, Node.class);
			return constructor.newInstance(doa, node);
		} catch (Throwable e) {
			log.error("", e);
			return null;
		}
	}

	@Override
	public Object getAttribute(String attributeName) {
		return getAttribute(attributeName, null);
	}

	@Override
	public Object getAttribute(String attributeName, Object defaultValue) {
		if (!hasProperty(ATTR_SUFFIX + attributeName)) {
			return defaultValue;
		}
		return getProperty(ATTR_SUFFIX + attributeName);
	}

	@Override
	public List<String> getAttributeNames() {
		List<String> attrNames = new ArrayList<String>();
		Iterable<String> propertyKeys = getPropertyKeys();
		for (String property : propertyKeys) {
			if (property.startsWith(ATTR_SUFFIX)) {
				String attrName = property.substring(ATTR_SUFFIX.length());
				attrNames.add(attrName);
			}
		}
		return attrNames;
	}

	@Override
	public void setAttribute(String attributeName, Object attributeValue) {
		if (attributeValue == null) {
			if (!hasProperty(ATTR_SUFFIX + attributeName)) {
				removeProperty(ATTR_SUFFIX + attributeName);
				return;
			} else {
				return;
			}
		}
		setProperty(ATTR_SUFFIX + attributeName, attributeValue);
	}

	@Override
	public IDocumentDefinition getDocumentDefinition() {
		if (!hasRelationship(DOARelationship.HAS_FIELD_DEFINITION,
				Direction.INCOMING)) {
			return null;
		}
		Relationship relation =
				getSingleRelationship(DOARelationship.HAS_FIELD_DEFINITION,
						Direction.INCOMING);
		Node documentDefinitionNode = relation.getStartNode();
		return new NeoDocumentDefinition(doa, documentDefinitionNode);
	}

	@Override
	public final void remove() {
		remove(false);
	}

	@Override
	public final void remove(boolean forceRemove) {
		if (forceRemove) {
			Iterable<Relationship> relations = getRelationships();
			for (Relationship relationship : relations) {
				relationship.delete();
			}
		}
		delete();
	}

}

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
package pl.doa.artifact.tag;

import java.text.MessageFormat;

import nu.xom.Nodes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.IListDocumentFieldValue;
import pl.doa.entity.IEntity;
import pl.doa.templates.tags.Tag;

public class FieldValueTag extends Tag {

	private final static Logger log = LoggerFactory
			.getLogger(FieldValueTag.class);

	private String name;
	private Object value;
	private String var;
	private String dataType;

	protected IDocumentFieldValue field;

	@Override
	public void processTagStart() throws Exception {
		buildField();
	}

	private void buildField() throws Exception {
		Tag parent = this.getParent();
		if (parent != null) {
			if (parent instanceof DocumentTag) {
				this.field = ((DocumentTag) parent).add(name);
			} else if (parent instanceof FieldValueTag) {
				FieldValueTag parentField = (FieldValueTag) parent;
				IDocumentFieldValue parentValue = parentField.field;
				if (parentValue instanceof IListDocumentFieldValue) {
					if (dataType == null) {
						throw new GeneralDOAException(
								"Data type for field [{0}] has to be defined!",
								name);
					}
					IListDocumentFieldValue listValue =
							(IListDocumentFieldValue) parentValue;
					IDocumentFieldValue newSimpleValue =
							listValue.addField(name,
									DocumentFieldDataType.valueOf(dataType));
					this.field = newSimpleValue;
				} else {
					throw new GeneralDOAException(
							"Only list supported here ...");
				}
			}
		}
	}

	@Override
	public Nodes processTagEnd() throws Exception {
		Object attributeValue = value;
		String bodyText = getBodyText();
		if (bodyText != null) {
			bodyText = bodyText.trim();
			if (bodyText.length() > 0) {
				attributeValue = bodyText;
			}
		}
		if (field == null) {
			buildField();
		}
		String stringValue = null;
		if (attributeValue instanceof String) {
			stringValue = (String) attributeValue;

			if (stringValue != null) {
				if (stringValue.startsWith("#") || stringValue.startsWith("@")) {
					String contextVar = stringValue.substring(1);
					IEntity found = (IEntity) context.getVariable(contextVar);
					field.setFieldValue(found);
				} else {
					field.setFieldValue(attributeValue);
				}
			}
		} else {
			if (field == null) {
				log.error(MessageFormat.format("Unable to find field with name [{0}]" , name));
				return null;
			}
			field.setFieldValue(attributeValue);
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

}

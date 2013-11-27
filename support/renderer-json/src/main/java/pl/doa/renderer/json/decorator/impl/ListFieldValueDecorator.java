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
package pl.doa.renderer.json.decorator.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.IListDocumentFieldValue;
import pl.doa.renderer.json.decorator.JSONFieldValueDecorator;

/**
 * @author activey
 * 
 */
public class ListFieldValueDecorator extends JSONFieldValueDecorator {

	private final static Logger log = LoggerFactory
			.getLogger(ListFieldValueDecorator.class);

	/*
	 * (non-Javadoc)
	 * @see
	 * pl.doa.renderer.json.decorator.JSONFieldValueDecorator#createObject(pl
	 * .doa.temp.document.field.DocumentFieldValue)
	 */
	@Override
	public JSONObject decorate(IDocumentFieldValue fieldValue)
			throws GeneralDOAException {
		JSONObject fieldObject = new JSONObject();
		try {
			IListDocumentFieldValue listValue =
					(IListDocumentFieldValue) fieldValue;
			fieldObject.put("type", listValue.getFieldType().getFieldDataType()
					.toString());

			List<JSONObject> jsonElements = new ArrayList<JSONObject>();
			Iterable<IDocumentFieldValue> elements = listValue.iterateFields();
			int elementsCount = 0;
			for (IDocumentFieldValue documentFieldValue : elements) {
				JSONObject decorated = null;
				try {
					decorated = factory.decorate(documentFieldValue);
				} catch (GeneralDOAException e) {
					log.error("", e);
					continue;
				}
				/*
				 * list.put(documentFieldValue.getFieldName(),
				 * decorated.build(documentFieldValue.getFieldName()));
				 */
				JSONObject object = new JSONObject();
				object.put(documentFieldValue.getFieldName(), decorated);
				jsonElements.add(object);
				elementsCount++;
			}
			fieldObject.put("elementsCount", elementsCount);
			fieldObject.put("value", jsonElements);
		} catch (JSONException e) {
			throw new GeneralDOAException(e);
		}
		return fieldObject;
	}

}

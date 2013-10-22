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
package pl.doa.renderer.json.decorator;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import pl.doa.GeneralDOAException;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.renderer.json.decorator.impl.BigDecimalFieldValueDecorator;
import pl.doa.renderer.json.decorator.impl.BooleanFieldValueDecorator;
import pl.doa.renderer.json.decorator.impl.DateFieldValueDecorator;
import pl.doa.renderer.json.decorator.impl.DoubleFieldValueDecorator;
import pl.doa.renderer.json.decorator.impl.IntegerFieldValueDecorator;
import pl.doa.renderer.json.decorator.impl.ListFieldValueDecorator;
import pl.doa.renderer.json.decorator.impl.LongFieldValueDecorator;
import pl.doa.renderer.json.decorator.impl.PasswordFieldValueDecorator;
import pl.doa.renderer.json.decorator.impl.ReferenceFieldValueDecorator;
import pl.doa.renderer.json.decorator.impl.StringFieldValueDecorator;

/**
 * @author activey
 * 
 */
public class JSONFieldValueDecoratorFactory {

	private static Map<DocumentFieldDataType, JSONFieldValueDecorator> decorators =
			new HashMap<DocumentFieldDataType, JSONFieldValueDecorator>();

	static {
		decorators.put(DocumentFieldDataType.string,
				new StringFieldValueDecorator());
		decorators.put(DocumentFieldDataType.list,
				new ListFieldValueDecorator());
		decorators.put(DocumentFieldDataType.integer,
				new IntegerFieldValueDecorator());
		decorators.put(DocumentFieldDataType.doubleprec,
				new DoubleFieldValueDecorator());
		decorators.put(DocumentFieldDataType.bigdecimal,
				new BigDecimalFieldValueDecorator());
		decorators.put(DocumentFieldDataType.longinteger,
				new LongFieldValueDecorator());
		decorators.put(DocumentFieldDataType.date,
				new DateFieldValueDecorator());
		decorators.put(DocumentFieldDataType.bool,
				new BooleanFieldValueDecorator());
		decorators.put(DocumentFieldDataType.reference,
				new ReferenceFieldValueDecorator());
		decorators.put(DocumentFieldDataType.password,
				new PasswordFieldValueDecorator());
	}

	public JSONObject decorate(IDocumentFieldValue fieldValue)
			throws GeneralDOAException {
		IDocumentFieldType fieldType = fieldValue.getFieldType();
		JSONFieldValueDecorator decorator =
				decorators.get(fieldType.getFieldDataType());
		if (decorator == null) {
			throw new GeneralDOAException(MessageFormat.format(
					"unable to find decorator for type: {0}", fieldValue
							.getClass().getName()));
		}
		decorator.setFactory(this);
		return decorator.decorate(fieldValue);
	}

}

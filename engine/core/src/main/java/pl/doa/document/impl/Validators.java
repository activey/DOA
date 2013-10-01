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
package pl.doa.document.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.document.IDocument;
import pl.doa.document.ValidationException;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.IDocumentFieldValue;

public class Validators {

    private final static Logger log = LoggerFactory.getLogger(Validators.class);

    public void validateField(IDocument document, IDocumentFieldType fieldType,
                              IDocumentFieldValue fieldValue) throws ValidationException {
        if (fieldType.isRequired()) {
            if (fieldValue == null || fieldValue.isEmpty()) {
                String name =
                        (String) fieldType.getAttribute("i18n.label",
                                fieldType.getName());
                throw new ValidationException(fieldType.getName(),
                        MessageFormat.format("Field {0} is required!", name));
            }
        }
        /*
		 * TODO !!! ZUPELNIE TO PRZEROBIC !!!
		 * 
		 * List<String> attributeNames = fieldType.getAttributeNames();
		for (String attrName : attributeNames) {
			Object attrValue = fieldType.getAttribute(attrName);
			invokeValidator(attrName, fieldValue, attrValue);
		}*/

    }

    private void invokeValidator(String validatorName,
                                 IDocumentFieldValue fieldValue, Object validatorAdvice)
            throws ValidationException {
        Method validationMethod = null;
        try {
            validationMethod =
                    this.getClass().getMethod(validatorName,
                            validatorAdvice.getClass(), fieldValue.getClass());
            validationMethod.invoke(this, validatorAdvice, fieldValue);
        } catch (NoSuchMethodException e) {
            return;
        } catch (IllegalArgumentException e) {
            return;
        } catch (IllegalAccessException e) {
            return;
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof ValidationException) {
                throw (ValidationException) targetException;
            }
            return;
        }
    }

    // ----------- implementacje walidatorow -----------

    public void minLength(Integer minLength, String valueSet)
            throws ValidationException {
        if (valueSet != null) {
            int length = valueSet.trim().length();
            if (length < minLength) {
                throw new ValidationException(null, "Field value is too short!");
            }
        }
    }

}

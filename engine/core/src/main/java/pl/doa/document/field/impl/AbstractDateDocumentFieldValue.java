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
package pl.doa.document.field.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import pl.doa.GeneralDOAException;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.IDocumentFieldValue;

/**
 * @author activey
 */
public abstract class AbstractDateDocumentFieldValue extends
        BaseAbstractFieldValue implements IDocumentFieldValue {

    public static final String DEFAULT_DATE_FORMAT = "dd.MM.yyyy HH:mm:ss zzz";

    protected abstract IDocumentFieldType getFieldTypeImpl();

    @Override
    public final IDocumentFieldType getFieldType() {
        return getFieldTypeImpl();
    }

    protected abstract String getFieldNameImpl();

    @Override
    public final String getFieldName() {
        return getFieldNameImpl();
    }

    protected abstract void setFieldNameImpl(String fieldName);

    @Override
    public final void setFieldName(String fieldName) {
        setFieldNameImpl(fieldName);
    }

    protected abstract int compareToImpl(Date fieldValue);

    @Override
    public final int compareTo(IDocumentFieldValue field) {
        if (field == null) {
            return 0;
        }
        Date dateValue = (Date) field.getFieldValue();
        return compareToImpl(dateValue);
    }

    protected abstract void copyFromImpl(IDocumentFieldValue otherField);

    @Override
    public final void copyFrom(IDocumentFieldValue otherField) {
        copyFromImpl(otherField);
    }

    protected abstract Date getFieldValueImpl();

    @Override
    public final Object getFieldValue() {
        return getFieldValueImpl();
    }

    protected abstract void setFieldValueImpl(Date fieldValue)
            throws GeneralDOAException;

    @Override
    public final void setFieldValue(Object fieldValue)
            throws GeneralDOAException {
        if (fieldValue instanceof String) {
            String stringValue = (String) fieldValue;
            if (stringValue.trim().length() == 0) {
                return;
            }
            // proba ustawienia wartosci jako timestamp
            try {
                long timestamp = Long.parseLong(stringValue);
                setFieldValueImpl(new Date(timestamp));
                return;
            } catch (NumberFormatException e) {
                // nie udalo sie ...
            }
            String dateFormat = DEFAULT_DATE_FORMAT;
            try {
                // pobieranie formatu z definicji pola
                IDocumentFieldType fieldDef = getFieldType();
                if (fieldDef != null) {
                    String fieldFormat =
                            (String) fieldDef.getAttribute("format");
                    if (fieldFormat != null) {
                        dateFormat = fieldFormat;
                    }
                }
                DateFormat format = new SimpleDateFormat(dateFormat);
                setFieldValue(format.parse(stringValue));
            } catch (ParseException e) {
                throw new GeneralDOAException(
                        "passed string value: [{0}] does not match date format: [{1}]",
                        stringValue, dateFormat);
            }
            return;
        }
        setFieldValueImpl((Date) fieldValue);
    }

    @Override
    public final String getFieldValueAsString() {
        Date fieldValue = getFieldValueImpl();
        if (fieldValue == null) {
            return null;
        }
        String dateFormat = DEFAULT_DATE_FORMAT;
        // pobieranie formatu z definicji pola
        IDocumentFieldType fieldDef = getFieldType();
        if (fieldDef != null) {
            String fieldFormat = (String) fieldDef.getAttribute("format");
            if (fieldFormat != null) {
                dateFormat = fieldFormat;
            }
        }
        DateFormat format = new SimpleDateFormat(dateFormat);
        return format.format(fieldValue);
    }

}

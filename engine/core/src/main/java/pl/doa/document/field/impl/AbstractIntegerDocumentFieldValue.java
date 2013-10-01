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

import pl.doa.GeneralDOAException;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.IDocumentFieldValue;

/**
 * @author activey
 */
public abstract class AbstractIntegerDocumentFieldValue extends
        BaseAbstractFieldValue implements IDocumentFieldValue {

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

    protected abstract int compareToImpl(Integer fieldValue);

    @Override
    public final int compareTo(IDocumentFieldValue field) {
        if (field == null) {
            return 0;
        }
        Integer integerValue = (Integer) field.getFieldValue();
        return compareToImpl(integerValue);
    }

    protected abstract void copyFromImpl(IDocumentFieldValue otherField);

    @Override
    public final void copyFrom(IDocumentFieldValue otherField) {
        copyFromImpl(otherField);
    }

    protected abstract Integer getFieldValueImpl();

    @Override
    public final Object getFieldValue() {
        return getFieldValueImpl();
    }

    protected abstract void setFieldValueImpl(Integer fieldValue)
            throws GeneralDOAException;

    @Override
    public final void setFieldValue(Object fieldValue)
            throws GeneralDOAException {
        if (fieldValue instanceof String) {
            setFieldValueImpl(new Integer((String) fieldValue));
            return;
        }
        setFieldValueImpl((Integer) fieldValue);
    }

    protected abstract String getFieldValueAsStringImpl();

    @Override
    public final String getFieldValueAsString() {
        return getFieldValueAsStringImpl();
    }

}

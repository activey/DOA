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
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.IListDocumentFieldValue;
import pl.doa.entity.IEntity;

/**
 * @author activey
 */
public abstract class AbstractListDocumentFieldValue extends
        BaseAbstractFieldValue implements IListDocumentFieldValue {

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

    protected abstract int compareToImpl(IDocumentFieldValue field);

    @Override
    public final int compareTo(IDocumentFieldValue field) {
        return compareToImpl(field);
    }

    @Override
    public final void copyFrom(IDocumentFieldValue otherField)
            throws GeneralDOAException {
        IListDocumentFieldValue otherList =
                (IListDocumentFieldValue) otherField;
        Iterable<IDocumentFieldValue> iterator = otherList.iterateFields();
        for (IDocumentFieldValue iDocumentFieldValue : iterator) {
            try {
                addFieldImpl(iDocumentFieldValue.getFieldName(),
                        iDocumentFieldValue.getFieldType().getFieldDataType())
                        .setFieldValue(iDocumentFieldValue.getFieldValue());
            } catch (Throwable t) {
                throw new GeneralDOAException(t);
            }
        }
    }

    @Override
    public final Iterable<IDocumentFieldValue> getFieldValue() {
        return iterateFieldsImpl();
    }

    protected abstract void setFieldValueImpl(
            Iterable<IDocumentFieldValue> value) throws GeneralDOAException;

    @Override
    public final void setFieldValue(Object fieldValue)
            throws GeneralDOAException {
        if (fieldValue instanceof Iterable<?>) {
            setFieldValueImpl((Iterable<IDocumentFieldValue>) fieldValue);
            return;
        }
        IDocumentFieldValue field = null;
        if (fieldValue instanceof String) {
            field = addStringField();
        }
        // TODO zaimplementowac pozostale typy

        if (field == null) {
            return;
        }
        field.setFieldValue(fieldValue);
    }

    protected abstract String getFieldValueAsStringImpl();

    @Override
    public final String getFieldValueAsString() {
        return getFieldValueAsStringImpl();
    }

    protected abstract IDocumentFieldValue addFieldImpl(String fieldName,
                                                        DocumentFieldDataType type) throws Throwable;

    @Override
    public final IDocumentFieldValue addField(String fieldName,
                                              DocumentFieldDataType type) throws GeneralDOAException {
        try {
            return addFieldImpl(fieldName, type);
        } catch (Throwable t) {
            throw new GeneralDOAException(t);
        }
    }

    protected abstract IDocumentFieldValue addStringFieldImpl(String fieldName,
                                                              String fieldValue) throws Throwable;

    @Override
    public final IDocumentFieldValue addStringField(String fieldName,
                                                    String fieldValue) throws GeneralDOAException {
        try {
            return addStringFieldImpl(fieldName, fieldValue);
        } catch (Throwable t) {
            throw new GeneralDOAException(t);
        }
    }

    protected abstract IDocumentFieldValue addStringFieldImpl(String fieldName)
            throws Throwable;

    @Override
    public final IDocumentFieldValue addStringField(String fieldName)
            throws GeneralDOAException {
        try {
            return addStringFieldImpl(fieldName);
        } catch (Throwable t) {
            throw new GeneralDOAException(t);
        }
    }

    protected abstract IDocumentFieldValue addReferenceFieldImpl(
            String fieldName) throws Throwable;

    @Override
    public final IDocumentFieldValue addReferenceField(String fieldName)
            throws GeneralDOAException {
        try {
            return addReferenceFieldImpl(fieldName);
        } catch (Throwable t) {
            throw new GeneralDOAException(t);
        }
    }

    protected abstract IDocumentFieldValue addReferenceFieldImpl(
            String fieldName, IEntity referenceEntity) throws Throwable;

    @Override
    public final IDocumentFieldValue addReferenceField(String fieldName,
                                                       IEntity referenceEntity) throws GeneralDOAException {
        try {
            return addReferenceFieldImpl(fieldName, referenceEntity);
        } catch (Throwable t) {
            throw new GeneralDOAException(t);
        }
    }

    @Override
    public final IDocumentFieldValue addField(DocumentFieldDataType type)
            throws GeneralDOAException {
        return addField(countFields() + 1 + "", type);
    }

    @Override
    public final IDocumentFieldValue addStringField()
            throws GeneralDOAException {
        return addStringField(countFields() + 1 + "");
    }

    @Override
    public final IDocumentFieldValue addReferenceField()
            throws GeneralDOAException {
        return addReferenceField(countFields() + 1 + "");
    }

    protected abstract Iterable<IDocumentFieldValue> iterateFieldsImpl();

    @Override
    public final Iterable<IDocumentFieldValue> iterateFields() {
        return iterateFieldsImpl();
    }

    protected abstract boolean isEmptyImpl();

    @Override
    public final boolean isEmpty() {
        return isEmptyImpl();
    }

}

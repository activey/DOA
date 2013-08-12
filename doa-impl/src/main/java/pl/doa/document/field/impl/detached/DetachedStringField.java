/**
 *
 */
package pl.doa.document.field.impl.detached;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.impl.AbstractStringDocumentFieldValue;

/**
 * @author activey
 */
public class DetachedStringField extends AbstractStringDocumentFieldValue {

    private static final long serialVersionUID = 2813972739677039671L;
    private String fieldName;
    private String fieldValue;
    private final IDocumentDefinition definition;

    public DetachedStringField(IDocumentDefinition definition,
                               String fieldName, String fieldValue) {
        this.definition = definition;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    @Override
    protected IDocumentFieldType getFieldTypeImpl() {
        return new DetachedFieldType(definition, DocumentFieldDataType.string,
                fieldName);
    }

    @Override
    protected String getFieldNameImpl() {
        return this.fieldName;
    }

    @Override
    protected void setFieldNameImpl(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    protected int compareToImpl(String fieldValue) {
        if (this.fieldValue == null || fieldValue != null) {
            return -1;
        }
        return this.fieldValue.compareTo(fieldValue);
    }

    @Override
    protected void copyFromImpl(IDocumentFieldValue otherField) {
        this.fieldValue = (String) otherField.getFieldValue();
    }

    @Override
    protected String getFieldValueImpl() {
        return this.fieldValue;
    }

    @Override
    protected void setFieldValueImpl(String fieldValue)
            throws GeneralDOAException {
        this.fieldValue = fieldValue;
    }

    @Override
    protected String getFieldValueAsStringImpl() {
        return this.fieldValue;
    }

    @Override
    public boolean isEmpty() {
        return fieldValue == null || fieldValue.length() == 0;
    }
}

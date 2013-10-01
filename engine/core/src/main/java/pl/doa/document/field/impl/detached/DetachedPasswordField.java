/**
 *
 */
package pl.doa.document.field.impl.detached;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.impl.AbstractPasswordDocumentFieldValue;

/**
 * @author activey
 */
public class DetachedPasswordField extends AbstractPasswordDocumentFieldValue {

    private static final long serialVersionUID = -234951171657205523L;
    private String fieldName;
    private String fieldValue;
    private final IDocumentDefinition definition;

    public DetachedPasswordField(IDocumentDefinition definition, String fieldName, String fieldValue) {
        this.definition = definition;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;

    }

    /* (non-Javadoc)
     * @see pl.doa.document.field.impl.AbstractPasswordDocumentFieldValue#getFieldTypeImpl()
     */
    @Override
    protected IDocumentFieldType getFieldTypeImpl() {
        return new DetachedFieldType(definition,
                DocumentFieldDataType.password, fieldName);
    }

    /* (non-Javadoc)
     * @see pl.doa.document.field.impl.AbstractPasswordDocumentFieldValue#getFieldNameImpl()
     */
    @Override
    protected String getFieldNameImpl() {
        return this.fieldName;
    }

    /* (non-Javadoc)
     * @see pl.doa.document.field.impl.AbstractPasswordDocumentFieldValue#setFieldNameImpl(java.lang.String)
     */
    @Override
    protected void setFieldNameImpl(String fieldName) {
        this.fieldName = fieldName;
    }

    /* (non-Javadoc)
     * @see pl.doa.document.field.impl.AbstractPasswordDocumentFieldValue#compareToImpl(java.lang.String)
     */
    @Override
    protected int compareToImpl(String fieldValue) {
        if (this.fieldValue == null || fieldValue != null) {
            return -1;
        }
        return this.fieldValue.compareTo(fieldValue);
    }

    /* (non-Javadoc)
     * @see pl.doa.document.field.impl.AbstractPasswordDocumentFieldValue#copyFromImpl(pl.doa.document.field.IDocumentFieldValue)
     */
    @Override
    protected void copyFromImpl(IDocumentFieldValue otherField) {
        this.fieldValue = (String) otherField.getFieldValue();
    }

    /* (non-Javadoc)
     * @see pl.doa.document.field.impl.AbstractPasswordDocumentFieldValue#getFieldValueImpl()
     */
    @Override
    protected String getFieldValueImpl() {
        return this.fieldValue;
    }

    /* (non-Javadoc)
     * @see pl.doa.document.field.impl.AbstractPasswordDocumentFieldValue#setFieldValueImpl(java.lang.String)
     */
    @Override
    protected void setFieldValueImpl(String fieldValue)
            throws GeneralDOAException {
        this.fieldValue = fieldValue;
    }

    /* (non-Javadoc)
     * @see pl.doa.document.field.impl.AbstractPasswordDocumentFieldValue#getFieldValueAsStringImpl()
     */
    @Override
    protected String getFieldValueAsStringImpl() {
        return this.fieldValue;
    }

    @Override
    public boolean isEmpty() {
        return fieldValue == null || fieldValue.length() == 0;
    }

}

/**
 *
 */
package pl.doa.document.field.impl.detached;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.impl.AbstractBooleanDocumentFieldValue;

/**
 * @author activey
 */
public class DetachedBooleanField extends AbstractBooleanDocumentFieldValue {

    private static final long serialVersionUID = 5316688996268102761L;

    private final static Logger log = LoggerFactory
            .getLogger(DetachedBooleanField.class);

    private String fieldName;
    private Boolean fieldValue;

    private final IDocumentDefinition definition;

    public DetachedBooleanField(IDocumentDefinition definition,
                                String fieldName, Object fieldValue) {
        this.definition = definition;
        this.fieldName = fieldName;
        try {
            setFieldValue(fieldValue);
        } catch (GeneralDOAException e) {
            log.error("", e);
        }
    }

    @Override
    protected IDocumentFieldType getFieldTypeImpl() {
        return definition.getFieldType(fieldName);
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
    protected int compareToImpl(Boolean fieldValue) {
        if (this.fieldValue == null || fieldValue != null) {
            return -1;
        }
        return this.fieldValue.compareTo(fieldValue);
    }

    @Override
    protected void copyFromImpl(IDocumentFieldValue otherField) {
        this.fieldValue = (Boolean) otherField.getFieldValue();
    }

    @Override
    protected Boolean getFieldValueImpl() {
        return this.fieldValue;
    }

    @Override
    protected void setFieldValueImpl(Boolean fieldValue)
            throws GeneralDOAException {
        this.fieldValue = fieldValue;
    }

    @Override
    protected String getFieldValueAsStringImpl() {
        return (fieldValue == null) ? null : this.fieldValue.toString();
    }

    @Override
    public boolean isEmpty() {
        return fieldValue == null;
    }

}

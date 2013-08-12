/**
 *
 */
package pl.doa.document.field.impl.detached;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.impl.AbstractDoubleDocumentFieldValue;

/**
 * @author activey
 */
public class DetachedDoubleField extends AbstractDoubleDocumentFieldValue {

    private static final long serialVersionUID = -4674616938122911826L;
    private final static Logger log = LoggerFactory
            .getLogger(DetachedDoubleField.class);
    private String fieldName;
    private Double fieldValue;
    private final IDocumentDefinition definition;

    public DetachedDoubleField(IDocumentDefinition definition,
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
        return new DetachedFieldType(definition,
                DocumentFieldDataType.doubleprec, fieldName);
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
    protected int compareToImpl(Double fieldValue) {
        if (this.fieldValue == null || fieldValue != null) {
            return -1;
        }
        return this.fieldValue.compareTo(fieldValue);
    }

    @Override
    protected void copyFromImpl(IDocumentFieldValue otherField) {
        this.fieldValue = (Double) otherField.getFieldValue();
    }

    @Override
    protected Double getFieldValueImpl() {
        return this.fieldValue;
    }

    @Override
    protected void setFieldValueImpl(Double fieldValue)
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

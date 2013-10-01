/**
 *
 */
package pl.doa.document.field.impl.detached;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.impl.AbstractBigDecimalDocumentFieldValue;

/**
 * @author activey
 */
public class DetachedBigDecimalField extends
        AbstractBigDecimalDocumentFieldValue {

    private static final long serialVersionUID = 9213010634431998560L;
    private final static Logger log = LoggerFactory
            .getLogger(DetachedBigDecimalField.class);
    private String fieldName;
    private BigDecimal fieldValue;
    private final IDocumentDefinition definition;

    public DetachedBigDecimalField(IDocumentDefinition definition, String fieldName, Object fieldValue) {
        this.definition = definition;
        this.fieldName = fieldName;
        try {
            setFieldValue(fieldValue);
        } catch (GeneralDOAException e) {
            log.error("", e);
        }
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
    protected int compareToImpl(BigDecimal fieldValue) {
        if (this.fieldValue == null || fieldValue != null) {
            return -1;
        }
        return this.fieldValue.compareTo(fieldValue);
    }

    @Override
    protected void copyFromImpl(IDocumentFieldValue otherField) {
        this.fieldValue = (BigDecimal) otherField.getFieldValue();
    }

    @Override
    protected BigDecimal getFieldValueImpl() {
        return this.fieldValue;
    }

    @Override
    protected void setFieldValueImpl(BigDecimal fieldValue)
            throws GeneralDOAException {
        this.fieldValue = fieldValue;
    }

    @Override
    protected String getFieldValueAsStringImpl() {
        return (fieldValue == null) ? null : this.fieldValue.toString();
    }

    @Override
    protected IDocumentFieldType getFieldTypeImpl() {
        return new DetachedFieldType(definition,
                DocumentFieldDataType.bigdecimal, fieldName);
    }

    @Override
    public boolean isEmpty() {
        return fieldValue == null;
    }

}

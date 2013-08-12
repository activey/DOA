/**
 *
 */
package pl.doa.document.field.impl.detached;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.impl.AbstractDateDocumentFieldValue;

/**
 * @author activey
 */
public class DetachedDateField extends AbstractDateDocumentFieldValue {

    private static final long serialVersionUID = 5875625693230782693L;
    private final static Logger log = LoggerFactory
            .getLogger(DetachedDateField.class);
    private String fieldName;
    private Date fieldValue;
    private final IDocumentDefinition definition;

    public DetachedDateField(IDocumentDefinition definition, String fieldName, Object fieldValue) {
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
                DocumentFieldDataType.date, fieldName);
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
    protected int compareToImpl(Date fieldValue) {
        if (this.fieldValue == null || fieldValue != null) {
            return -1;
        }
        return this.fieldValue.compareTo(fieldValue);
    }

    @Override
    protected void copyFromImpl(IDocumentFieldValue otherField) {
        this.fieldValue = (Date) otherField.getFieldValue();
    }

    @Override
    protected Date getFieldValueImpl() {
        return this.fieldValue;
    }

    @Override
    protected void setFieldValueImpl(Date fieldValue)
            throws GeneralDOAException {
        this.fieldValue = fieldValue;
    }

    @Override
    public boolean isEmpty() {
        return fieldValue == null;
    }

}

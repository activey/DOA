/**
 *
 */
package pl.doa.document.field.impl.detached;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.impl.AbstractReferenceDocumentFieldValue;
import pl.doa.entity.IEntity;

/**
 * @author activey
 */
public class DetachedReferenceField extends AbstractReferenceDocumentFieldValue {

    private static final long serialVersionUID = 7692911737843874137L;
    private final static Logger log = LoggerFactory
            .getLogger(DetachedReferenceField.class);
    private String fieldName;
    private IEntity fieldValue;
    private final IDocumentDefinition definition;

    public DetachedReferenceField(IDocumentDefinition definition,
                                  String fieldName, Object fieldValue, IDOA doa) {
        super(doa);
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
                DocumentFieldDataType.reference, fieldName);
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
    protected int compareToImpl(IEntity fieldValue) {
        if (this.fieldValue == null || fieldValue != null) {
            return -1;
        }
        return (this.fieldValue.equals(fieldValue)) ? 0 : -1;
    }

    @Override
    protected IEntity getFieldValueImpl() {
        return this.fieldValue;
    }

    @Override
    protected void setFieldValueImpl(IEntity fieldValue)
            throws GeneralDOAException {
        this.fieldValue = fieldValue;
    }

    @Override
    protected String getFieldValueAsStringImpl() {
        return (fieldValue == null) ? null : this.fieldValue.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DetachedReferenceField)) {
            return false;
        }
        DetachedReferenceField field = (DetachedReferenceField) obj;
        IEntity reference = (IEntity) field.getFieldValue();
        if (reference == null && fieldValue == null) {
            return true;
        }
        if (reference == null) {
            return false;
        }
        return reference.getId() == fieldValue.getId();
    }

    @Override
    public boolean isEmpty() {
        return fieldValue == null;
    }

}

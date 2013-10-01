/**
 *
 */
package pl.doa.document.field.impl.detached;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.IListDocumentFieldValue;
import pl.doa.document.field.impl.AbstractListDocumentFieldValue;
import pl.doa.document.impl.DetachedDocument;
import pl.doa.entity.IEntity;
import pl.doa.utils.IteratorIterable;

/**
 * @author activey
 */
public class DetachedListFieldValue extends AbstractListDocumentFieldValue
        implements IListDocumentFieldValue, Serializable {

    private static final long serialVersionUID = 5143673552935222328L;

    private final static Logger log = LoggerFactory
            .getLogger(DetachedListFieldValue.class);

    private Map<String, IDocumentFieldValue> innerFields =
            new HashMap<String, IDocumentFieldValue>();
    private String fieldName;

    private transient IDOA doa;

    private final IDocumentDefinition definition;

    public DetachedListFieldValue(IDocumentDefinition definition,
                                  String fieldName, Iterable<IDocumentFieldValue> fieldValue, IDOA doa) {
        this.definition = definition;
        this.fieldName = fieldName;
        this.doa = doa;
        if (fieldValue == null) {
            return;
        }
        try {
            setFieldValueImpl(fieldValue);
        } catch (GeneralDOAException e) {
            log.error("", e);
        }
    }

    @Override
    public IDocumentFieldValue getListField(String fieldName) {
        IDocumentFieldValue innerField = innerFields.get(fieldName);
        return innerField;
    }

    @Override
    public IDocumentFieldValue addFieldImpl(String fieldName,
                                            DocumentFieldDataType type) throws GeneralDOAException {
        IDocumentFieldValue detached =
                DetachedDocument.buildDetachedField(doa, null, type, fieldName,
                        null);
        innerFields.put(fieldName, detached);
        return detached;
    }

    @Override
    public IDocumentFieldValue addStringFieldImpl(String fieldName,
                                                  String fieldValue) throws GeneralDOAException {
        IDocumentFieldValue detached =
                DetachedDocument.buildDetachedField(doa, null,
                        DocumentFieldDataType.string, fieldName, fieldValue);
        innerFields.put(fieldName, detached);
        return detached;
    }

    @Override
    public IDocumentFieldValue addStringFieldImpl(String fieldName)
            throws GeneralDOAException {
        return addStringField(fieldName, null);
    }

    @Override
    public IDocumentFieldValue addReferenceFieldImpl(String fieldName)
            throws GeneralDOAException {
        IDocumentFieldValue detached =
                DetachedDocument.buildDetachedField(doa, null,
                        DocumentFieldDataType.reference, fieldName, null);
        innerFields.put(fieldName, detached);
        return detached;
    }

    @Override
    public IDocumentFieldValue addReferenceFieldImpl(String fieldName,
                                                     IEntity referenceEntity) throws GeneralDOAException {
        IDocumentFieldValue detached =
                DetachedDocument.buildDetachedField(doa, null,
                        DocumentFieldDataType.reference, fieldName, null);
        detached.setFieldValue(referenceEntity);
        innerFields.put(fieldName, detached);
        return detached;
    }

    @Override
    public Iterable<IDocumentFieldValue> iterateFieldsImpl() {
        return new IteratorIterable<IDocumentFieldValue>(innerFields.values()
                .iterator());
    }

    @Override
    public boolean isEmptyImpl() {
        return innerFields.isEmpty();
    }

    @Override
    protected IDocumentFieldType getFieldTypeImpl() {
        return new DetachedFieldType(definition, DocumentFieldDataType.list,
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
    protected int compareToImpl(IDocumentFieldValue field) {
        // TODO !!!
        return 0;
    }

    @Override
    protected void setFieldValueImpl(Iterable<IDocumentFieldValue> value)
            throws GeneralDOAException {
        for (IDocumentFieldValue field : value) {
            Object fieldValue = field.getFieldValue();
            IDocumentFieldValue newInner =
                    addFieldImpl(field.getFieldName(), field.getFieldType()
                            .getFieldDataType());
            newInner.setFieldValue(fieldValue);
        }
    }

    @Override
    protected String getFieldValueAsStringImpl() {
        StringBuffer buffer = new StringBuffer();
        for (IDocumentFieldValue field : iterateFields()) {
            buffer.append(field.getFieldValueAsString()).append(", ");
        }
        return buffer.toString();
    }

    @Override
    public long countFields() {
        return innerFields.keySet().size();
    }

    public void setDoa(IDOA doa) {
        this.doa = doa;
    }

    @Override
    public void remove() {
        this.innerFields.clear();
    }
}

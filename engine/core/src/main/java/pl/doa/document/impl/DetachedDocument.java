package pl.doa.document.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.DocumentValidationException;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.IDocumentFieldEvaluator;
import pl.doa.document.alignment.IDocumentAligner;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.impl.detached.DetachedBigDecimalField;
import pl.doa.document.field.impl.detached.DetachedBooleanField;
import pl.doa.document.field.impl.detached.DetachedDateField;
import pl.doa.document.field.impl.detached.DetachedDoubleField;
import pl.doa.document.field.impl.detached.DetachedIntegerField;
import pl.doa.document.field.impl.detached.DetachedListFieldValue;
import pl.doa.document.field.impl.detached.DetachedLongField;
import pl.doa.document.field.impl.detached.DetachedPasswordField;
import pl.doa.document.field.impl.detached.DetachedReferenceField;
import pl.doa.document.field.impl.detached.DetachedStringField;
import pl.doa.entity.DetachedEntity;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityProxy;

public class DetachedDocument extends DetachedEntity implements IDocument,
        IEntityProxy<IDocument>, Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory
            .getLogger(DetachedDocument.class);

    private transient IDocumentDefinition definition;

    private Map<String, IDocumentFieldValue> fields = new HashMap<String, IDocumentFieldValue>();

    public DetachedDocument(IDOA doa, IDocumentDefinition definition) {
        super(doa);
        this.definition = definition;
    }

    public DetachedDocument(IDOA doa, IDocument document) {
        super(doa, document);
    }

    @Override
    public IDocumentDefinition getDefinition() {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            IDocument doc = (IDocument) storedEntity;
            return doc.getDefinition();
        }
        return this.definition;
    }

    @Override
    public boolean isFieldAvailable(String fieldName) {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            IDocument doc = (IDocument) storedEntity;
            return doc.isFieldAvailable(fieldName);
        }
        return fields.containsKey(fieldName);
    }

    @Override
    public void setFieldValue(String fieldName, Object fieldValue)
            throws GeneralDOAException {
        setFieldValue(fieldName, fieldValue, null);
    }

    @Override
    public void setFieldValue(String fieldName, Object fieldValue,
                              DocumentFieldDataType dataType) throws GeneralDOAException {
        if (fieldName == null) {
            return;
        }
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            IDocument doc = (IDocument) storedEntity;
            doc.setFieldValue(fieldName, fieldValue);
            return;
        }
        if (fieldValue == null) {
            fields.remove(fieldName);
            return;
        }
        IDocumentFieldValue detachedField = getField(fieldName, true);
        if (detachedField == null) {
            return;
        }
        detachedField.setFieldValue(fieldValue);
    }

    @Override
    public void setFieldValue(String fieldName, IDocumentFieldValue otherField)
            throws GeneralDOAException {
        /*
		 * if (otherField == null) { fields.remove(fieldName); return; }
		 */
        if (otherField == null) {
            return;
        }
        setFieldValue(fieldName, otherField.getFieldValue());
    }

    @Override
    public IDocumentFieldValue getField(String fieldName, boolean createIfNull)
            throws GeneralDOAException {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            IDocument doc = (IDocument) storedEntity;
            return doc.getField(fieldName, createIfNull);
        }
        IDocumentFieldValue field = fields.get(fieldName);
        if (fieldName.indexOf(".") > 0) {
            StringTokenizer tokenizer = new StringTokenizer(fieldName, ".");
            IDocumentFieldValue inner = AbstractDocument.getInnerField(this,
                    tokenizer, null, createIfNull, null);
            if (inner != null) {
                return inner;
            }
        }
        if (field == null && createIfNull) {
            IDocumentFieldType fieldType = definition.getFieldType(fieldName);
            if (fieldType == null) {
                return null;
            }
            DocumentFieldDataType dataType = fieldType.getFieldDataType();
            field = DetachedDocument.buildDetachedField(doa, definition,
                    dataType, fieldName, null);
            fields.put(fieldName, field);
        }
        return field;
    }

    @Override
    public IDocumentFieldValue getField(String fieldName) {
        try {
            return getField(fieldName, false);
        } catch (GeneralDOAException e) {
            return null;
        }
    }

    @Override
    public Object getFieldValue(String fieldName) {
        return getFieldValue(fieldName, null);
    }

    @Override
    public Object getFieldValue(String fieldName, Object whenNull) {
        IDocumentFieldValue field = getField(fieldName);
        if (field == null) {
            return whenNull;
        }
        return field.getFieldValue();
    }

    @Override
    public Iterator<String> getFieldsNames() {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            IDocument doc = (IDocument) storedEntity;
            return doc.getFieldsNames();
        }
        return fields.keySet().iterator();
    }

    @Override
    public Iterator<IDocumentFieldValue> getFields() {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            IDocument doc = (IDocument) storedEntity;
            return doc.getFields();
        }
        return fields.values().iterator();
    }

    @Override
    public String getFieldValueAsString(String fieldName) {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            IDocument doc = (IDocument) storedEntity;
            return doc.getFieldValueAsString(fieldName);
        }
        IDocumentFieldValue field = getField(fieldName);
        if (field == null) {
            return null;
        }
        return field.getFieldValueAsString();
    }

    @Override
    public void setDefinition(IDocumentDefinition definition) {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            IDocument doc = (IDocument) storedEntity;
            doc.setDefinition(definition);
            return;
        }
        this.definition = definition;
    }

    @Override
    public void validateDocument(IDocumentDefinition definition)
            throws DocumentValidationException {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            IDocument doc = (IDocument) storedEntity;
            doc.validateDocument(definition);
            return;
        }
        AbstractDocument.validateDocument(this, definition);
    }

    @Override
    public void validateDocument() throws DocumentValidationException {
        validateDocument(definition);
    }

    @Override
    public boolean isDefinedBy(IDocumentDefinition documentDefinition) {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            IDocument doc = (IDocument) storedEntity;
            return doc.isDefinedBy(documentDefinition);
        }
        return AbstractDocument.isDefinedBy(this, documentDefinition);
    }

    @Override
    public boolean isDefinedBy(String documentDefinitionLocation) {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            IDocument doc = (IDocument) storedEntity;
            return doc.isDefinedBy(documentDefinitionLocation);
        }
        return AbstractDocument.isDefinedBy(this, (IDocumentDefinition) doa
                .lookupEntityByLocation(documentDefinitionLocation));
    }

    @Override
    public void setFields(List<IDocumentFieldValue> list) {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            IDocument doc = (IDocument) storedEntity;
            doc.setFields(list);
            return;
        }
        for (IDocumentFieldValue fieldValue : list) {
            fields.put(fieldValue.getFieldName(), fieldValue);
        }
    }

    @Override
    public IDocument align(IDocumentDefinition toDefinition)
            throws GeneralDOAException {
        return AbstractDocument.align(doa, this, toDefinition);
    }

    @Override
    public IDocumentAligner getAligner(IDocumentDefinition toDefinition) {
        return AbstractDocument.getAligner(doa, this.getDefinition(),
                toDefinition);
    }

    @Override
    public void copyFieldFrom(IDocument input, String fieldName)
            throws GeneralDOAException {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            IDocument doc = (IDocument) storedEntity;
            doc.copyFieldFrom(input, fieldName);
            return;
        }
        IDocumentFieldValue fieldValue = input.getField(fieldName);
        if (fieldValue == null) {
            return;
        }
        fields.put(fieldName, DetachedDocument.buildDetachedField(doa,
                definition, fieldValue));
    }

    @Override
    public void copyFieldFrom(IDocument input, String sourceFieldName,
                              String destFileName) throws GeneralDOAException {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            IDocument doc = (IDocument) storedEntity;
            doc.copyFieldFrom(input, sourceFieldName, destFileName);
            return;
        }
        IDocumentFieldValue fieldValue = input.getField(sourceFieldName);
        if (fieldValue == null) {
            return;
        }
        fields.put(destFileName, DetachedDocument.buildDetachedField(doa,
                definition, fieldValue));
    }

    @Override
    protected IEntity buildAttached(IEntitiesContainer container)
            throws GeneralDOAException {
        IDocument stored = doa.createDocument(getName(), definition, container);
        Iterator<String> fields = getFieldsNames();
        while (fields.hasNext()) {
            String fieldName = fields.next();
            stored.copyFieldFrom(this, fieldName);
        }
        return stored;
    }

    public static IDocumentFieldValue buildDetachedField(IDOA doa,
                                                         IDocumentDefinition definition, IDocumentFieldValue fieldValue)
            throws GeneralDOAException {
        return DetachedDocument.buildDetachedField(doa, definition, fieldValue
                .getFieldType().getFieldDataType(), fieldValue.getFieldName(),
                fieldValue.getFieldValue());
    }

    public static IDocumentFieldValue buildDetachedField(IDOA doa,
                                                         IDocumentDefinition definition, DocumentFieldDataType dataType,
                                                         String fieldName, Object fieldValue) throws GeneralDOAException {
        switch (dataType) {
            case string:
                return new DetachedStringField(definition, fieldName,
                        (String) fieldValue);
            case password:
                return new DetachedPasswordField(definition, fieldName,
                        (String) fieldValue);
            case bigdecimal:
                return new DetachedBigDecimalField(definition, fieldName,
                        fieldValue);
            case doubleprec:
                return new DetachedDoubleField(definition, fieldName, fieldValue);
            case integer:
                return new DetachedIntegerField(definition, fieldName, fieldValue);
            case longinteger:
                return new DetachedLongField(definition, fieldName, fieldValue);
            case bool:
                return new DetachedBooleanField(definition, fieldName, fieldValue);
            case date:
                return new DetachedDateField(definition, fieldName, fieldValue);
            case list: {
                Iterable<IDocumentFieldValue> iterable = null;
                try {
                    iterable = (Iterable<IDocumentFieldValue>) fieldValue;
                    return new DetachedListFieldValue(definition, fieldName,
                            iterable, doa);
                } catch (ClassCastException e) {
                    throw new GeneralDOAException("Wrong value passed!");
                }
            }
            case reference: {
                return new DetachedReferenceField(definition, fieldName,
                        fieldValue, doa);
            }
            default:
                break;
        }
        return null;
    }

    public final IDocument createCopy() throws GeneralDOAException {
        return createCopy(null);
    }

    @Override
    public final IDocument createCopy(IDocumentFieldEvaluator evaluator)
            throws GeneralDOAException {
        return AbstractDocument.createCopy(doa, this, evaluator);
    }

    @Override
    public IDocument get() {
        IDocument stored = (IDocument) getStoredEntity();
        if (stored == null) {
            try {
                return (IDocument) store("/tmp");
            } catch (GeneralDOAException e) {
                log.error("", e);
            }
        }
        return stored;
    }

    @Override
    public void detach() {
        super.detach();
        this.fields.clear();
    }

}

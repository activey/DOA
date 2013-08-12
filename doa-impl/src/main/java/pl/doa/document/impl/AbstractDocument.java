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
package pl.doa.document.impl;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.document.DocumentValidationException;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.IDocumentFieldEvaluator;
import pl.doa.document.ValidationException;
import pl.doa.document.alignment.IDocumentAligner;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.IListDocumentFieldValue;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.entity.impl.AbstractEntity;
import pl.doa.renderer.IRenderer;
import pl.doa.renderer.template.BasicTemplateFinder;
import pl.doa.resource.IStaticResource;
import pl.doa.utils.IteratorIterable;

/**
 * @author activey
 */
public abstract class AbstractDocument extends AbstractEntity implements
        IDocument {

    private final static Logger log = LoggerFactory
            .getLogger(AbstractDocument.class);

    public AbstractDocument(IDOA doa) {
        super(doa);
    }

    protected abstract IDocumentDefinition getDefinitionImpl();

    public final IDocumentDefinition getDefinition() {
        return getDefinitionImpl();
    }

    protected abstract boolean isFieldAvailableImpl(String fieldName);

    public final boolean isFieldAvailable(String fieldName) {
        return isFieldAvailableImpl(fieldName);
    }

    @Override
    public final void setFieldValue(String fieldName,
                                    IDocumentFieldValue otherField) throws GeneralDOAException {
        if (otherField == null) {
            return;
        }
        try {
            setFieldValue(fieldName, otherField.getFieldValue());
        } catch (Throwable e) {
            throw new GeneralDOAException(e);
        }
    }

    @Override
    public final void setFieldValue(String fieldName, Object fieldValue)
            throws GeneralDOAException {
        setFieldValue(fieldName, fieldValue, null);
    }

    @Override
    public final void setFieldValue(String fieldName, Object fieldValue,
                                    DocumentFieldDataType dataType) throws GeneralDOAException {
        IDocumentFieldValue documentField = getField(fieldName, true, dataType);
        if (documentField == null) {
            return;
        }
        documentField.setFieldValue(fieldValue);
    }

    protected abstract IDocumentFieldValue getFieldImpl(String fieldName);

    protected abstract void addFieldImpl(IDocumentFieldValue fieldValue);

    private final IDocumentFieldValue getField(String fieldName,
                                               boolean createIfNull, DocumentFieldDataType dataType)
            throws GeneralDOAException {
        if (fieldName.indexOf(".") > 0) {
            StringTokenizer tokenizer = new StringTokenizer(fieldName, ".");
            IDocumentFieldValue inner = AbstractDocument.getInnerField(this,
                    tokenizer, null, createIfNull, dataType);
            if (inner != null) {
                return inner;
            }
        }
        IDocumentFieldValue fieldValue = getFieldImpl(fieldName);
        if (fieldValue == null) {
            if (!createIfNull) {
                return null;
            }
            IDocumentFieldType field = getDefinition().getFieldType(fieldName);
            if (field == null) {
                return null;
            }
            try {
                fieldValue = field.createValueInstance(fieldName);
            } catch (GeneralDOAException e) {
                log.error("", e);
                return null;
            }
            addFieldImpl(fieldValue);
        }
        return fieldValue;
    }

    public final IDocumentFieldValue getField(String fieldName,
                                              boolean createIfNull) throws GeneralDOAException {
        return getField(fieldName, createIfNull, null);
    }

    public final IDocumentFieldValue getField(String fieldName) {
        try {
            return getField(fieldName, false);
        } catch (GeneralDOAException e) {
            return null;
        }
    }

    protected abstract Object getFieldValueImpl(String fieldName);

    public final Object getFieldValue(String fieldName) {
        return getFieldValueImpl(fieldName);
    }

    @Override
    public final Object getFieldValue(String fieldName, Object whenNull) {
        Object value = getFieldValue(fieldName);
        if (value == null) {
            value = whenNull;
        }
        return value;
    }

    protected abstract Iterator<String> getFieldsNamesImpl();

    public final Iterator<String> getFieldsNames() {
        return getFieldsNamesImpl();
    }

    protected abstract Iterator<IDocumentFieldValue> getFieldsImpl();

    public final Iterator<IDocumentFieldValue> getFields() {
        return getFieldsImpl();
    }

    protected abstract String getFieldValueAsStringImpl(String fieldName);

    public final String getFieldValueAsString(String fieldName) {
        return getFieldValueAsStringImpl(fieldName);
    }

    protected abstract void setDefinitionImpl(IDocumentDefinition definition);

    public final void setDefinition(IDocumentDefinition definition) {
        setDefinitionImpl(definition);
    }

    public final boolean isDefinedBy(IDocumentDefinition documentDefinition) {
        return AbstractDocument.isDefinedBy(this, documentDefinition);
    }

    public static final boolean isDefinedBy(IDocument document,
                                            IDocumentDefinition documentDefinition) {
        IDocumentDefinition thisDefinition = document.getDefinition();
        if (thisDefinition.getId() == documentDefinition.getId()) {
            return true;
        }
        return (thisDefinition.isDescendantOf(documentDefinition));
    }

    public final boolean isDefinedBy(String documentDefinitionLocation) {
        IDocumentDefinition definition = (IDocumentDefinition) doa
                .lookupEntityByLocation(documentDefinitionLocation);
        if (definition == null) {
            log.error(MessageFormat.format(
                    "unable to find document definition under location [{0}]",
                    documentDefinitionLocation));
            return false;
        }
        return isDefinedBy(definition);
    }

    protected abstract void setFieldsImpl(List<IDocumentFieldValue> list);

    public final void setFields(List<IDocumentFieldValue> list) {
        setFieldsImpl(list);
    }

    public static IDocumentAligner getAligner(IDOA doa,
                                              IDocumentDefinition fromDefinition, IDocumentDefinition toDefinition) {
        return doa.lookupAligner(fromDefinition, toDefinition);
    }

    public final IDocumentAligner getAligner(IDocumentDefinition toDefinition) {
        return AbstractDocument.getAligner(doa, this.getDefinition(),
                toDefinition);
    }

    public static IDocument align(IDOA doa, IDocument fromDocument,
                                  IDocumentDefinition toDefinition) throws GeneralDOAException {
        IDocumentDefinition fromDefinition = fromDocument.getDefinition();
        IDocumentAligner aligner = AbstractDocument.getAligner(doa,
                fromDefinition, toDefinition);
        if (aligner == null) {
            log.error("Aligner not found!");
            throw new GeneralDOAException(MessageFormat.format(
                    "unable to find aligner: {0} -> {1}",
                    fromDefinition.getLocation(), toDefinition.getLocation()));
        }
        log.debug("found aligner node: " + aligner.getLocation());
        return aligner.align(fromDocument);
    }

    public final IDocument align(IDocumentDefinition toDefinition)
            throws GeneralDOAException {
        return AbstractDocument.align(doa, this, toDefinition);
    }

    @Override
    public final void copyFieldFrom(IDocument input, String fieldName)
            throws GeneralDOAException {
        if (input == null) {
            throw new GeneralDOAException("Input document is null!");
        }
        IDocumentFieldValue field = getField(fieldName, true);
        IDocumentFieldValue copyFrom = input.getField(fieldName);
        if (copyFrom == null) {
            return;
        }
        field.copyFrom(copyFrom);
    }

    public final void copyFieldFrom(IDocument input, String sourceFieldName,
                                    String destFileName) throws GeneralDOAException {
        if (input == null) {
            throw new GeneralDOAException("Input document is null!");
        }
        IDocumentFieldValue field = getField(destFileName, true);
        IDocumentFieldValue copyFrom = input.getField(sourceFieldName);
        if (copyFrom == null) {
            return;
        }
        field.copyFrom(copyFrom);
    }

    @Override
    public final void validateDocument(IDocumentDefinition definition)
            throws DocumentValidationException {
        AbstractDocument.validateDocument(this, definition);
    }

    @Override
    public final void validateDocument() throws DocumentValidationException {
        validateDocument(getDefinition());
    }

    @Override
    public final IDocument createCopy() throws GeneralDOAException {
        return createCopy(null);
    }

    @Override
    public final IDocument createCopy(IDocumentFieldEvaluator evaluator)
            throws GeneralDOAException {
        return AbstractDocument.createCopy(doa, this, evaluator);
    }

    public static IDocumentFieldValue getInnerField(IDocument document,
                                                    StringTokenizer tokenizer, IListDocumentFieldValue parentField,
                                                    boolean createIfNull, DocumentFieldDataType dataType)
            throws GeneralDOAException {
        if (tokenizer.hasMoreTokens()) {
            String namePart = tokenizer.nextToken();
            IDocumentFieldValue field = null;

            if (parentField == null) {
                field = document.getField(namePart, createIfNull);
            } else {
                field = parentField.getListField(namePart);
                if (field == null && createIfNull) {
                    IDocumentFieldType fieldType = parentField.getFieldType();
                    DocumentFieldDataType innerFieldType = dataType;
                    String type = (String) fieldType.getAttribute("dataType");
                    if (type != null) {
                        innerFieldType = DocumentFieldDataType.valueOf(type);
                    }
                    if (innerFieldType == null) {
                        innerFieldType = DocumentFieldDataType.string;
                    }
                    try {
                        field = parentField.addField(namePart, innerFieldType);
                    } catch (GeneralDOAException e) {
                        log.error("", e);
                    }
                }
            }

            if (field == null) {
                return null;
            }
            if (!(field instanceof IListDocumentFieldValue)) {
                if (!tokenizer.hasMoreTokens()) {
                    return field;
                }
                return null;
            }
            IListDocumentFieldValue listField = (IListDocumentFieldValue) field;
            return AbstractDocument.getInnerField(document, tokenizer,
                    listField, createIfNull, dataType);
        }
        return null;
    }

    public final static IDocument createCopy(IDOA doa, IDocument document,
                                             IDocumentFieldEvaluator evaluator) throws GeneralDOAException {
        Iterator<IDocumentFieldValue> fields = document.getFields();
        DetachedDocument copy = new DetachedDocument(doa,
                document.getDefinition());
        copy.setName(document.getName());
        copy.setId(document.getId());
        while (fields.hasNext()) {
            IDocumentFieldValue fieldValue = fields.next();
            boolean toCopy = true;
            if (evaluator != null) {
                toCopy = evaluator.evaluate(fieldValue);
            }
            if (!toCopy) {
                continue;
            }
            copy.setFieldValue(fieldValue.getFieldName(), fieldValue);
        }
        return copy;
    }

    public final static void validateDocument(IDocument document,
                                              IDocumentDefinition definition) throws DocumentValidationException {

        IDocumentDefinition docDefinition = (definition == null) ? document
                .getDefinition() : definition;

        // ustalanie bledow walidacji dla pol
        Validators validators = new Validators();
        DocumentValidationException documentErrors = null;
        Iterable<String> fieldNames = new IteratorIterable<String>(
                docDefinition.getFieldNames());
        for (String fieldName : fieldNames) {
            IDocumentFieldType fieldType = docDefinition
                    .getFieldType(fieldName);
            IDocumentFieldValue fieldValue = document.getField(fieldName);
            try {
                validators.validateField(document, fieldType, fieldValue);
            } catch (ValidationException e) {
                if (documentErrors == null) {
                    documentErrors = new DocumentValidationException();
                }
                documentErrors.addFieldException(fieldType.getName(),
                        e.getMessage());
            }
        }
        if (documentErrors != null) {
            throw documentErrors;
        }
    }

}

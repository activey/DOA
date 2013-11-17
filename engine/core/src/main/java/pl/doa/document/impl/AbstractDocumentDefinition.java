/**
 *
 */
package pl.doa.document.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.entity.IEntity;
import pl.doa.entity.impl.AbstractEntity;
import pl.doa.utils.IteratorCollection;

/**
 * @author activey
 */
public abstract class AbstractDocumentDefinition extends AbstractEntity
        implements IDocumentDefinition {

    public AbstractDocumentDefinition(IDOA doa) {
        super(doa);
    }

    @Override
    public final IDocumentFieldType addField(String fieldName,
                                             DocumentFieldDataType dataType) throws GeneralDOAException {
        return addFieldImpl(fieldName, dataType);
    }

    @Override
    public final IDocumentFieldType addField(String fieldName,
                                             DocumentFieldDataType dataType, boolean required,
                                             boolean authorizable) throws GeneralDOAException {
        return addFieldImpl(fieldName, dataType, required, authorizable);
    }

    @Override
    public final void modifyField(String fieldName, String newName,
                                  DocumentFieldDataType newType, boolean required,
                                  boolean authorizable) throws GeneralDOAException {
        modifyFieldImpl(fieldName, newName, newType, required, authorizable);
    }

    @Override
    public final void removeField(String fieldName) {
        removeFieldImpl(fieldName);
    }

    @Override
    public final IDocument createDocumentInstance(String name)
            throws GeneralDOAException {
        return getDoa().createDocument(name, this);
    }

    @Override
    public final IDocument createDocumentInstance() throws GeneralDOAException {
        return getDoa().createDocument(this);
    }

    @Override
    public final IDocument createDocumentInstance(String name,
                                                  IEntitiesContainer container) throws GeneralDOAException {
        return getDoa().createDocument(name, this, container);
    }

    @Override
    public final Iterator<String> getFieldNames() {
        Set<String> fieldNames = new HashSet<String>();
        Iterator<String> thisFields = getFieldNamesImpl();
        if (thisFields != null) {
            fieldNames.addAll(new IteratorCollection<String>(thisFields));
        }
        IEntity ancestor = getAncestor();
        if (ancestor != null) {
            IDocumentDefinition ancestorDefinition =
                    (IDocumentDefinition) ancestor;
            fieldNames.addAll(new IteratorCollection<String>(ancestorDefinition
                    .getFieldNames()));
        }
        return fieldNames.iterator();
    }

    @Override
    public final IDocumentFieldType getFieldType(String fieldName) {
        IDocumentFieldType fieldType = getFieldTypeImpl(fieldName);
        if (fieldType != null) {
            return fieldType;
        }
        IEntity ancestor = getAncestor();
        if (ancestor != null) {
            return ((IDocumentDefinition) ancestor).getFieldType(fieldName);
        }
        return fieldType;
    }

    @Override
    public final void setDocumentFields(List<IDocumentFieldType> documentFields) {
        setDocumentFieldsImpl(documentFields);
    }

    @Override
    public final Iterator<IDocumentFieldType> getDocumentFields() {
        Set<IDocumentFieldType> fields = new HashSet<IDocumentFieldType>();
        Iterator<IDocumentFieldType> thisFields = getDocumentFieldsImpl();
        if (thisFields != null) {
            fields.addAll(new IteratorCollection<IDocumentFieldType>(thisFields));
        }
        IEntity ancestor = getAncestor();
        if (ancestor != null) {
            IDocumentDefinition ancestorDefinition =
                    (IDocumentDefinition) ancestor;
            fields.addAll(new IteratorCollection<IDocumentFieldType>(
                    ancestorDefinition.getDocumentFields()));
        }
        return fields.iterator();
    }

    @Override
    public final Iterator<IDocumentFieldType> getRequiredFields() {
        Set<IDocumentFieldType> fields = new HashSet<IDocumentFieldType>();
        Iterator<IDocumentFieldType> thisFields = getRequiredFieldsImpl();
        if (thisFields != null) {
            fields.addAll(new IteratorCollection<IDocumentFieldType>(thisFields));
        }
        IEntity ancestor = getAncestor();
        if (ancestor != null) {
            IDocumentDefinition ancestorDefinition =
                    (IDocumentDefinition) ancestor;
            fields.addAll(new IteratorCollection<IDocumentFieldType>(
                    ancestorDefinition.getRequiredFields()));
        }
        return fields.iterator();
    }

    @Override
    public final Iterator<IDocumentFieldType> getAuthorizableFields() {
        Set<IDocumentFieldType> fields = new HashSet<IDocumentFieldType>();
        Iterator<IDocumentFieldType> thisFields = getAuthorizableFieldsImpl();
        if (thisFields != null) {
            fields.addAll(new IteratorCollection<IDocumentFieldType>(thisFields));
        }
        IEntity ancestor = getAncestor();
        if (ancestor != null) {
            IDocumentDefinition ancestorDefinition =
                    (IDocumentDefinition) ancestor;
            fields.addAll(new IteratorCollection<IDocumentFieldType>(
                    ancestorDefinition.getAuthorizableFields()));
        }
        return fields.iterator();
    }

    protected abstract IDocumentFieldType addFieldImpl(String fieldName,
                                                       DocumentFieldDataType dataType) throws GeneralDOAException;

    protected abstract IDocumentFieldType addFieldImpl(String fieldName,
                                                       DocumentFieldDataType dataType, boolean required,
                                                       boolean authorizable) throws GeneralDOAException;

    protected abstract void modifyFieldImpl(String fieldName, String newName,
                                            DocumentFieldDataType newType, boolean required,
                                            boolean authorizable) throws GeneralDOAException;

    protected abstract void removeFieldImpl(String fieldName);

    protected abstract Iterator<String> getFieldNamesImpl();

    protected abstract IDocumentFieldType getFieldTypeImpl(String fieldName);

    protected abstract void setDocumentFieldsImpl(
            List<IDocumentFieldType> documentFields);

    protected abstract Iterator<IDocumentFieldType> getDocumentFieldsImpl();

    protected abstract Iterator<IDocumentFieldType> getRequiredFieldsImpl();

    protected abstract Iterator<IDocumentFieldType> getAuthorizableFieldsImpl();

}

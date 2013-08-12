package pl.doa.service.impl;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.entity.DetachedEntity;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityProxy;

public class DetachedDocumentDefinition extends DetachedEntity implements
        IDocumentDefinition, IEntityProxy<IDocumentDefinition>, Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory
            .getLogger(DetachedDocumentDefinition.class);

    public DetachedDocumentDefinition(IDOA doa, IDocumentDefinition entity) {
        super(doa, entity);
    }

    @Override
    public IDocumentFieldType addField(String fieldName,
                                       DocumentFieldDataType dataType) throws GeneralDOAException {
        IDocumentDefinition storedEntity =
                (IDocumentDefinition) getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.addField(fieldName, dataType);
        }
        return null;
    }

    @Override
    public IDocumentFieldType addField(String fieldName,
                                       DocumentFieldDataType dataType, boolean required,
                                       boolean authorizable) throws GeneralDOAException {
        IDocumentDefinition storedEntity =
                (IDocumentDefinition) getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.addField(fieldName, dataType, required,
                    authorizable);
        }
        return null;
    }

    @Override
    public void modifyField(String fieldName, String newName,
                            DocumentFieldDataType newType, boolean required,
                            boolean authorizable) throws GeneralDOAException {
        IDocumentDefinition storedEntity =
                (IDocumentDefinition) getStoredEntity();
        if (storedEntity != null) {
            storedEntity.modifyField(fieldName, newName, newType, required,
                    authorizable);
        }
    }

    @Override
    public void removeField(String fieldName) {
        IDocumentDefinition storedEntity =
                (IDocumentDefinition) getStoredEntity();
        if (storedEntity != null) {
            storedEntity.removeField(fieldName);
        }
    }

    @Override
    public IDocument createDocumentInstance(String name)
            throws GeneralDOAException {
        IDocumentDefinition storedEntity =
                (IDocumentDefinition) getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.createDocumentInstance(name);
        }
        return null;
    }

    @Override
    public IDocument createDocumentInstance() throws GeneralDOAException {
        IDocumentDefinition storedEntity =
                (IDocumentDefinition) getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.createDocumentInstance();
        }
        return null;
    }

    @Override
    public IDocument createDocumentInstance(String name,
                                            IEntitiesContainer container) throws GeneralDOAException {
        IDocumentDefinition storedEntity =
                (IDocumentDefinition) getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.createDocumentInstance(name, container);
        }
        return null;
    }

    @Override
    public Iterator<String> getFieldNames() {
        IDocumentDefinition storedEntity =
                (IDocumentDefinition) getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.getFieldNames();
        }
        return null;
    }

    @Override
    public IDocumentFieldType getFieldType(String fieldName) {
        IDocumentDefinition storedEntity =
                (IDocumentDefinition) getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.getFieldType(fieldName);
        }
        return null;
    }

    @Override
    public void setDocumentFields(List<IDocumentFieldType> documentFields) {
        IDocumentDefinition storedEntity =
                (IDocumentDefinition) getStoredEntity();
        if (storedEntity != null) {
            storedEntity.setDocumentFields(documentFields);
        }
    }

    @Override
    public Iterator<IDocumentFieldType> getDocumentFields() {
        IDocumentDefinition storedEntity =
                (IDocumentDefinition) getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.getDocumentFields();
        }
        return null;
    }

    @Override
    public Iterator<IDocumentFieldType> getRequiredFields() {
        IDocumentDefinition storedEntity =
                (IDocumentDefinition) getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.getRequiredFields();
        }
        return null;
    }

    @Override
    public Iterator<IDocumentFieldType> getAuthorizableFields() {
        IDocumentDefinition storedEntity =
                (IDocumentDefinition) getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.getAuthorizableFields();
        }
        return null;
    }

    @Override
    protected IEntity buildAttached(IEntitiesContainer container)
            throws GeneralDOAException {
        return null;
    }

    @Override
    public IDocumentDefinition get() {
        IDocumentDefinition stored = (IDocumentDefinition) getStoredEntity();
        if (stored == null) {
            try {
                return (IDocumentDefinition) store("/tmp");
            } catch (GeneralDOAException e) {
                log.error("", e);
            }
        }
        return stored;
    }

}

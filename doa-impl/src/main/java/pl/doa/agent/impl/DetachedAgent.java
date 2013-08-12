package pl.doa.agent.impl;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.agent.IAgent;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.impl.DetachedDocument;
import pl.doa.entity.DetachedEntity;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityProxy;

public class DetachedAgent extends DetachedEntity implements IAgent,
        IEntityProxy<IAgent>, Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory
            .getLogger(DetachedDocument.class);

    public DetachedAgent(IDOA doa, IAgent entity) {
        super(doa, entity);

    }

    @Override
    public IAgent get() {
        IAgent stored = (IAgent) getStoredEntity();
        if (stored == null) {
            try {
                return (IAgent) store("/tmp");
            } catch (GeneralDOAException e) {
                log.error("", e);
            }
        }
        return stored;
    }

    @Override
    public IEntitiesContainer getPredefinedDocuments()
            throws GeneralDOAException {
        IAgent storedEntity = (IAgent) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.getPredefinedDocuments();
        } else {
            return null;
        }
    }

    @Override
    public IEntitiesContainer getFingerprintsContainer() {
        IAgent storedEntity = (IAgent) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.getFingerprintsContainer();
        } else {
            return null;
        }
    }

    @Override
    public IDocument getPredefinedDocument(String documentName)
            throws GeneralDOAException {
        IAgent storedEntity = (IAgent) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.getPredefinedDocument(documentName);
        } else {
            return null;
        }
    }

    @Override
    public Object getPredefineFieldValue(String documentName, String fieldName)
            throws GeneralDOAException {
        IAgent storedEntity = (IAgent) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.getPredefineFieldValue(documentName, fieldName);
        } else {
            return null;
        }
    }

    @Override
    public boolean isAnonymous() {
        IAgent storedEntity = (IAgent) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.isAnonymous();
        } else {
            return false;
        }
    }

    @Override
    protected IEntity buildAttached(IEntitiesContainer container)
            throws GeneralDOAException {
        return null;
    }

    @Override
    public IEntitiesContainer getFingerprintsContainer(boolean createIfNull)
            throws GeneralDOAException {
        IAgent storedEntity = (IAgent) getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.getFingerprintsContainer(createIfNull);
        } else {
            return null;
        }
    }

}

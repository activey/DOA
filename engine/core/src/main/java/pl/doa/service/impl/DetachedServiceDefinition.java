package pl.doa.service.impl;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.agent.IAgent;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.impl.DetachedDocument;
import pl.doa.entity.DetachedEntity;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityProxy;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;

public class DetachedServiceDefinition extends DetachedEntity implements
        IServiceDefinition, IEntityProxy<IServiceDefinition>, Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory
            .getLogger(DetachedDocument.class);

    public DetachedServiceDefinition(IDOA doa, IServiceDefinition entity) {
        super(doa, entity);
    }

    @Override
    public IRunningService executeService(IDocument serviceInput, IAgent runAs,
                                          boolean asynchronous) throws GeneralDOAException {
        IServiceDefinition storedEntity = (IServiceDefinition) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.executeService(serviceInput, runAs,
                    asynchronous);
        } else {
            return null;
        }
    }

    @Override
    public IRunningService executeService(IDocument serviceInput,
                                          boolean asynchronous) throws GeneralDOAException {
        IServiceDefinition storedEntity = (IServiceDefinition) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.executeService(serviceInput, asynchronous);
        } else {
            return null;
        }
    }

    @Override
    public IDocumentDefinition getInputDefinition() {
        IServiceDefinition storedEntity = (IServiceDefinition) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.getInputDefinition();
        } else {
            return null;
        }
    }

    @Override
    public List<IDocumentDefinition> getPossibleOutputs() {
        IServiceDefinition storedEntity = (IServiceDefinition) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.getPossibleOutputs();
        } else {
            return null;
        }
    }

    @Override
    public IDocumentDefinition getPossibleOutputDefinition(
            String possibleOutputName) {
        IServiceDefinition storedEntity = (IServiceDefinition) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.getPossibleOutputDefinition(possibleOutputName);
        } else {
            return null;
        }
    }

    @Override
    public void addPossibleOutputDefinition(
            IDocumentDefinition possibleOutputDefinition) {
        IServiceDefinition storedEntity = (IServiceDefinition) getStoredEntity();

        if (storedEntity != null) {
            storedEntity.addPossibleOutputDefinition(possibleOutputDefinition);
        }
    }

    @Override
    public void removePossibleOutputDefinition(
            IDocumentDefinition possibleOutputDefinition) {
        IServiceDefinition storedEntity = (IServiceDefinition) getStoredEntity();

        if (storedEntity != null) {
            storedEntity
                    .removePossibleOutputDefinition(possibleOutputDefinition);
        }

    }

    @Override
    public void setInputDefinition(IDocumentDefinition inputDefinition) {
        IServiceDefinition storedEntity = (IServiceDefinition) getStoredEntity();

        if (storedEntity != null) {
            storedEntity.setInputDefinition(inputDefinition);
        }
    }

    @Override
    public String getLogicClass() {
        IServiceDefinition storedEntity = (IServiceDefinition) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.getLogicClass();
        } else {
            return null;
        }
    }

    @Override
    public void setLogicClass(String logicClass) {
        IServiceDefinition storedEntity = (IServiceDefinition) getStoredEntity();

        if (storedEntity != null) {
            storedEntity.setLogicClass(logicClass);
        }
    }

    @Override
    public List<IRunningService> getRunningServices() {
        IServiceDefinition storedEntity = (IServiceDefinition) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.getRunningServices();
        } else {
            return null;
        }
    }

    @Override
    public void addRunning(IRunningService runningService) {
        IServiceDefinition storedEntity = (IServiceDefinition) getStoredEntity();

        if (storedEntity != null) {
            storedEntity.addRunning(runningService);
        }

    }

    @Override
    protected IEntity buildAttached(IEntitiesContainer container)
            throws GeneralDOAException {
        return null;
    }

    @Override
    public IServiceDefinition get() {
        IServiceDefinition stored = (IServiceDefinition) getStoredEntity();
        if (stored == null) {
            try {
                return (IServiceDefinition) store("/tmp");
            } catch (GeneralDOAException e) {
                log.error("", e);
            }
        }
        return stored;
    }

}

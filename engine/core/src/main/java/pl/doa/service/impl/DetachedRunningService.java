/**
 *
 */
package pl.doa.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.agent.IAgent;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.entity.DetachedEntity;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityProxy;
import pl.doa.entity.event.IEntityEventDescription;
import pl.doa.entity.event.IEntityEventListener;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;

/**
 * @author activey
 */
public class DetachedRunningService extends DetachedEntity implements
        IRunningService, IEntityProxy<IRunningService> {

    private final static Logger log = LoggerFactory
            .getLogger(DetachedRunningService.class);

    private IDocument input;
    private IDocument output;
    private IAgent agent;
    private boolean asynchronous;
    private String definitionLocation;
    private byte[] stateData;

    public DetachedRunningService(IServiceDefinition definition, IDOA doa) {
        super(doa);
        this.definitionLocation = definition.getLocation();
    }

    @Override
    public IServiceDefinition getServiceDefinition() {
        return (IServiceDefinition) doa
                .lookupEntityByLocation(definitionLocation);
    }

    @Override
    public IAgent getAgent() {
        if (this.agent != null) {
            return this.agent;
        }
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            IRunningService service = (IRunningService) storedEntity;
            IAgent storedAgent = service.getAgent();
            if (storedAgent != null) {
                return storedAgent;
            }
        }
        return doa.getAgent();
    }

    @Override
    public IDocument getInput() {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            IRunningService service = (IRunningService) storedEntity;
            return service.getInput();
        }
        return this.input;
    }

    @Override
    public void setInput(IDocument input) {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            IRunningService service = (IRunningService) storedEntity;
            service.setInput(input);
            return;
        }
        this.input = input;
    }

    @Override
    public IDocument getOutput() {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity == null) {
            return this.output;
        }
        IRunningService service = (IRunningService) storedEntity;
        return service.getOutput();
    }

    @Override
    public void setServiceDefinition(IServiceDefinition serviceDefinition) {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            IRunningService service = (IRunningService) storedEntity;
            service.setServiceDefinition(serviceDefinition);
        }
    }

    @Override
    public void setAgent(IAgent agent) {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            IRunningService service = (IRunningService) storedEntity;
            service.setAgent(agent);
            return;
        }
        this.agent = agent;
    }

    @Override
    public void setOutput(IDocument output) {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            IRunningService service = (IRunningService) storedEntity;
            service.setOutput(output);
            return;
        }
        this.output = output;
    }

    @Override
    public void setAsynchronous(boolean asynchronous) {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            IRunningService service = (IRunningService) storedEntity;
            service.setAsynchronous(asynchronous);
            return;
        }
        this.asynchronous = asynchronous;
    }

    @Override
    public boolean isAsynchronous() {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            IRunningService service = (IRunningService) storedEntity;
            return service.isAsynchronous();
        }
        return asynchronous;
    }

    @Override
    protected IEntity buildAttached(IEntitiesContainer container)
            throws GeneralDOAException {
        IRunningService attached =
                doa.createRunningService(getServiceDefinition(), container);
        attached.setAgent(agent);
        attached.setAsynchronous(asynchronous);
        attached.setInput(input);
        attached.setOutput(output);
        attached.serializeState(stateData);
        return attached;
    }

    @Override
    public void handleEvent(IEntityEventDescription eventDescription)
            throws Exception {
        // do nothing
    }

    @Override
    public void serializeState(byte[] stateData) {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity == null) {
            this.stateData = stateData;
            return;
        }
        IRunningService service = (IRunningService) storedEntity;
        service.serializeState(stateData);
    }

    @Override
    public byte[] deserializeState() {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity == null) {
            return this.stateData;
        }
        IRunningService service = (IRunningService) storedEntity;
        return service.deserializeState();
    }

    @Override
    public IRunningService get() {
        IRunningService stored = (IRunningService) getStoredEntity();
        if (stored == null) {
            try {
                return (IRunningService) store("/tmp");
            } catch (GeneralDOAException e) {
                log.error("", e);
            }
        }
        return stored;
    }

    @Override
    public IEntityEventListener getAwaitedEventListener() {
        IRunningService storedEntity = (IRunningService) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.getAwaitedEventListener();
        } else {
            return null;
        }
    }

}

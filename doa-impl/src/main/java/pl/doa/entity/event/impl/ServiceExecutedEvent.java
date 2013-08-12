/**
 *
 */
package pl.doa.entity.event.impl;

import pl.doa.agent.IAgent;
import pl.doa.document.IDocument;
import pl.doa.entity.event.DetachedEvent;
import pl.doa.entity.event.EntityEventType;
import pl.doa.entity.event.EventProperty;
import pl.doa.entity.event.IEntityEventDescription;
import pl.doa.service.IRunningService;

/**
 * @author activey
 */
public class ServiceExecutedEvent extends DetachedEvent {


    private static final long serialVersionUID = 6154535824409052800L;

    @EventProperty
    private IDocument output;

    @EventProperty
    private IAgent agent;

    public ServiceExecutedEvent(IRunningService waitingForService) {
        super(waitingForService);
        this.output = waitingForService.getOutput();
        this.agent = waitingForService.getAgent();
    }

    public ServiceExecutedEvent(IEntityEventDescription eventDescription) {
        super(eventDescription.getSourceEntity());
        this.output =
                (IDocument) eventDescription.getReferenceProperty("output");
    }

    public IDocument getServiceOutput() {
        return this.output;
    }

    public IAgent getAgent() {
        return this.agent;
    }

    @Override
    public EntityEventType getEventType() {
        return EntityEventType.SERVICE_EXECUTED;
    }

}

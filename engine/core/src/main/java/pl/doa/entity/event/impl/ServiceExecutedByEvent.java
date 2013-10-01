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
import pl.doa.service.IServiceDefinition;

/**
 * @author activey
 */
public class ServiceExecutedByEvent extends DetachedEvent {

    private static final long serialVersionUID = -2070565955079254083L;

    @EventProperty
    private IAgent agent;

    @EventProperty
    private IDocument output;

    public ServiceExecutedByEvent(IServiceDefinition service, IDocument output,
                                  IAgent agent) {
        super(service);
        this.output = output;
        this.agent = agent;
    }

    public ServiceExecutedByEvent(IEntityEventDescription eventDescription) {
        super(eventDescription.getSourceEntity());
        this.output =
                (IDocument) eventDescription.getReferenceProperty("output");
        this.agent = (IAgent) eventDescription.getReferenceProperty("agent");

    }

    public ServiceExecutedByEvent(IRunningService runningService) {
        this(runningService.getServiceDefinition(), runningService.getOutput(),
                runningService.getAgent());
    }

    public IAgent getAgent() {
        return this.agent;
    }

    public IDocument getServiceOutput() {
        return this.output;
    }

    @Override
    public EntityEventType getEventType() {
        return EntityEventType.SERVICE_EXECUTED_BY;
    }

}

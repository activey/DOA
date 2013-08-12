/**
 *
 */
package pl.doa.entity.event.impl;

import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.entity.event.DetachedEvent;
import pl.doa.entity.event.EntityEventType;
import pl.doa.entity.event.EventProperty;

/**
 * @author activey
 */
public class EntityCreatedEvent extends DetachedEvent {

    private static final long serialVersionUID = 2241291533313022825L;

    @EventProperty
    private final IEntity createdEntity;

    public EntityCreatedEvent(IEntity createdEntity,
                              IEntitiesContainer container) {
        super(container);
        this.createdEntity = createdEntity;
    }

    @Override
    public EntityEventType getEventType() {
        return EntityEventType.ENTITY_CREATED;
    }

    public IEntity getCreatedEntity() {
        return createdEntity;
    }

}

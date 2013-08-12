package pl.doa.entity.event;

public class WrappedEvent extends DetachedEvent {

    private IEntityEventDescription wrapped;

    public WrappedEvent(IEntityEventDescription wrapped) {
        super(wrapped);
        this.wrapped = wrapped;
    }

    @Override
    public EntityEventType getEventType() {
        return wrapped.getEventType();
    }
}

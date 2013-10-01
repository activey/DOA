/**
 *
 */
package pl.doa.entity.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.IDOA;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityProxy;
import pl.doa.entity.impl.AbstractEntity;

/**
 * @author activey
 */
public abstract class AbstractEntityEvent extends AbstractEntity implements
        IEntityEvent {

    private final Logger log = LoggerFactory
            .getLogger(AbstractEntityEvent.class);

    public AbstractEntityEvent(IDOA doa) {
        super(doa);
    }

    protected abstract void setSourceEntityImpl(IEntity sourceEntity);

    @Override
    public final void setSourceEntity(IEntity sourceEntity) {
        setSourceEntityImpl(sourceEntity);
    }

    protected abstract IEntity getSourceEntityImpl();

    @Override
    public final IEntity getSourceEntity() {
        return getSourceEntityImpl();
    }

    protected abstract EntityEventType getEventTypeImpl();

    @Override
    public final EntityEventType getEventType() {
        return getEventTypeImpl();
    }

    protected abstract Iterable<String> getEventPropertyNamesImpl();

    @Override
    public final Iterable<String> getEventPropertyNames() {
        return getEventPropertyNamesImpl();
    }

    protected abstract Object getEventPropertyImpl(String propertyName);

    @Override
    public final Object getEventProperty(String propertyName) {
        return getEventPropertyImpl(propertyName);
    }

    protected abstract void setEventPropertyImpl(String propertyName,
                                                 Object propertyValue);

    @Override
    public final void setEventProperty(String propertyName, Object propertyValue) {
        setEventPropertyImpl(propertyName, propertyValue);
    }

    protected abstract String getStringPropertyImpl(String propertyName);

    @Override
    public final String getStringProperty(String propertyName) {
        return getStringPropertyImpl(propertyName);
    }

    protected abstract void setStringPropertyImpl(String propertyName,
                                                  String propertyValue);

    @Override
    public final void setStringProperty(String propertyName,
                                        String propertyValue) {
        setStringPropertyImpl(propertyName, propertyValue);
    }

    protected abstract Integer getIntPropertyImpl(String propertyName);

    @Override
    public final Integer getIntProperty(String propertyName) {
        return getIntPropertyImpl(propertyName);
    }

    protected abstract void setIntPropertyImpl(String propertyName,
                                               int propertyValue);

    @Override
    public final void setIntProperty(String propertyName, int propertyValue) {
        setIntPropertyImpl(propertyName, propertyValue);
    }

    protected abstract IEntity getReferencePropertyImpl(String propertyName);

    @Override
    public final IEntity getReferenceProperty(String propertyName) {
        return getReferencePropertyImpl(propertyName);
    }

    protected abstract void setReferencePropertyImpl(String propertyName,
                                                     IEntity propertyValue);

    @Override
    public final void setReferenceProperty(String propertyName,
                                           IEntity propertyValue) {
        if (propertyValue != null && propertyValue instanceof IEntityProxy) {
            IEntityProxy<IEntity> proxy = (IEntityProxy<IEntity>) propertyValue;
            setReferencePropertyImpl(propertyName, proxy.get());
            return;
        }
        setReferencePropertyImpl(propertyName, propertyValue);
    }

}

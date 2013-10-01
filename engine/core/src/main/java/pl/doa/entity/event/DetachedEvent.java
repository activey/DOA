/**
 *
 */
package pl.doa.entity.event;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import pl.doa.IDOA;
import pl.doa.entity.IEntity;

/**
 * @author activey
 */
public abstract class DetachedEvent implements IEntityEventDescription,
        Serializable {

    private static final long serialVersionUID = 1695136274464910147L;

    protected IEntity eventSource;

    private IEntityEventDescription wrapped;

    public DetachedEvent(IEntity eventSource) {
        this.eventSource = eventSource;
    }

    public DetachedEvent(IEntityEventDescription wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public final IEntity getSourceEntity() {
        return (wrapped != null) ? wrapped.getSourceEntity() : eventSource;
    }

    public final void setSourceEntity(IEntity entity) {
        throw new RuntimeException("Operation not allowed!");
    }

    @Override
    public final Iterable<String> getEventPropertyNames() {
        if (wrapped != null) {
            return wrapped.getEventPropertyNames();
        }

        List<String> props = new ArrayList<String>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(EventProperty.class) == null) {
                continue;
            }
            Class<?> fieldClass = field.getType();
            if (!fieldClass.isPrimitive()) {
                if (!IEntity.class.isAssignableFrom(fieldClass)) {
                    continue;
                }
            }
            props.add(field.getName());
        }
        return props;
    }

    @Override
    public final Object getEventProperty(String propertyName) {
        if (wrapped != null) {
            return wrapped.getEventProperty(propertyName);
        }
        try {
            Field field = this.getClass().getDeclaredField(propertyName);
            if (!field.isAnnotationPresent(EventProperty.class)) {
                return null;
            }
            Class<?> fieldClass = field.getType();
            if (fieldClass.isPrimitive()
                    || IEntity.class.isAssignableFrom(fieldClass)) {
                field.setAccessible(true);
                return field.get(this);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public final void setEventProperty(String propertyName, Object propertyValue) {
        try {
            Field field = this.getClass().getDeclaredField(propertyName);
            if (field.getAnnotation(EventProperty.class) == null) {
                return;
            }
            Class<?> fieldClass = field.getType();
            if (fieldClass.isAssignableFrom(propertyValue.getClass())) {
                field.set(this, propertyValue);
            }
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public final String getStringProperty(String propertyName) {
        return (String) getEventProperty(propertyName);
    }

    @Override
    public final void setStringProperty(String propertyName,
                                        String propertyValue) {
        setEventProperty(propertyName, propertyValue);
    }

    @Override
    public final Integer getIntProperty(String propertyName) {
        return (Integer) getEventProperty(propertyName);
    }

    @Override
    public final void setIntProperty(String propertyName, int propertyValue) {
        setEventProperty(propertyName, propertyValue);
    }

    @Override
    public final IEntity getReferenceProperty(String propertyName) {
        return (IEntity) getEventProperty(propertyName);
    }

    @Override
    public final void setReferenceProperty(String propertyName,
                                           IEntity propertyValue) {
        setEventProperty(propertyName, propertyValue);
    }

    public final IEntityEvent buildEvent(IDOA doa) {
        IEntityEvent event =
                doa.createEntityEvent(getSourceEntity(), getEventType());
        Iterable<String> props = getEventPropertyNames();
        for (String propertyName : props) {
            Object property = getEventProperty(propertyName);
            if (property == null) {
                continue;
            }
            if (property instanceof IEntity) {
                event.setReferenceProperty(propertyName, (IEntity) property);
            } else {
                event.setEventProperty(propertyName, property);
            }
        }
        return event;
    }

}

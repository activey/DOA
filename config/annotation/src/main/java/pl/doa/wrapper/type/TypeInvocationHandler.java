package pl.doa.wrapper.type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.entity.IEntity;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: activey
 * Date: 29.07.13
 * Time: 16:03
 * To change this template use File | Settings | File Templates.
 */
public abstract class TypeInvocationHandler<T extends IEntity> implements InvocationHandler {

    private final static Logger log = LoggerFactory.getLogger(TypeInvocationHandler.class);

    protected final T entity;

    public TypeInvocationHandler(T entity) {
        this.entity = entity;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        Annotation[] annotations = method.getDeclaredAnnotations();
        if (annotations == null || annotations.length == 0) {
            return method.invoke(entity, objects);
        }
        return invoke(method, objects);
    }

    protected abstract Object invoke(Method method, Object[] params) throws Throwable;
}

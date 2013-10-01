package pl.doa.wrapper.type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.wrapper.annotation.Field;
import pl.doa.wrapper.utils.ReflectionUtils;
import pl.doa.document.IDocument;

import java.lang.reflect.Method;

/**
 * User: activey
 * Date: 29.07.13
 * Time: 17:21
 */
public class DocumentInvocationHandler extends TypeInvocationHandler<IDocument> {

    private final static Logger log = LoggerFactory.getLogger(DocumentInvocationHandler.class);

    public DocumentInvocationHandler(IDocument entity) {
        super(entity);
    }

    @Override
    protected Object invoke(Method method, Object[] params) throws Throwable {
        Field field = method.getAnnotation(Field.class);
        if (field == null) {
            return null;
        }
        String methodName = method.getName();
        String fieldName = field.name();
        if (fieldName == null || fieldName.trim().length() == 0) {
            fieldName = ReflectionUtils.getPropertyName(method);
        }
        // checking if its setter or getter
        if (ReflectionUtils.isGetter(method)) {
            // getting field value
            Object fieldValue = entity.getFieldValue(fieldName);
            if (fieldValue == null) {
                return null;
            }
            Class<?> returnType = method.getReturnType();
            if (fieldValue.getClass().isAssignableFrom(returnType)) {
                return fieldValue;
            } else {
                log.error(String.format("Data type mismatch for field [%s]", fieldName));
            }
        } else if (ReflectionUtils.isSetter(method)) {
            // setting field value
            Class<?>[] methodParams = method.getParameterTypes();
            if (methodParams == null || methodParams.length == 0) {
                log.warn(String.format("Setter method [%s] has no params!", method.getName()));
            } else {
                Object fieldValue = params[0];
                entity.setFieldValue(fieldName, fieldValue);
            }
        }
        return null;
    }

}

package pl.doa.wrapper.utils;

import pl.doa.wrapper.annotation.Field;

import java.beans.Introspector;
import java.lang.reflect.*;

/**
 * Created with IntelliJ IDEA. User: activey Date: 29.07.13 Time: 17:39 To change this template use File | Settings |
 * File Templates.
 */
public class ReflectionUtils {

    public static boolean isGetter(Method method) {
        if (Modifier.isPublic(method.getModifiers()) &&
                method.getParameterTypes().length == 0) {
            if (method.getName().matches("^get[A-Z].*") &&
                    !method.getReturnType().equals(void.class))
                return true;
            if (method.getName().matches("^is[A-Z].*") &&
                    method.getReturnType().equals(boolean.class))
                return true;
        }
        return false;
    }

    public static boolean isSetter(Method method) {
        return Modifier.isPublic(method.getModifiers()) &&
                method.getReturnType().equals(void.class) &&
                method.getParameterTypes().length == 1 &&
                method.getName().matches("^set[A-Z].*");
    }

    public static String getPropertyName(Member member) {
        String name = null;
        if (member instanceof Field) {
            name = member.getName();
        } else if (member instanceof Method) {
            String methodName = member.getName();
            if (methodName.matches("^is[A-Z].*")) {
                name = Introspector.decapitalize(methodName.substring(2));
            } else if (methodName.matches("^get[A-Z].*")) {
                name = Introspector.decapitalize(methodName.substring(3));
            } else if (methodName.matches("^set[A-Z].*")) {
                name = Introspector.decapitalize(methodName.substring(3));
            }
        }
        return name;
    }

    public static <T extends Object> Class<T> getClassType(Class<?> typeClass, Class<? extends T> superclassFilter, int typeIndex) {
        Type classType = typeClass.getGenericSuperclass();
        ParameterizedType paramType = (ParameterizedType) classType;
        Type[] types = paramType.getActualTypeArguments();
        if (types == null || types.length == 0) {
            return null;
        }
        Type type = types[typeIndex];
        if (type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            if (superclassFilter.isAssignableFrom(clazz)) {
                return (Class<T>) clazz;
            }
        }
        return null;
    }

    public static Class<?> getClassType(Class<?> typeClass, int typeIndex) {
        return ReflectionUtils.getClassType(typeClass, Object.class, typeIndex);
    }
}

package pl.doa.wrapper.test;

import junit.framework.TestCase;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created with IntelliJ IDEA. User: activey Date: 31.07.13 Time: 14:49 To change this template use File | Settings |
 * File Templates.
 */
public class Test extends TestCase {

    public void test1() {
        Class<TestService> typeClass = TestService.class;
        Type type = typeClass.getGenericSuperclass();
        ParameterizedType paramType = (ParameterizedType) type;
        Type[] types = paramType.getActualTypeArguments();


        System.out.println(">>> " + types[0]);
    }
}

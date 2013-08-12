package pl.doa.wrapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Attributes {

    public @interface Attribute {
        public String name() default "";

        public String value() default "";
    }

    Attribute[] value();

}

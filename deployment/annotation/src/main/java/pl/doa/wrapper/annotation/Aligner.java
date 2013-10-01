package pl.doa.wrapper.annotation;

import pl.doa.document.IDocument;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User: activey Date: 08.08.13 Time: 20:56
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Aligner {

    public String name() default "";
}

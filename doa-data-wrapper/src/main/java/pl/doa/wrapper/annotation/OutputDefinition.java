package pl.doa.wrapper.annotation;

import pl.doa.document.IDocument;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA.
 * User: activey
 * Date: 31.07.13
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OutputDefinition {

    public Class<? extends IDocument> definition();
}

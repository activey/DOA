package pl.doa.wrapper.wicket;

import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.entity.IEntity;
import pl.doa.wicket.WicketDOAApplication;
import pl.doa.wicket.decorator.IEntityDecorator;
import pl.doa.wrapper.type.TypeWrapper;

/**
 * Created with IntelliJ IDEA.
 * User: activey
 * Date: 06.08.13
 * Time: 21:02
 * To change this template use File | Settings | File Templates.
 */
public abstract class WrappedWicketDOAApplication extends WicketDOAApplication {

    public <T extends IEntityDecorator<? extends IEntity>> void registerDecorator(
            Class<? extends IDocument> wrappdType, final Class<T> decoratorClass) {
        IDocumentDefinition type = TypeWrapper.unwrapDocumentDefinition(wrappdType, getApplicationContainer());
        decoratorLocator.registerDecorator(type.getId(), decoratorClass);
    }
}

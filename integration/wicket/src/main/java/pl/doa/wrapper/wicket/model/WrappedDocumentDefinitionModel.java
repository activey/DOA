package pl.doa.wrapper.wicket.model;

import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.wicket.WicketDOAApplication;
import pl.doa.wicket.model.EntityModel;
import pl.doa.wrapper.type.TypeWrapper;

/**
 * User: activey
 * Date: 08.08.13
 * Time: 14:12
 */
public class WrappedDocumentDefinitionModel extends EntityModel<IDocumentDefinition> {

    public WrappedDocumentDefinitionModel(Class<? extends IDocument> wrappedType) {
        super(TypeWrapper.unwrapDocumentDefinition(wrappedType, WicketDOAApplication.get().getApplicationContainer()));
    }
}

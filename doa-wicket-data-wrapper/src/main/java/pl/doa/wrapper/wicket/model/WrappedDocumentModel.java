package pl.doa.wrapper.wicket.model;

import pl.doa.document.IDocument;
import pl.doa.wicket.WicketDOAApplication;
import pl.doa.wicket.model.EntityModel;
import pl.doa.wicket.model.document.DocumentModel;
import pl.doa.wicket.model.service.ServiceDefinitionModel;
import pl.doa.wrapper.service.AbstractWrappedServiceDefinitionLogic;
import pl.doa.wrapper.type.TypeWrapper;

/**
 * User: activey
 * Date: 08.08.13
 * Time: 14:12
 */
public class WrappedDocumentModel<T extends IDocument> extends EntityModel<T> {

    public WrappedDocumentModel(Class<T> wrappedType) {
        super(TypeWrapper.wrap(wrappedType, WicketDOAApplication.get().getApplicationContainer()));
    }
}

package pl.doa.wrapper.wicket.model;

import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.service.IServiceDefinition;
import pl.doa.wicket.WicketDOAApplication;
import pl.doa.wicket.model.EntityModel;
import pl.doa.wicket.model.service.ServiceDefinitionModel;
import pl.doa.wrapper.service.AbstractWrappedServiceDefinitionLogic;
import pl.doa.wrapper.type.TypeWrapper;

/**
 * User: activey
 * Date: 08.08.13
 * Time: 14:12
 */
public class WrappedServiceDefinitionModel<T extends Class<AbstractWrappedServiceDefinitionLogic<?>>> extends ServiceDefinitionModel {

    public WrappedServiceDefinitionModel(T wrappedType) {
        super(TypeWrapper.unwrapServiceDefinition(wrappedType, WicketDOAApplication.get().getApplicationContainer()));
    }
}

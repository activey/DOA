package pl.doa.wrapper.wicket.service;

import org.apache.wicket.model.CompoundPropertyModel;
import pl.doa.document.IDocument;
import pl.doa.wicket.form.CallServiceForm;
import pl.doa.wrapper.service.AbstractWrappedServiceDefinitionLogic;
import pl.doa.wrapper.wicket.model.WrappedDocumentModel;
import pl.doa.wrapper.wicket.model.WrappedServiceDefinitionModel;

/**
 * User: activey
 * Date: 31.07.13
 * Time: 15:36
 */
public class WrappedCallServiceForm<S extends IDocument> extends CallServiceForm<S> {

    public WrappedCallServiceForm(String id, Class<? extends AbstractWrappedServiceDefinitionLogic<?>> serviceClass, Class<S> inputType) {
        super(id, new WrappedServiceDefinitionModel(serviceClass), new CompoundPropertyModel<S>(new WrappedDocumentModel<S>(inputType)));
    }


}

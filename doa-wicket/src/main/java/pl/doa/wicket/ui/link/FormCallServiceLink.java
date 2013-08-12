/**
 *
 */
package pl.doa.wicket.ui.link;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.IAjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes.Method;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.wicket.form.CallServiceForm;

/**
 * @author activey
 */
public class FormCallServiceLink extends AjaxSubmitLink {

    private final static Logger log = LoggerFactory
            .getLogger(FormCallServiceLink.class);

    public FormCallServiceLink(String id, CallServiceForm serviceForm) {
        super(id, serviceForm);
    }

    public FormCallServiceLink(String id) {
        super(id);
    }

    @Override
    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
        CallServiceForm serviceForm = (CallServiceForm) form;
        try {
            onBefeforeRun(serviceForm.getServiceModel(),
                    serviceForm.getInputModel(), target);
        } catch (GeneralDOAException e) {
            log.error("", e);
        }
    }

    @Override
    protected final void onAfterSubmit(AjaxRequestTarget target, Form<?> form) {
        CallServiceForm serviceForm = (CallServiceForm) form;
        try {
            onAfterRun(serviceForm.getRunningServiceModel(), target);
        } catch (GeneralDOAException e) {
            log.error("", e);
        }
    }

    @Override
    protected void onError(AjaxRequestTarget target, Form<?> form) {
        CallServiceForm serviceForm = (CallServiceForm) form;
    }

    protected void onBefeforeRun(IModel<IServiceDefinition> serviceModel,
                                 IModel<IDocument> inputModel, AjaxRequestTarget target)
            throws GeneralDOAException {
    }

    protected void onAfterRun(IModel<IRunningService> runningService,
                              AjaxRequestTarget target) throws GeneralDOAException {
    }

    protected void populateListeners(List<IAjaxCallListener> listeners) {

    }

    @Override
    protected final void updateAjaxAttributes(AjaxRequestAttributes attributes) {
        attributes.setMethod(Method.POST);
        populateListeners(attributes.getAjaxCallListeners());
    }
}

package com.olender.webapp.admin.pages.images;

import com.olender.webapp.components.link.ValidatingCallServiceLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.wicket.form.CallServiceForm;
import pl.doa.wicket.ui.widgets.InputReferencesListField;

public class UploadImageForm extends CallServiceForm {

    private final IModel<IEntitiesContainer> containerModel;

    public UploadImageForm(String id, IModel<IEntitiesContainer> containerModel) {
        super(id, "/services/application/image_upload");
        this.containerModel = containerModel;
    }

    @Override
    protected void initForm() throws Exception {
        ValidatingCallServiceLink link = new ValidatingCallServiceLink(
                "link_image_upload") {

            @Override
            protected void onAfterRun(IRunningService runningService,
                                      IDocument input, AjaxRequestTarget target)
                    throws GeneralDOAException {
                UploadImageForm.this.handleRunning(runningService, target);
            }

            @Override
            protected void onBefeforeRun(IServiceDefinition service,
                                         IDocument input) throws GeneralDOAException {
                input.setFieldValue("imagesContainer",
                        containerModel.getObject());
            }

        };
        add(link);

        add(new InputReferencesListField("images", getModel(), "images"));
    }

    protected void handleRunning(IRunningService running,
                                 AjaxRequestTarget target) {
    }
}

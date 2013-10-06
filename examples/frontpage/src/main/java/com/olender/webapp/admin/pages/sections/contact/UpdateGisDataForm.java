/**
 * 
 */
package com.olender.webapp.admin.pages.sections.contact;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.wicket.form.CallServiceForm;
import pl.doa.wicket.model.EntityModel;
import pl.doa.wicket.ui.widgets.DocumentField;
import pl.doa.wicket.ui.widgets.DocumentTextArea;

import com.olender.webapp.behavior.impl.wysywig.WysywigEditorBehavior;
import com.olender.webapp.components.link.ValidatingCallServiceLink;

/**
 * @author activey
 * 
 */
public class UpdateGisDataForm extends CallServiceForm {

	public UpdateGisDataForm(String id, IModel<IDocument> existingSection) {
		super(id, new EntityModel<IServiceDefinition>(
				"/services/application/section_contact_update"),
				existingSection);
		setMultiPart(false);
	}

	protected void initForm() throws Exception {
		add(new DocumentField("apiKey", getModel(), "apiKey"));
		add(new DocumentField("longitude", getModel(), "longitude"));
		add(new DocumentField("latitude", getModel(), "latitude"));
		add(new DocumentTextArea("description", getModel(), "description")
				.add(new WysywigEditorBehavior()));

		add(new ValidatingCallServiceLink("call_service") {

			@Override
			protected void onBefeforeRun(IServiceDefinition service,
					IDocument input) throws GeneralDOAException {
				input.setFieldValue("section",
						UpdateGisDataForm.this.getModelObject());
			}

			@Override
			protected void onAfterRun(IRunningService runningService,
					IDocument input, AjaxRequestTarget target)
					throws GeneralDOAException {
				UpdateGisDataForm.this.handleRunning(runningService, target);
			}

		});

	}

	protected void handleRunning(IRunningService running,
			AjaxRequestTarget target) {
	}

	@Override
	protected boolean isTransactional() {
		return true;
	}
}

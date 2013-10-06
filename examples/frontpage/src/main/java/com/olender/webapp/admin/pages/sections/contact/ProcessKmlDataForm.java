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
import pl.doa.wicket.ui.widgets.InputReferenceField;

import com.olender.webapp.components.link.ValidatingCallServiceLink;

/**
 * @author activey
 * 
 */
public class ProcessKmlDataForm extends CallServiceForm {

	private final IModel<IDocument> existingSection;

	public ProcessKmlDataForm(String id, IModel<IDocument> existingSection) {
		super(id, new EntityModel<IServiceDefinition>(
				"/services/application/process_kml_data"));
		this.existingSection = existingSection;
	}

	protected void initForm() throws Exception {
		add(new InputReferenceField("kmlFile", getModel(), "kmlFile"));

		add(new ValidatingCallServiceLink("process_kml") {

			@Override
			protected void onAfterRun(IRunningService runningService,
					IDocument input, AjaxRequestTarget target)
					throws GeneralDOAException {
				ProcessKmlDataForm.this.handleRunning(runningService, target);
			}

			@Override
			protected void onBefeforeRun(IServiceDefinition service,
					IDocument input) throws GeneralDOAException {
				input.setFieldValue("section", existingSection.getObject());
			}

		});

	}

	protected void handleRunning(IRunningService running,
			AjaxRequestTarget target) {
	}
}

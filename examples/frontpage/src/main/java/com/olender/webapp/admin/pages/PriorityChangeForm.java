package com.olender.webapp.admin.pages;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.wicket.form.CallServiceForm;

import com.olender.webapp.components.link.ValidatingCallServiceLink;

public class PriorityChangeForm extends CallServiceForm {

	private final IModel<IDocument> sectionModel;

	public PriorityChangeForm(String id, IModel<IDocument> sectionModel) {
		super(id, "/services/application/priority_change");
		this.sectionModel = sectionModel;
	}

	@Override
	protected void initForm() throws Exception {

		add(new ValidatingCallServiceLink("link_priority_up") {
			@Override
			protected void onBefeforeRun(IServiceDefinition service,
					IDocument input) throws GeneralDOAException {
				input.setFieldValue("section", sectionModel.getObject());
				input.setFieldValue("delta", 1L);
			}

			@Override
			protected void onAfterRun(IRunningService runningService,
					IDocument input, AjaxRequestTarget target)
					throws GeneralDOAException {
				PriorityChangeForm.this.priorityChanged(
						runningService.getOutput(), target);
			}

		});

		add(new ValidatingCallServiceLink("link_priority_down") {
			@Override
			protected void onBefeforeRun(IServiceDefinition service,
					IDocument input) throws GeneralDOAException {
				input.setFieldValue("section", sectionModel.getObject());
				input.setFieldValue("delta", -1L);
			}

			@Override
			protected void onAfterRun(IRunningService runningService,
					IDocument input, AjaxRequestTarget target)
					throws GeneralDOAException {
				PriorityChangeForm.this.priorityChanged(
						runningService.getOutput(), target);
			}

		});
	}

	protected void priorityChanged(IDocument output, AjaxRequestTarget target) {
	}
}

package com.olender.webapp.admin.pages;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.IAjaxCallListener;
import org.apache.wicket.model.IModel;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.wicket.form.CallServiceForm;
import pl.doa.wicket.model.EntityModel;
import pl.doa.wicket.ui.link.EntityLink;

import com.olender.webapp.components.ajax.ConfirmListener;
import com.olender.webapp.components.link.ValidatingCallServiceLink;

public abstract class RemoveSectionForm extends CallServiceForm {

	public RemoveSectionForm(String id, IModel<IDocument> sectionModel) {
		super(id, new EntityModel<IServiceDefinition>(
				"/services/application/section_remove"), sectionModel);
	}

	@Override
	protected void initForm() throws Exception {
		ValidatingCallServiceLink removeLink = new ValidatingCallServiceLink(
				"link_section_remove") {

			@Override
			protected void populateListeners(List<IAjaxCallListener> listeners) {
				listeners.add(new ConfirmListener());
			}

			@Override
			protected void onAfterRun(IRunningService runningService,
					IDocument input, AjaxRequestTarget target)
					throws GeneralDOAException {
				sectionRemoved(true, target);
			}

		};
		add(removeLink);

		add(sectionLink(inputModel));
	}

	protected abstract EntityLink<IDocument> sectionLink(
			IModel<IDocument> sectionModel);

	protected void sectionRemoved(boolean removed, AjaxRequestTarget target) {

	}
}

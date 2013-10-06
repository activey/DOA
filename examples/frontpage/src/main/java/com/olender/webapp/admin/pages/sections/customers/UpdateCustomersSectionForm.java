package com.olender.webapp.admin.pages.sections.customers;

import org.apache.wicket.model.IModel;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.service.IServiceDefinition;
import pl.doa.wicket.form.CallServiceForm;
import pl.doa.wicket.model.EntityModel;
import pl.doa.wicket.model.document.DocumentModel;
import pl.doa.wicket.ui.widgets.DocumentTextArea;

import com.olender.webapp.behavior.impl.wysywig.WysywigEditorBehavior;
import com.olender.webapp.components.link.ValidatingCallServiceLink;

public class UpdateCustomersSectionForm extends CallServiceForm {

	public UpdateCustomersSectionForm(String id, IModel<IDocument> section)
			throws GeneralDOAException {
		super(id, new EntityModel<IServiceDefinition>(
				"/services/application/section_customers_update"),
				new DocumentModel(section.getObject(), true));
	}

	@Override
	protected void initForm() throws Exception {
		add(new DocumentTextArea("section_description", getModel(),
				"description").add(new WysywigEditorBehavior()));

		add(new ValidatingCallServiceLink("call_service"));
	}
}

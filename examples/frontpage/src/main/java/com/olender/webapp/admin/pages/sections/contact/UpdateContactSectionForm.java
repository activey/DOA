/**
 * 
 */
package com.olender.webapp.admin.pages.sections.contact;

import org.apache.wicket.model.IModel;

import pl.doa.document.IDocument;
import pl.doa.service.IServiceDefinition;
import pl.doa.wicket.form.CallServiceForm;
import pl.doa.wicket.model.EntityModel;
import pl.doa.wicket.ui.widgets.DocumentField;

import com.olender.webapp.components.link.ValidatingCallServiceLink;

/**
 * @author activey
 * 
 */
public class UpdateContactSectionForm extends CallServiceForm {

	public UpdateContactSectionForm(String id, IModel<IDocument> existingSection) {
		super(id, new EntityModel<IServiceDefinition>(
				"/services/application/section_contact_update"),
				existingSection);
		setOutputMarkupId(true);
		setMultiPart(false);
	}

	protected void initForm() throws Exception {
		add(new DocumentField("addr1", getModel(), "addr1"));
		add(new DocumentField("addr2", getModel(), "addr2"));
		add(new DocumentField("addr3", getModel(), "addr3"));

		add(new DocumentField("phone1", getModel(), "phone1"));
		add(new DocumentField("phone2", getModel(), "phone2"));
		add(new DocumentField("email", getModel(), "email"));

		add(new ValidatingCallServiceLink("call_service"));

	}
}

package com.olender.webapp.admin.pages;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.wicket.form.CallServiceForm;
import pl.doa.wicket.model.container.ContainerEntitiesProvider;
import pl.doa.wicket.ui.widgets.DocumentField;

import com.olender.webapp.components.link.ValidatingCallServiceLink;

public class CreateSectionForm extends CallServiceForm {

	public CreateSectionForm(String id) {
		super(id, "/services/application/section_create");
	}

	@Override
	protected void initForm() throws Exception {
		DataView<IDocumentDefinition> sectionTypes = new DataView<IDocumentDefinition>(
				"section_type",
				new ContainerEntitiesProvider<IDocumentDefinition>(
						"/definitions/sections/application")) {
			@Override
			protected void populateItem(final Item<IDocumentDefinition> item) {
				ValidatingCallServiceLink link = new ValidatingCallServiceLink(
						"link_section_create") {

					@Override
					protected void onBefeforeRun(IServiceDefinition service,
							IDocument input) throws GeneralDOAException {
						input.setFieldValue("section_definition",
								item.getModelObject());
					}

					@Override
					protected void onAfterRun(IRunningService runningService,
							IDocument input, AjaxRequestTarget target)
							throws GeneralDOAException {
						target.add(getPage().get("sections_gallery_list"));
						target.add(getPage().get("sections_text_list"));
						target.add(getPage().get("sections_customers_list"));
						target.add(getPage().get("sections_contact_list"));
					}

				};
				link.setBody(new AbstractReadOnlyModel<String>() {

					@Override
					public String getObject() {
						IDocumentDefinition def = item.getModelObject();
						return def.getAttribute("frontpage.label",
								def.getName());
					}
				});
				item.add(link);
			}
		};
		add(sectionTypes);

		add(new DocumentField("section_name", getModel(), "section_name"));
	}
}

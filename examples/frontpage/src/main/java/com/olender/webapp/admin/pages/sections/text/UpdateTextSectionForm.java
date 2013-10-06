package com.olender.webapp.admin.pages.sections.text;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.resource.IStaticResource;
import pl.doa.service.IServiceDefinition;
import pl.doa.wicket.form.CallServiceForm;
import pl.doa.wicket.model.EntityModel;
import pl.doa.wicket.model.document.DocumentModel;
import pl.doa.wicket.model.document.field.DocumentFieldModel;
import pl.doa.wicket.ui.widgets.DocumentField;
import pl.doa.wicket.ui.widgets.DocumentReferenceField;
import pl.doa.wicket.ui.widgets.DocumentTextArea;
import pl.doa.wicket.ui.window.ReturnableEntityWindow;

import com.olender.webapp.behavior.impl.wysywig.WysywigEditorBehavior;
import com.olender.webapp.components.form.ImageReferenceField;
import com.olender.webapp.components.link.ValidatingCallServiceLink;

public class UpdateTextSectionForm extends CallServiceForm {

	private final static Logger log = LoggerFactory
			.getLogger(UpdateTextSectionForm.class);

	public UpdateTextSectionForm(String id, IModel<IDocument> section)
			throws GeneralDOAException {
		super(id, new EntityModel<IServiceDefinition>(
				"/services/application/section_text_update"),
				new DocumentModel(section.getObject(), true));
	}

	@Override
	protected void initForm() throws Exception {
		add(new DocumentField("section_header", getModel(), "header"));

		add(new DocumentTextArea("section_description", getModel(),
				"description").add(new WysywigEditorBehavior()));

		DocumentReferenceField<IStaticResource> imageField = new ImageReferenceField(
				"section_image", new DocumentFieldModel(getModel(),
						"leftImage", true));
		ReturnableEntityWindow<IEntitiesContainer, IStaticResource> selectWindow = imageField
				.createPopupWindow("placeholder_popup",
						new EntityModel<IEntitiesContainer>(
								"/images/application"), new Model<String>(
								"Wybierz obraz"));
		add(selectWindow);
		add(selectWindow.showWindowLink("link_popup_show"));
		add(imageField.setOutputMarkupId(true));

		add(new ValidatingCallServiceLink("call_service"));
	}
}

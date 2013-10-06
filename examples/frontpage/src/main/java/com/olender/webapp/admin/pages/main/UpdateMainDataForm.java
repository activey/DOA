package com.olender.webapp.admin.pages.main;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

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
import pl.doa.wicket.ui.window.ReturnableEntityWindow;

import com.olender.webapp.components.form.ImageReferenceField;
import com.olender.webapp.components.link.ValidatingCallServiceLink;

public class UpdateMainDataForm extends CallServiceForm {

	public UpdateMainDataForm(String id, IModel<IDocument> section)
			throws GeneralDOAException {
		super(id, new EntityModel<IServiceDefinition>(
				"/services/application/main_data_update"), new DocumentModel(
				section.getObject(), true));
	}

	@Override
	protected void initForm() throws Exception {
		DocumentReferenceField<IStaticResource> logoField = new ImageReferenceField(
				"logo", new DocumentFieldModel(getModel(), "logo", true));
		ReturnableEntityWindow<IEntitiesContainer, IStaticResource> selectWindow = logoField
				.createPopupWindow("placeholder_popup",
						new EntityModel<IEntitiesContainer>(
								"/images/application"), new Model<String>(
								"Wybierz logo"));
		add(selectWindow);
		add(selectWindow.showWindowLink("link_popup_show"));
		add(logoField.setOutputMarkupId(true));

		add(new DocumentField("twitterUrl", getModel(), "twitterUrl"));
		add(new DocumentField("facebookUrl", getModel(), "facebookUrl"));
		add(new DocumentField("googlePlusUrl", getModel(), "googlePlusUrl"));

		add(new ValidatingCallServiceLink("call_service"));
	}
}

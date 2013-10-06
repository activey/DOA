package com.olender.webapp.admin.pages.sections.gallery;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.resource.IStaticResource;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.wicket.form.CallServiceForm;
import pl.doa.wicket.model.EntityModel;
import pl.doa.wicket.model.document.field.DocumentListFieldModel;
import pl.doa.wicket.ui.widgets.DocumentField;
import pl.doa.wicket.ui.widgets.DocumentReferenceField;
import pl.doa.wicket.ui.window.ReturnableEntityWindow;

import com.olender.webapp.components.form.ImageReferenceField;
import com.olender.webapp.components.link.ValidatingCallServiceLink;

public class AddImageForm extends CallServiceForm {

	private final IModel<IDocument> section;

	public AddImageForm(String id, IModel<IDocument> section) {
		super(id, "/services/application/image_link");
		this.section = section;
	}

	@Override
	protected void initForm() throws Exception {
		DocumentReferenceField<IStaticResource> imageField = new ImageReferenceField(
				"gallery_image", new DocumentListFieldModel(getModel(),
						"toEntities", "bigImage", true,
						DocumentFieldDataType.reference));
		ReturnableEntityWindow<IEntitiesContainer, IStaticResource> selectImageWindow = imageField
				.createPopupWindow("placeholder_popup_big",
						new EntityModel<IEntitiesContainer>(
								"/images/application"), new Model<String>(
								"Wybierz obraz"));
		add(selectImageWindow);
		add(selectImageWindow.showWindowLink("link_popup_show_big"));
		add(imageField.setOutputMarkupId(true));

		DocumentReferenceField<IStaticResource> smallImageField = new ImageReferenceField(
				"gallery_image_small", new DocumentListFieldModel(getModel(),
						"toEntities", "smallImage", true,
						DocumentFieldDataType.reference));
		ReturnableEntityWindow<IEntitiesContainer, IStaticResource> selectSmallImageWindow = smallImageField
				.createPopupWindow("placeholder_popup_small",
						new EntityModel<IEntitiesContainer>(
								"/images/application"), new Model<String>(
								"Wybierz miniaturę"));
		add(selectSmallImageWindow);
		add(selectSmallImageWindow.showWindowLink("link_popup_show_small"));
		add(smallImageField.setOutputMarkupId(true));

		add(new ValidatingCallServiceLink("create_image_link") {

			@Override
			protected void onBefeforeRun(IServiceDefinition service,
					IDocument input) throws GeneralDOAException {
				input.setFieldValue("fromEntity", section.getObject());
			}

			

			@Override
			protected void onAfterRun(IRunningService runningService,
					IDocument input, AjaxRequestTarget target)
					throws GeneralDOAException {
				clearServiceInput();
				
				AddImageForm.this.handleRunnning(runningService, target);
			}

		});

		add(new DocumentField("gallery_priority", getModel(), "priority"));
	}

	protected void handleRunnning(IRunningService running,
			AjaxRequestTarget target) {
	}
	
	@Override
	protected boolean isTransactional() {
		return true;
	}
}

package com.olender.webapp.admin.pages.sections.gallery;

import com.olender.webapp.behavior.impl.wysywig.WysywigEditorBehavior;
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
import pl.doa.wicket.ui.link.EntityAjaxLink;
import pl.doa.wicket.ui.widgets.DocumentField;
import pl.doa.wicket.ui.widgets.DocumentReferenceField;
import pl.doa.wicket.ui.widgets.DocumentTextArea;
import pl.doa.wicket.ui.window.ReturnableEntityWindow;

import com.olender.webapp.components.form.ImageReferenceField;
import com.olender.webapp.components.link.ValidatingCallServiceLink;

public class EditLinkForm extends CallServiceForm {

	public EditLinkForm(String id, IModel<IDocument> exisingData) {
		super(id, new EntityModel<IServiceDefinition>(
				"/services/application/update_image_link"), exisingData);
	}

	@Override
	protected void initForm() throws Exception {
        add(new DocumentTextArea("description", getModel(), "description")
                .add(new WysywigEditorBehavior()));

		DocumentReferenceField<IStaticResource> thumbnailField = new ImageReferenceField(
				"gallery_thumbnail", new DocumentListFieldModel(getModel(),
						"toEntities", "smallImage", true,
						DocumentFieldDataType.reference));
		ReturnableEntityWindow<IEntitiesContainer, IStaticResource> selectThumbnailWindow = thumbnailField
				.createPopupWindow("placeholder_popup",
						new EntityModel<IEntitiesContainer>(
								"/images/application"), new Model<String>(
								"Wybierz miniaturę"));
		add(selectThumbnailWindow);
		add(selectThumbnailWindow.showWindowLink("link_popup_show"));
		add(thumbnailField.setOutputMarkupId(true));

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

		add(new ValidatingCallServiceLink("save_link") {

			@Override
			protected void onAfterRun(IRunningService runningService,
					IDocument input, AjaxRequestTarget target)
					throws GeneralDOAException {
				EditLinkForm.this.handleRunning(runningService, target);
			}

		});

		add(new EntityAjaxLink<IServiceDefinition>("cancel_link",
				getServiceModel()) {

			@Override
			public void onClick(IModel<IServiceDefinition> service,
					AjaxRequestTarget target) {
				EditLinkForm.this.onCancel(target);
			}
		});

		add(new DocumentField("gallery_priority", getModel(), "priority"));
	}

	protected void onCancel(AjaxRequestTarget target) {
	}

	protected void handleRunning(IRunningService running,
			AjaxRequestTarget target) {
	}
}

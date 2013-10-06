package com.olender.webapp.components.form;

import org.apache.wicket.model.IModel;

import com.olender.webapp.admin.pages.sections.common.SelectResourcePanel;

import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.resource.IStaticResource;
import pl.doa.wicket.model.document.IDocumentAwareModel;
import pl.doa.wicket.ui.panel.ReturnableEntityPanel;
import pl.doa.wicket.ui.widgets.DocumentReferenceField;
import pl.doa.wicket.ui.window.IReturnable;

public class ImageReferenceField extends
		DocumentReferenceField<IStaticResource> {

	public ImageReferenceField(String id, IModel<IDocument> documentModel,
			String fieldName) {
		super(id, documentModel, fieldName);
	}

	public ImageReferenceField(String id,
			IDocumentAwareModel<IDocumentFieldValue> fieldModel) {
		super(id, fieldModel);
	}

	@Override
	protected ReturnableEntityPanel<IStaticResource> createReturnablePanel(
			String panelId, IModel<IEntitiesContainer> containerModel,
			IReturnable<IStaticResource> returnable) {
		return new SelectResourcePanel(panelId, containerModel, returnable);
	}

	@Override
	protected int getPopupHeight() {
		return 480;
	}

	@Override
	protected int getPopupWidth() {
		return 950;
	}
}

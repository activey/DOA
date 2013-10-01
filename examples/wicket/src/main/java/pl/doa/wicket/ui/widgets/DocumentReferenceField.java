/**
 * 
 */
package pl.doa.wicket.ui.widgets;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.entity.IEntity;
import pl.doa.wicket.model.document.IDocumentAwareModel;
import pl.doa.wicket.ui.panel.ReturnableEntityPanel;
import pl.doa.wicket.ui.window.IReturnable;
import pl.doa.wicket.ui.window.ReturnableEntityWindow;

/**
 * @author activey
 * 
 */
public abstract class DocumentReferenceField<T extends IEntity> extends
		DocumentField {

	private final static Logger log = LoggerFactory
			.getLogger(DocumentReferenceField.class);

	public DocumentReferenceField(String id, IModel<IDocument> documentModel,
			String fieldName) {
		super(id, documentModel, fieldName);
	}

	public DocumentReferenceField(String id,
			IDocumentAwareModel<IDocumentFieldValue> fieldModel) {
		super(id, fieldModel);
	}

	public final ReturnableEntityWindow<IEntitiesContainer, T> createPopupWindow(
			String id, final IModel<IEntitiesContainer> containerModel,
			IModel<String> labelModel) {

		return new ReturnableEntityWindow<IEntitiesContainer, T>(id,
				containerModel, labelModel) {

			@Override
			public final int getInitialWidth() {
				return DocumentReferenceField.this.getPopupWidth();
			}

			@Override
			public final int getInitialHeight() {
				return DocumentReferenceField.this.getPopupHeight();
			}

			@Override
			protected ReturnableEntityPanel<T> createReturnablePanel(
					String panelId, IReturnable<T> returnable) {
				return DocumentReferenceField.this.createReturnablePanel(
						panelId, containerModel, returnable);

			}

			protected void onWindowClose(AjaxRequestTarget target, T result) {
				IDocumentFieldValue referenceField =
						(IDocumentFieldValue) DocumentReferenceField.this
								.getModelObject();
				if (referenceField == null) {

					DocumentReferenceField.this.setModelObject(null);
				}
				if (referenceField != null) {
					try {
						referenceField.setFieldValue(result);
						DocumentReferenceField.this.inputChanged();

						target.add(DocumentReferenceField.this);
					} catch (GeneralDOAException e) {
						log.error("", e);
					}
				}

			};

		};
	}

	protected int getPopupHeight() {
		return 400;
	}

	protected int getPopupWidth() {
		return 500;
	}

	protected abstract ReturnableEntityPanel<T> createReturnablePanel(
			String panelId, IModel<IEntitiesContainer> containerModel,
			IReturnable<T> returnable);

}

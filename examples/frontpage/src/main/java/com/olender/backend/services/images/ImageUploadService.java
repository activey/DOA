/**
 * 
 */
package com.olender.backend.services.images;

import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.IListDocumentFieldValue;
import pl.doa.entity.ITransactionCallback;
import pl.doa.resource.IStaticResource;

import com.olender.backend.services.BaseServiceDefinitionLogic;

/**
 * @author activey
 * 
 */
public class ImageUploadService extends BaseServiceDefinitionLogic {

	/* (non-Javadoc)
	 * @see pl.doa.service.AsynchronousServiceDefinitionLogic#alignAsync()
	 */
	@Override
	protected void alignAsync() throws GeneralDOAException {
		IDocument input = getInput();
		
		final IListDocumentFieldValue images =
				(IListDocumentFieldValue) input.getField("images");
		if (images == null) {
			setOutput(createOutputDocument("void"));
			return;
		}

		doa.doInTransaction(new ITransactionCallback<Void>() {

			public Void performOperation() throws Exception {
				IDocument input = getInput();
				IEntitiesContainer imagesContainer =
					(IEntitiesContainer) input.getFieldValue("imagesContainer");
				Iterable<IDocumentFieldValue> imagesCollection =
						images.iterateFields();
				for (IDocumentFieldValue imageField : imagesCollection) {
					final IStaticResource uploadedImage =
							(IStaticResource) imageField.getFieldValue();
					imagesContainer.addEntity(uploadedImage);
				}
				return null;
			}
		});
		setOutput(createOutputDocument("void"));
	}
}

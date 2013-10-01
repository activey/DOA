/**
 * 
 */
package pl.doa.wicket.ui.widgets;

import org.apache.wicket.model.IModel;

import pl.doa.document.IDocument;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.wicket.model.document.IDocumentAwareModel;

/**
 * @author activey
 * 
 */
public class DocumentPasswordField extends DocumentField {

	public DocumentPasswordField(String id,
			IDocumentAwareModel<IDocumentFieldValue> fieldModel) {
		super(id, fieldModel);
	}

	public DocumentPasswordField(String id, IModel<IDocument> documentModel,
			String fieldName) {
		super(id, documentModel, fieldName);
	}

	@Override
	protected String getInputType() {
		return "password";
	}
}

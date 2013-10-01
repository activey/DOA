/**
 * 
 */
package pl.doa.wicket.model.document.field;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import pl.doa.document.IDocument;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.IListDocumentFieldValue;
import pl.doa.utils.IteratorCollection;
import pl.doa.wicket.model.document.DocumentModel;

/**
 * @author activey
 * 
 */
public class ListDocumentFieldModel extends
		AbstractReadOnlyModel<List<IDocumentFieldValue>> {

	private final IModel<IDocument> documentModel;
	private final String fieldName;

	public ListDocumentFieldModel(IDocument document, String fieldName) {
		this.documentModel = new DocumentModel(document);
		this.fieldName = fieldName;
	}

	public ListDocumentFieldModel(IModel<IDocument> documentModel,
			String fieldName) {
		this.documentModel = documentModel;
		this.fieldName = fieldName;
	}

	@Override
	public void detach() {
	}

	@Override
	public List<IDocumentFieldValue> getObject() {
		IDocument document = documentModel.getObject();
		IDocumentFieldValue field = document.getField(fieldName);
		if (!(field instanceof IListDocumentFieldValue)) {
			return null;
		}
		IListDocumentFieldValue listField = (IListDocumentFieldValue) field;
		return new ArrayList(new IteratorCollection(listField.iterateFields()
				.iterator()));
	}
}

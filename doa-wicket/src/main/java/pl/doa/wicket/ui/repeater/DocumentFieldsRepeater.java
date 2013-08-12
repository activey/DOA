package pl.doa.wicket.ui.repeater;

import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;

import pl.doa.document.IDocument;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.wicket.model.document.field.DocumentFieldsModel;

public abstract class DocumentFieldsRepeater extends
		ListView<IDocumentFieldValue> {

	public DocumentFieldsRepeater(String id, IModel<IDocument> documentModel) {
		super(id, new DocumentFieldsModel(documentModel));
	}

	public DocumentFieldsRepeater(String id, IDocument document) {
		super(id, new DocumentFieldsModel(document));
	}
}

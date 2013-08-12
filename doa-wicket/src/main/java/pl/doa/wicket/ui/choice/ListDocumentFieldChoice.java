/**
 * 
 */
package pl.doa.wicket.ui.choice;

import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;

import pl.doa.document.IDocument;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.wicket.converter.DocumentFieldConverter;
import pl.doa.wicket.model.document.DocumentModel;
import pl.doa.wicket.model.document.field.DocumentFieldModel;

/**
 * @author activey
 * 
 */
public class ListDocumentFieldChoice extends
		DropDownChoice<IDocumentFieldValue> {

	private final IModel<IDocument> documentModel;
	private final String fieldName;

	public ListDocumentFieldChoice(String id, IModel<IDocument> documentModel,
			String fieldName, IModel<List<IDocumentFieldValue>> choicesModel) {
		super(id, new DocumentFieldModel(documentModel, fieldName),
				choicesModel);
		this.fieldName = fieldName;
		this.documentModel = documentModel;
	}

	public ListDocumentFieldChoice(String id, IDocument document,
			String fieldName, IModel<List<IDocumentFieldValue>> choicesModel) {
		this(id, new DocumentModel(document), fieldName, choicesModel);
	}

	public IConverter getConverter(Class type) {
		return new DocumentFieldConverter(documentModel, fieldName);
	}
}

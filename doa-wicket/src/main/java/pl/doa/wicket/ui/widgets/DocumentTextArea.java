/**
 * 
 */
package pl.doa.wicket.ui.widgets;

import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;

import pl.doa.document.IDocument;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.wicket.converter.DocumentFieldConverter;
import pl.doa.wicket.model.document.field.DocumentFieldModel;
import pl.doa.wicket.ui.feedback.FeedbackLabel;

/**
 * @author activey
 * 
 */
public class DocumentTextArea extends TextArea<IDocumentFieldValue> {

	private final IModel<IDocument> documentModel;
	private final String fieldName;

	public DocumentTextArea(String id, IModel<IDocument> documentModel,
			String fieldName) {
		super(id, new DocumentFieldModel(documentModel, fieldName));
		setType(IDocumentFieldValue.class);
		this.documentModel = documentModel;
		this.fieldName = fieldName;
	}

	@SuppressWarnings("all")
	public IConverter getConverter(Class type) {
		return new DocumentFieldConverter(documentModel, fieldName);
	}

	public final FeedbackLabel createFeedbackLabel(String labelId) {
		return new FeedbackLabel(labelId, this) {
			@Override
			public boolean isVisible() {
				return DocumentTextArea.this.hasFeedbackMessage();
			}
		};
	}
}

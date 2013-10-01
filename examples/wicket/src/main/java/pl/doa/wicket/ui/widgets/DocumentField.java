/**
 * 
 */
package pl.doa.wicket.ui.widgets;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;

import pl.doa.document.IDocument;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.wicket.converter.DocumentFieldConverter;
import pl.doa.wicket.model.document.IDocumentAwareModel;
import pl.doa.wicket.model.document.field.DocumentFieldModel;
import pl.doa.wicket.ui.feedback.FeedbackLabel;

/**
 * @author activey
 * 
 */
public class DocumentField extends TextField<IDocumentFieldValue> {

	protected IModel<IDocument> documentModel;
	protected String fieldName;

	public DocumentField(String id,
			IDocumentAwareModel<IDocumentFieldValue> fieldModel) {
		super(id, fieldModel, IDocumentFieldValue.class);
		this.documentModel = fieldModel.getDocumentModel();
		this.fieldName = fieldModel.getFieldName();

		initFieldConstraints();
	}

	private void initFieldConstraints() {
		IDocumentFieldValue fieldValue = getModelObject();
		if (fieldValue == null) {
			return;
		}
		IDocumentFieldType fieldType = fieldValue.getFieldType();

		this.setRequired(fieldType.isRequired());

		DocumentFieldDataType dataType = fieldType.getFieldDataType();
		switch (dataType) {
		case bigdecimal:
			setType(BigDecimal.class);
			break;
		case bool:
			setType(Boolean.class);
			break;
		case date:
			setType(Date.class);
			break;
		case doubleprec:
			setType(Double.class);
			break;
		case integer:
			setType(Integer.class);
			break;
		case longinteger:
			setType(Long.class);
			break;
		default:
			break;
		}
	}

	public DocumentField(String id, IModel<IDocument> documentModel,
			String fieldName) {
		this(id, new DocumentFieldModel(documentModel, fieldName, true));
	}

	@SuppressWarnings("all")
	public IConverter getConverter(Class type) {
		//if (IDocumentFieldValue.class.isAssignableFrom(type)) {
			return (this.documentModel != null) ? new DocumentFieldConverter(
					documentModel, fieldName) : new DocumentFieldConverter(
					getModel());
		//}
		//return super.getConverter(type);
	}

	protected final void onComponentTag(final ComponentTag tag) {
		// Default handling for component tag
		super.onComponentTag(tag);

		tag.put("value", getModelValue());
	}

	@Override
	protected String getInputType() {
		return "text";
	}

	public final FeedbackLabel createFeedbackLabel(String labelId) {
		return new FeedbackLabel(labelId, this) {
			@Override
			public boolean isVisible() {
				return DocumentField.this.hasFeedbackMessage();
			}
		};
	}
}

/**
 * 
 */
package pl.doa.wicket.converter;

import java.util.Locale;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.document.field.IDocumentFieldValue;

/**
 * @author activey
 * 
 */
public class DocumentFieldConverter implements IConverter<IDocumentFieldValue> {

	private final static Logger log = LoggerFactory
			.getLogger(DocumentFieldConverter.class);

	private IModel<IDocument> documentModel;
	private String fieldName;
	private IModel<? extends IDocumentFieldValue> fieldModel;

	public DocumentFieldConverter(IModel<IDocument> documentModel,
			String fieldName) {
		this.documentModel = documentModel;
		this.fieldName = fieldName;
	}

	public DocumentFieldConverter(IModel<? extends IDocumentFieldValue> fieldModel) {
		this.fieldModel = fieldModel;
	}

	@Override
	public IDocumentFieldValue convertToObject(String fieldValue, Locale arg1) {
		IDocumentFieldValue field;
		try {
			field =
					(documentModel != null) ? documentModel.getObject()
							.getField(fieldName, true) : fieldModel.getObject();
			if (field == null) {
				return null;
			}
			field.setFieldValue(fieldValue);
		} catch (GeneralDOAException e) {
			log.error("", e);
			return null;
		}
		return field;
	}

	@Override
	public String convertToString(IDocumentFieldValue field, Locale arg1) {
		return field.getFieldValueAsString();
	}

}

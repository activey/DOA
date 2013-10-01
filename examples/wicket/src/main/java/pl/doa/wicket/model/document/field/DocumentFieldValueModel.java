/**
 * 
 */
package pl.doa.wicket.model.document.field;

import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.wicket.model.document.DocumentModel;

/**
 * @author activey
 * 
 */
public class DocumentFieldValueModel<T extends Object> implements IModel<T> {

	private final static Logger log = LoggerFactory
			.getLogger(DocumentFieldValueModel.class);

	private final IModel<IDocument> documentModel;
	private final String fieldName;

	public DocumentFieldValueModel(IDocument document, String fieldName) {
		this(new DocumentModel(document), fieldName);
	}

	public DocumentFieldValueModel(IModel<IDocument> documentModel,
			String fieldName) {
		this.documentModel = documentModel;
		this.fieldName = fieldName;
	}

	@Override
	public void detach() {
	}

	@Override
	public T getObject() {
		IDocument doc = documentModel.getObject();
		if (doc == null) {
			return null;
		}
		T fieldValue = (T) doc.getFieldValue(fieldName);
		return fieldValue;
	}

	@Override
	public void setObject(T fieldValue) {
		IDocument document = documentModel.getObject();
		try {
			document.setFieldValue(fieldName, fieldValue);
		} catch (GeneralDOAException e) {
			log.error("", e);
		}
	}

}

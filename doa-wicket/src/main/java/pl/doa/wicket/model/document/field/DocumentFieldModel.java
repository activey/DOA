/**
 * 
 */
package pl.doa.wicket.model.document.field;

import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.wicket.model.document.DocumentModel;
import pl.doa.wicket.model.document.IDocumentAwareModel;

/**
 * @author activey
 * 
 */
public class DocumentFieldModel implements
		IDocumentAwareModel<IDocumentFieldValue> {

	private final static Logger log = LoggerFactory
			.getLogger(DocumentFieldModel.class);

	private final IModel<IDocument> documentModel;
	private final String fieldName;

	private final boolean createIfNull;

	public DocumentFieldModel(IDocument document, String fieldName) {
		this(document, fieldName, false);
	}

	public DocumentFieldModel(IDocument document, String fieldName,
			boolean createIfNull) {
		this(new DocumentModel(document), fieldName);
	}

	public DocumentFieldModel(IModel<IDocument> documentModel, String fieldName) {
		this(documentModel, fieldName, false);
	}

	public DocumentFieldModel(IModel<IDocument> documentModel,
			String fieldName, boolean createIfNull) {
		this.createIfNull = createIfNull;
		this.documentModel = documentModel;
		this.fieldName = fieldName;

	}

	@Override
	public void detach() {
	}

	@Override
	public IDocumentFieldValue getObject() {
		IDocument doc = documentModel.getObject();
		if (doc == null) {
			return null;
		}
		IDocumentFieldValue field;
		try {
			field = doc.getField(fieldName, createIfNull);
		} catch (GeneralDOAException e) {
			log.error("", e);
			return null;
		}
		return field;
	}

	@Override
	public void setObject(IDocumentFieldValue fieldValue) {
		IDocument document = documentModel.getObject();
		try {
			document.setFieldValue(fieldName, fieldValue);
		} catch (GeneralDOAException e) {
			log.error("", e);
		}
	}

	@Override
	public IModel<IDocument> getDocumentModel() {
		return documentModel;
	}

	@Override
	public String getFieldName() {
		return this.fieldName;
	}

}

/**
 * 
 */
package pl.doa.wicket.model.document.field;

import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.IListDocumentFieldValue;
import pl.doa.wicket.model.document.DocumentModel;
import pl.doa.wicket.model.document.IDocumentAwareModel;

/**
 * @author activey
 * 
 */
public class DocumentListFieldModel implements
		IDocumentAwareModel<IDocumentFieldValue> {

	private final static Logger log = LoggerFactory
			.getLogger(DocumentListFieldModel.class);

	private final IModel<IDocument> documentModel;
	private final String fieldName;

	private final String listField;

	private final boolean createIfInull;

	private final DocumentFieldDataType defaultDataType;

	public DocumentListFieldModel(IDocument document, String listField,
			String fieldName) {
		this(document, listField, fieldName, false);
	}

	public DocumentListFieldModel(IDocument document, String listField,
			String fieldName, boolean createIfNull) {
		this(new DocumentModel(document), listField, fieldName, createIfNull,
				null);
	}

	public DocumentListFieldModel(IModel<IDocument> documentModel,
			String listField, String fieldName) {
		this(documentModel, listField, fieldName, false, null);

	}

	public DocumentListFieldModel(IModel<IDocument> documentModel,
			String listField, String fieldName, boolean createIfInull,
			DocumentFieldDataType defaultDataType) {
		this.documentModel = documentModel;
		this.listField = listField;
		this.fieldName = fieldName;
		this.createIfInull = createIfInull;
		this.defaultDataType = defaultDataType;
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
			field = doc.getField(listField, createIfInull);
		} catch (GeneralDOAException e) {
			log.error("", e);
			return null;
		}
		if (field == null) {
			return null;
		}
		if (!(field instanceof IListDocumentFieldValue)) {
			return null;
		}
		IListDocumentFieldValue listField = (IListDocumentFieldValue) field;
		IDocumentFieldValue innerField = listField.getListField(fieldName);
		if (innerField == null && createIfInull) {
			try {
				innerField = listField.addField(fieldName, defaultDataType);
			} catch (GeneralDOAException e) {
				log.error("", e);
			}
		}
		return innerField;
	}

	@Override
	public void setObject(IDocumentFieldValue field) {
		IDocument doc = documentModel.getObject();
		if (doc == null) {
			return;
		}
		try {
			IDocumentFieldValue potentialList = doc.getField(listField, true);
			if (!(potentialList instanceof IListDocumentFieldValue)) {
				return;
			}
			IListDocumentFieldValue list =
					(IListDocumentFieldValue) potentialList;
			if (field == null) {
				return;
			}
			list.copyFrom(field);
		} catch (GeneralDOAException e) {
			log.error("", e);
			return;
		}
	}

	@Override
	public IModel<IDocument> getDocumentModel() {
		return documentModel;
	}

	@Override
	public String getFieldName() {
		return fieldName;
	}

}

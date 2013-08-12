package pl.doa.wicket.model.document;

import org.apache.wicket.model.IModel;

import pl.doa.document.IDocument;

public interface IDocumentAwareModel<T> extends IModel<T> {

	public IModel<IDocument> getDocumentModel();

	public String getFieldName();

}

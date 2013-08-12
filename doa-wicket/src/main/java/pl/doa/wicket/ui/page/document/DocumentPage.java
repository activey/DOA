/**
 * 
 */
package pl.doa.wicket.ui.page.document;

import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.doa.document.IDocument;
import pl.doa.utils.PathIterator;
import pl.doa.wicket.model.document.DocumentModel;
import pl.doa.wicket.ui.page.EntityPage;

/**
 * @author activey
 * 
 */
public class DocumentPage extends EntityPage<IDocument> {

	private static final String DEFAULT_DOCUMENT_LOCATION = "/index.html";

	public DocumentPage() {
		super();
	}

	public DocumentPage(IDocument entity) {
		super(entity);
	}

	public DocumentPage(PageParameters parameters) {
		super(parameters);
	}

	public DocumentPage(PathIterator<String> documentPath) {
		super(documentPath);
	}

	public DocumentPage(String documentLocation) {
		super(documentLocation);
	}

	@Override
	protected IModel<IDocument> getDefaultEntityModel() {
		return new DocumentModel(DEFAULT_DOCUMENT_LOCATION);
	}
}

/**
 * 
 */
package pl.doa.wicket.ui.link.document;

import pl.doa.document.IDocument;
import pl.doa.wicket.model.document.DocumentModel;
import pl.doa.wicket.ui.link.EntityLink;
import pl.doa.wicket.ui.page.document.DocumentPage;

/**
 * @author activey
 * 
 */
public class DocumentLink extends EntityLink<IDocument> {

	private static final long serialVersionUID = 1L;

	public DocumentLink(String id, final IDocument document,
			Class<? extends DocumentPage> pageClass,
			final IDocumentLinkLabel label) {
		super(id, new DocumentModel(document), pageClass, label);
	}

	public DocumentLink(String id, IDocument document,
			Class<? extends DocumentPage> pageClass) {
		this(id, document, pageClass, null);
	}
}

/**
 *
 */
package pl.doa.wicket.ui.link.document;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

import pl.doa.document.IDocument;

/**
 * @author activey
 */
public class DocumentActionLink extends Link<IDocument> {

    public DocumentActionLink(String id, IModel<IDocument> model) {
        super(id, model);
    }

    @Override
    public final void onClick() {
        onClick(getModelObject());
    }

    public void onClick(IDocument document) {

    }
}

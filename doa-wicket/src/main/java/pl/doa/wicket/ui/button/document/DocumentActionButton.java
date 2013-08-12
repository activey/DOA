/**
 * 
 */
package pl.doa.wicket.ui.button.document;

import org.apache.wicket.model.IModel;

import pl.doa.document.IDocument;
import pl.doa.wicket.ui.button.EntityActionButton;

/**
 * @author activey
 * 
 */
public class DocumentActionButton extends EntityActionButton<IDocument> {

	public DocumentActionButton(String id, IModel<IDocument> model) {
		super(id, model);
	}

}

/**
 * 
 */
package pl.doa.wicket.model.document;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.wicket.model.EntityModel;

/**
 * @author activey
 * 
 */
public class DocumentModel<T extends IDocument> extends EntityModel<T> {

	public DocumentModel(T entity, boolean copy)
			throws GeneralDOAException {
		super((copy) ? (T) entity.createCopy() : entity);
	}

	public DocumentModel(T entity) {
		super(entity);
	}

	public DocumentModel(long entityId) {
		super(entityId);
	}

	public DocumentModel(String entityLocation) {
		super(entityLocation);
	}
}

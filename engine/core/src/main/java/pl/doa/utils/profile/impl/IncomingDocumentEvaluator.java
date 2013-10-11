/**
 *
 */
package pl.doa.utils.profile.impl;

import java.util.ArrayList;
import java.util.List;

import pl.doa.document.IDocument;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.utils.IteratorCollection;

/**
 * @author activey
 */
public class IncomingDocumentEvaluator implements IEntityEvaluator {

    private IDocument incomingDocument;
    private List<IDocumentFieldType> authorizableFields = new ArrayList<IDocumentFieldType>();

    public IncomingDocumentEvaluator(IDocument incomingDocument) {
        this.incomingDocument = incomingDocument;
        authorizableFields.addAll(new IteratorCollection<IDocumentFieldType>(
                incomingDocument.getDefinition().getAuthorizableFields()));

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * pl.doa.entity.IEntityEvaluator#isReturnableEntity(pl.doa.entity.IEntity)
     */
    @Override
    public boolean isReturnableEntity(IEntity currentEntity) {
        if (authorizableFields.size() == 0) {
            return false;
        }
        if (!(currentEntity instanceof IDocument)) {
            return false;
        }
        IDocument doc = (IDocument) currentEntity;
        if (!(doc.isDefinedBy(incomingDocument.getDefinition()))) {
            return false;
        }
        // result is true if all are equal, or one is equal and the rest is null
        boolean result = false;

        for (IDocumentFieldType authorizableField : authorizableFields) {
            IDocumentFieldValue obj1 = doc
                    .getField(authorizableField.getName());
            IDocumentFieldValue obj2 = incomingDocument
                    .getField(authorizableField.getName());

            if (obj1 != null) {
                if (obj1.compareTo(obj2) == 0) {
                    result = true;
                } else {
                    return false;
                }
            }
        }
        return result;
    }


}

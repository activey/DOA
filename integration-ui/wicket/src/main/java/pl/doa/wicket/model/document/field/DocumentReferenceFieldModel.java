/**
 *
 */
package pl.doa.wicket.model.document.field;

import org.apache.wicket.model.IModel;

import pl.doa.document.IDocument;
import pl.doa.resource.IStaticResource;

/**
 * @author activey
 */
public class DocumentReferenceFieldModel extends
        DocumentFieldValueModel<IStaticResource> {

    public DocumentReferenceFieldModel(IDocument document, String fieldName) {
        super(document, fieldName);
    }

    public DocumentReferenceFieldModel(IModel<IDocument> documentModel,
                                       String fieldName) {
        super(documentModel, fieldName);
    }

}

/**
 *
 */
package pl.doa.wicket.model.document.field;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.utils.IteratorCollection;
import pl.doa.utils.IteratorIterable;
import pl.doa.wicket.model.document.DocumentModel;

/**
 * @author activey
 */
public class DocumentFieldsModel extends
        AbstractReadOnlyModel<List<IDocumentFieldValue>> {

    private final static Logger log = LoggerFactory
            .getLogger(DocumentFieldsModel.class);

    private final IModel<IDocument> documentModel;

    public DocumentFieldsModel(IDocument document) {
        this.documentModel = new DocumentModel(document);
    }

    public DocumentFieldsModel(IModel<IDocument> model) {
        this.documentModel = model;
    }

    @Override
    public void detach() {
    }

    @Override
    public List<IDocumentFieldValue> getObject() {
        IDocument document = documentModel.getObject();
        IDocumentDefinition def = document.getDefinition();
        Iterable<String> names =
                new IteratorIterable<String>(def.getFieldNames());
        for (String fieldName : names) {
            try {
                document.getField(fieldName, true);
            } catch (GeneralDOAException e) {
                log.error("", e);
            }
        }
        return new ArrayList(new IteratorCollection(document.getFields()));
    }
}

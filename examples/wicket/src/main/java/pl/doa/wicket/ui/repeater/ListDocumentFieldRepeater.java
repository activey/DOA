package pl.doa.wicket.ui.repeater;

import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;

import pl.doa.document.IDocument;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.wicket.model.document.field.ListDocumentFieldModel;

public abstract class ListDocumentFieldRepeater extends
        ListView<IDocumentFieldValue> {

    public ListDocumentFieldRepeater(String id,
                                     IModel<IDocument> documentModel, String fieldName) {
        super(id, new ListDocumentFieldModel(documentModel, fieldName));
    }

}

package pl.doa.wicket.ui.widgets;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;

import pl.doa.document.IDocument;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.wicket.converter.DocumentFieldConverter;
import pl.doa.wicket.model.document.field.DocumentFieldModel;

public class DocumentFieldLabel extends Label {
    protected IModel<IDocument> documentModel;
    protected String fieldName;

    public DocumentFieldLabel(String id, IModel<IDocumentFieldValue> fieldModel) {
        super(id, fieldModel);
    }

    public DocumentFieldLabel(String id, IModel<IDocument> documentModel,
                              String fieldName) {
        super(id, new DocumentFieldModel(documentModel, fieldName));
    }

    @SuppressWarnings("all")
    public IConverter getConverter(Class type) {
        if (IDocumentFieldValue.class.isAssignableFrom(type)) {
            return (this.documentModel != null) ? new DocumentFieldConverter(
                    documentModel, fieldName) : new DocumentFieldConverter(
                    getModel());
        }
        return null;
    }

    public final IModel<IDocumentFieldValue> getModel() {
        return (IModel<IDocumentFieldValue>) getDefaultModel();
    }

    public final IDocumentFieldValue getModelObject() {
        return (IDocumentFieldValue) getDefaultModelObject();
    }
}

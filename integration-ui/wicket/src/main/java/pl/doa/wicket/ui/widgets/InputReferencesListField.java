/**
 *
 */
package pl.doa.wicket.ui.widgets;

import java.util.Collection;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.document.IDocument;
import pl.doa.document.field.IListDocumentFieldValue;
import pl.doa.resource.IStaticResource;
import pl.doa.wicket.WicketDOAApplication;
import pl.doa.wicket.ui.feedback.FeedbackLabel;

/**
 * @author activey
 */
public class InputReferencesListField extends FileUploadField {

    private final static Logger log = LoggerFactory
            .getLogger(InputReferencesListField.class);

    private final IModel<IDocument> documentModel;
    private final String fieldName;

    public InputReferencesListField(String id, IModel<IDocument> documentModel,
                                    String fieldName) {
        super(id);
        this.documentModel = documentModel;
        this.fieldName = fieldName;
    }

    @Override
    public void updateModel() {
        super.updateModel();
        Collection<FileUpload> uploadedFiles = getFileUploads();
        if (uploadedFiles == null || uploadedFiles.size() == 0) {
            return;
        }
        IDocument doc = documentModel.getObject();
        if (doc == null) {
            return;
        }

        // wypelnianie pola typu lista o nazwie fieldNAme
        IListDocumentFieldValue listField = null;
        try {
            listField = (IListDocumentFieldValue) doc.getField(fieldName, true);
            if (listField.countFields() > 0) {
                listField.remove();
                listField = (IListDocumentFieldValue) doc.getField(fieldName,
                        true);
            }
        } catch (GeneralDOAException e1) {
            log.error("", e1);
            return;
        }
        IDOA doa = WicketDOAApplication.get().getDoa();
        for (FileUpload uploadedFile : uploadedFiles) {
            if (uploadedFile != null) {
                try {
                    IStaticResource res = doa.createStaticResource(
                            uploadedFile.getClientFileName(),
                            uploadedFile.getContentType());
                    res.setContentFromStream(uploadedFile.getInputStream(),
                            uploadedFile.getSize());
                    listField.addReferenceField(
                            uploadedFile.getClientFileName(), res);
                } catch (Exception e) {
                    error(e.getMessage());
                    log.error("", e);
                }

            }
        }
    }

    public final FeedbackLabel createFeedbackLabel(String labelId) {
        return new FeedbackLabel(labelId, this) {
            @Override
            public boolean isVisible() {
                return InputReferencesListField.this.hasFeedbackMessage();
            }
        };
    }

}

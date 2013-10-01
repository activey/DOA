/**
 *
 */
package pl.doa.wicket.ui.widgets;

import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.IDOA;
import pl.doa.document.IDocument;
import pl.doa.resource.IStaticResource;
import pl.doa.wicket.WicketDOAApplication;

/**
 * @author activey
 */
public class InputReferenceField extends FileUploadField {

    private final static Logger log = LoggerFactory
            .getLogger(InputReferenceField.class);

    private final IModel<IDocument> documentModel;
    private final String fieldName;

    public InputReferenceField(String id, IModel<IDocument> documentModel,
                               String fieldName) {
        super(id);
        this.documentModel = documentModel;
        this.fieldName = fieldName;
    }

    @Override
    public void updateModel() {
        super.updateModel();
        FileUpload uploadedFile = getFileUpload();
        if (uploadedFile == null) {
            return;
        }
        IDocument doc = documentModel.getObject();
        if (doc == null) {
            return;
        }
        IDOA doa = WicketDOAApplication.get().getDoa();
        if (uploadedFile != null) {
            try {
                IStaticResource res =
                        doa.createStaticResource(
                                uploadedFile.getClientFileName(),
                                uploadedFile.getContentType());
                res.setContentFromStream(uploadedFile.getInputStream(),
                        uploadedFile.getSize());
                doc.setFieldValue(fieldName, res);
            } catch (Exception e) {
                error(e.getMessage());
                log.error("", e);
            }

        }

    }

}

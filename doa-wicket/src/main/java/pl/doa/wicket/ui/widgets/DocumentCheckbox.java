/**
 * 
 */
package pl.doa.wicket.ui.widgets;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;

/**
 * @author activey
 * 
 */
public class DocumentCheckbox extends CheckBox {

	private final static Logger log = LoggerFactory
			.getLogger(DocumentCheckbox.class);

	public DocumentCheckbox(String id, final IModel<IDocument> documentModel,
			final String fieldName) {
		super(id, new IModel<Boolean>() {

			@Override
			public void detach() {
			}

			@Override
			public Boolean getObject() {
				IDocument doc = documentModel.getObject();
				if (doc == null) {
					return false;
				}
				return (Boolean) doc.getFieldValue(fieldName, false);
			}

			@Override
			public void setObject(Boolean object) {
				IDocument doc = documentModel.getObject();
				if (doc == null) {
					return;
				}
				try {
					doc.setFieldValue(fieldName, object);
				} catch (GeneralDOAException e) {
					log.error("", e);
				}
			}
		});
	}

}

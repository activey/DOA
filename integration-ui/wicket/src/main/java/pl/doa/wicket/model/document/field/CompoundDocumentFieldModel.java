/**
 * 
 */
package pl.doa.wicket.model.document.field;

import org.apache.wicket.model.AbstractReadOnlyModel;

import pl.doa.document.IDocument;
import pl.doa.document.field.IDocumentFieldValue;

/**
 * @author activey
 * 
 */
public class CompoundDocumentFieldModel extends AbstractReadOnlyModel<String> {

	private IDocument document;
	private String[] fieldNames;
	private final String separator;

	public CompoundDocumentFieldModel(IDocument document, String separator,
			String... fieldNames) {
		this.document = document;
		this.separator = separator;
		this.fieldNames = fieldNames;
	}

	@Override
	public String getObject() {
		StringBuilder builder = new StringBuilder();
		for (String fieldName : fieldNames) {
			IDocumentFieldValue field = document.getField(fieldName);
			if (field == null) {
				continue;
			}
			builder.append(field.getFieldValueAsString()).append(separator);
		}
		return builder.toString();
	}

}

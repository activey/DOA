/**
 * 
 */
package pl.doa.templates;

import java.io.IOException;
import java.io.OutputStream;

import nu.xom.Element;
import nu.xom.Serializer;

/**
 * @author activey
 * 
 */
public class TemplateSerializer extends Serializer {

	private final boolean preserveNamespace;

	private boolean printEmptyTags = true;

	public TemplateSerializer(OutputStream out, boolean preserveNamespace) {
		super(out);
		this.preserveNamespace = preserveNamespace;
	}

	public TemplateSerializer(OutputStream out, boolean preserveNamespace,
			boolean printEmptyTags) {
		super(out);
		this.preserveNamespace = preserveNamespace;
		this.printEmptyTags = printEmptyTags;
	}

	@Override
	protected void writeNamespaceDeclarations(Element element)
			throws IOException {
		if (!preserveNamespace) {
			return;
		}
		super.writeNamespaceDeclarations(element);
	}

	@Override
	protected void writeEmptyElementTag(Element element) throws IOException {
		if (printEmptyTags) {
			writeStartTag(element);
			writeEndTag(element);
			return;
		}
		super.writeEmptyElementTag(element);
	}

}

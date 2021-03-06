package pl.doa.http.ext.webdav.resource;

import pl.doa.IDOA;
import pl.doa.document.IDocument;

import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.ResourceFactory;

public class WebDavResourceFactory implements ResourceFactory {

	private final IDocument requestDocument;
	private final IDOA doa;

	public WebDavResourceFactory(IDOA doa, IDocument requestDocument) {
		this.doa = doa;
		this.requestDocument = requestDocument;
	}

	@Override
	public Resource getResource(String host, String path) {
		IDocument applicationDoc = (IDocument) requestDocument
				.getFieldValue("applicationDocument");
		String rootUri = requestDocument.getFieldValueAsString("rootUri");
		String applicationUri = path.substring(rootUri.length());
		if (applicationUri.trim().length() == 0) {
			applicationUri = "/";
		}
		ResourceLocator locator = new ResourceLocator(doa,
				applicationDoc.getContainer());
		return locator.locateResource(applicationUri);
	}

}

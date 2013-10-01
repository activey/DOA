/**
 * 
 */
package pl.doa.http.ext.webdav.processor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import pl.doa.IDOA;
import pl.doa.document.IDocument;
import pl.doa.entity.ITransactionCallback;
import pl.doa.http.ext.webdav.request.DOARequest;
import pl.doa.http.ext.webdav.request.DOAResponse;
import pl.doa.http.ext.webdav.resource.WebDavResourceFactory;
import pl.doa.resource.IStaticResource;
import pl.doa.servlet.filter.processor.IRequestProcessor;

import com.bradmcevoy.http.HttpManager;

/**
 * @author activey
 * 
 */
public class WebdavProcessor implements IRequestProcessor {

	@Override
	public void processRequest(final IDOA doa, final IDocument requestDocument,
			final IDocument responseDocument) throws Exception {

		ByteArrayOutputStream outputStream = doa
				.doInTransaction(new ITransactionCallback<ByteArrayOutputStream>() {

					@Override
					public ByteArrayOutputStream performOperation()
							throws Exception {
						HttpManager manager = new HttpManager(
								new WebDavResourceFactory(doa, requestDocument));
						ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
						DOAResponse response = new DOAResponse(
								responseDocument, outputStream);
						manager.process(new DOARequest(requestDocument),
								response);
						return outputStream;
					}
				});

		// tworzenie wyniku
		int size = outputStream.size();
		if (size > 0) {
			IStaticResource resource = doa.createStaticResource(
					"responseEntity", "text/xml");
			resource.setContentFromStream(
					new ByteArrayInputStream(outputStream.toByteArray()),
					(long) size);
			responseDocument.setFieldValue("response", resource);
		}
	}

	@Override
	public boolean matches(String uri) {
		return true;
	}

}

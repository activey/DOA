/**
 *
 */
package pl.doa.servlet.filter.processor.rest;

import pl.doa.IDOA;
import pl.doa.document.IDocument;
import pl.doa.resource.IStaticResource;

/**
 * @author activey
 */
public class BinaryResourceResponse implements RestCallResponse {

    private final IStaticResource resource;

    public BinaryResourceResponse(IStaticResource resource) {
        this.resource = resource;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.shelf.web.rest.RestCallResponse#commitResponse(pl.doa.document.IDocument
     * )
     */
    @Override
    public void commitResponse(IDOA doa, IDocument responseDocument)
            throws Exception {
        responseDocument.setFieldValue("response", resource);
    }

}

/**
 *
 */
package pl.doa.servlet.filter.processor.rest;

import pl.doa.IDOA;
import pl.doa.document.IDocument;

/**
 * @author activey
 */
public class EmptyResponse implements RestCallResponse {

    /*
     * (non-Javadoc)
     *
     * @see
     * com.shelf.web.rest.RestCallResponse#commitResponse(pl.doa.document.IDocument
     * )
     */
    @Override
    public void commitResponse(IDOA doa, IDocument responseDocument) {

    }

}

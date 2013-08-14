/**
 *
 */
package pl.doa.servlet.filter.processor.rest.json;

import org.json.JSONException;
import org.json.JSONObject;

import pl.doa.IDOA;
import pl.doa.document.IDocument;
import pl.doa.resource.IStaticResource;
import pl.doa.servlet.filter.processor.rest.RestCallResponse;

/**
 * @author activey
 */
public class JsonResponse<T extends Object> implements RestCallResponse {

    private JSONObject jsonResponse;
    protected final T object;

    public JsonResponse(T object) {
        this.object = object;
        this.jsonResponse = new JSONObject();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.shelf.web.rest.RestCallResponse#commitResponse(pl.doa.document.IDocument
     * )
     */
    @Override
    public final void commitResponse(IDOA doa, IDocument responseDocument)
            throws Exception {
        buildJSON(jsonResponse);

        IStaticResource jsonResource = doa
                .createStaticResource("application/json");
        jsonResource.setContentFromBytes(jsonResponse.toString().getBytes());
        responseDocument.setFieldValue("response", jsonResource);

    }

    protected void buildJSON(JSONObject json) throws Exception {

    }

}

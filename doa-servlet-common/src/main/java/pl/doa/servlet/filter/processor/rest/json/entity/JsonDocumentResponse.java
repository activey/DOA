/**
 *
 */
package pl.doa.servlet.filter.processor.rest.json.entity;

import org.json.JSONObject;

import pl.doa.document.IDocument;
import pl.doa.mapper.json.DocumentMapper;

/**
 * @author activey
 */
public class JsonDocumentResponse extends JsonEntityResponse<IDocument> {

    public JsonDocumentResponse(IDocument entity) {
        super(entity);
    }

    @Override
    protected void buildJSON(JSONObject json) throws Exception {
        new DocumentMapper().map(object, json);
    }
}

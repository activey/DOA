/**
 *
 */
package pl.doa.servlet.filter.processor.rest.json.entity;

import org.json.JSONObject;

import pl.doa.mapper.json.StaticResourceMapper;
import pl.doa.resource.IStaticResource;

/**
 * @author activey
 */
public class JsonResourceResponse extends JsonEntityResponse<IStaticResource> {

    public JsonResourceResponse(IStaticResource entity) {
        super(entity);
    }

    @Override
    protected void buildJSON(JSONObject json) throws Exception {
        new StaticResourceMapper().map(object, json);
    }
}

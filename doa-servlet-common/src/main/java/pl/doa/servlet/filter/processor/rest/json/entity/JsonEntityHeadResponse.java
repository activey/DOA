/**
 *
 */
package pl.doa.servlet.filter.processor.rest.json.entity;

import org.json.JSONObject;

import pl.doa.entity.IEntity;
import pl.doa.mapper.json.EntityHeadMapper;
import pl.doa.servlet.filter.processor.rest.json.JsonResponse;

/**
 * @author activey
 */
public class JsonEntityHeadResponse<T extends IEntity> extends JsonResponse<T> {

    public JsonEntityHeadResponse(T entity) {
        super(entity);
    }

    protected void buildJSON(JSONObject json) throws Exception {
        new EntityHeadMapper<IEntity>().map(object, json);
    }

}

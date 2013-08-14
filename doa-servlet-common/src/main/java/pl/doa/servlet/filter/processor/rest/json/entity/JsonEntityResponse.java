/**
 *
 */
package pl.doa.servlet.filter.processor.rest.json.entity;

import org.json.JSONObject;

import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.entity.IEntity;
import pl.doa.mapper.json.BasicEntityMapper;
import pl.doa.resource.IStaticResource;

/**
 * @author activey
 */
public class JsonEntityResponse<T extends IEntity> extends
        JsonEntityHeadResponse<T> {

    public JsonEntityResponse(T entity) {
        super(entity);
    }

    @Override
    protected void buildJSON(JSONObject json) throws Exception {
        new BasicEntityMapper<IEntity>().map(object, json);
    }

    public static JsonEntityResponse<? extends IEntity> getResponse(
            IEntity entity) {
        if (entity instanceof IEntitiesContainer) {
            return new JsonContainerResponse((IEntitiesContainer) entity);
        } else if (entity instanceof IStaticResource) {
            return new JsonResourceResponse((IStaticResource) entity);
        } else if (entity instanceof IDocument) {
            return new JsonDocumentResponse((IDocument) entity);
        }
        return new JsonEntityResponse<IEntity>(entity);
    }
}

/**
 *
 */
package pl.doa.servlet.filter.processor.rest.json.entity;

import org.json.JSONObject;

import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.mapper.json.ContainerMapper;

/**
 * @author activey
 */
public class JsonContainerResponse extends
        JsonEntityResponse<IEntitiesContainer> {

    private final IEntityEvaluator evaluator;

    public JsonContainerResponse(IEntitiesContainer entity) {
        this(entity, null);
    }

    public JsonContainerResponse(IEntitiesContainer entity,
                                 IEntityEvaluator evaluator) {
        super(entity);
        this.evaluator = evaluator;
    }

    protected boolean isRenderFull() {
        return false;
    }

    @Override
    protected void buildJSON(JSONObject json) throws Exception {
        new ContainerMapper(evaluator, true).map(object, json);
    }
}

/**
 *
 */
package pl.doa.mapper.json;

import org.json.JSONException;
import org.json.JSONObject;
import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.agent.IAgent;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.entity.IEntity;
import pl.doa.mapper.EntityMapper;
import pl.doa.resource.IStaticResource;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;

/**
 * @author activey
 */
public class EntityHeadMapper<T extends IEntity> extends
        EntityMapper<T, JSONObject> {

    public static final String TYPE_ENTITY = "entity";
    public static final String TYPE_RESOURCE = "resource";
    public static final String TYPE_RUNNING_SERVICE = "running_service";
    public static final String TYPE_AGENT = "agent";
    public static final String TYPE_SERVICE_DEFINITION = "service_definition";
    public static final String TYPE_CONTAINER = "container";
    public static final String TYPE_DOCUMENT_DEFINITION = "document_definition";
    public static final String TYPE_DOCUMENT = "document";

    private static String getEntityTypeName(IEntity entity) {
        if (entity instanceof IDocument) {
            return TYPE_DOCUMENT;
        } else if (entity instanceof IDocumentDefinition) {
            return TYPE_DOCUMENT_DEFINITION;
        } else if (entity instanceof IEntitiesContainer) {
            return TYPE_CONTAINER;
        } else if (entity instanceof IServiceDefinition) {
            return TYPE_SERVICE_DEFINITION;
        } else if (entity instanceof IAgent) {
            return TYPE_AGENT;
        } else if (entity instanceof IRunningService) {
            return TYPE_RUNNING_SERVICE;
        } else if (entity instanceof IStaticResource) {
            return TYPE_RESOURCE;
        }
        return TYPE_ENTITY;
    }

    @Override
    public void map(T entity, JSONObject object) throws GeneralDOAException {
        if (entity == null) {
            return;
        }
        try {
            object.put("UUID", entity.getId());
            object.put("name", entity.getName());
            object.put("location", entity.getLocation());
            object.put("type", getEntityTypeName(entity));
        } catch (JSONException e) {
            throw new GeneralDOAException(e);
        }

    }

    @Override
    public T map(JSONObject object, IDOA doa) throws GeneralDOAException {
        return null;
    }

}

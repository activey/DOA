/**
 * 
 */
package pl.doa.mapper.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.resource.IStaticResource;

/**
 * @author activey
 * 
 */
public class ContainerMapper extends BasicEntityMapper<IEntitiesContainer> {

	private IEntityEvaluator evaluator;

	public ContainerMapper() {
		this(null, false);
	}

	public ContainerMapper(IEntityEvaluator evaluator, boolean renderFull) {
		super(renderFull);	
		this.evaluator = evaluator;
	}

	@Override
	public void map(IEntitiesContainer entity, JSONObject object)
			throws GeneralDOAException {
		super.map(entity, object);
		if (!isRenderFull()) {
			return;
		}		
		JSONArray entitiesArray = new JSONArray();
		Iterable<? extends IEntity> entities = entity.getEntities(evaluator);
		for (IEntity containerEntity : entities) {
			JSONObject entityJson = new JSONObject();
			if (containerEntity instanceof IDocument) {
				new DocumentMapper().map((IDocument) containerEntity,
						entityJson);
			} else if (containerEntity instanceof IStaticResource) {
				new StaticResourceMapper().map(
						(IStaticResource) containerEntity, entityJson);
			} else if (containerEntity instanceof IEntitiesContainer) {
				new ContainerMapper().map(
						(IEntitiesContainer) containerEntity, entityJson);
			}

			entitiesArray.put(entityJson);
		}
		try {
			object.put("entities", entitiesArray);
		} catch (JSONException e) {
			throw new GeneralDOAException(e);
		}
	}

	@Override
	protected IEntitiesContainer map(JSONObject object,
			IEntitiesContainer entity) throws Exception {
		super.map(object, entity);
		return entity;
	}
}

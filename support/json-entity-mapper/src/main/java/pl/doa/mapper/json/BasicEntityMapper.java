/**
 * 
 */
package pl.doa.mapper.json;

import org.json.JSONException;
import org.json.JSONObject;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.document.IDocumentDefinition;
import pl.doa.entity.IEntity;

/**
 * @author activey
 * 
 */
public class BasicEntityMapper<T extends IEntity> extends EntityHeadMapper<T> {

	private boolean renderFull;

	public BasicEntityMapper() {
		this(true);
	}

	public BasicEntityMapper(boolean renderFull) {
		this.renderFull = renderFull;
	}

	protected boolean isRenderFull() {
		return this.renderFull;
	}

	@Override
	public void map(T entity, JSONObject object) throws GeneralDOAException {
		super.map(entity, object);

		if (!isRenderFull() || entity == null) {
			return;
		}
		try {
			object.put("createdAt", entity.getCreated().getTime());
			object.put("modifiedAt", entity.getLastModified().getTime());
		} catch (JSONException e) {
			throw new GeneralDOAException(e);
		}

	}

	@Override
	public final T map(JSONObject object, IDOA doa) throws GeneralDOAException {
		String entityType;
		try {
			entityType = object.getString("type");
		} catch (JSONException e) {
			throw new GeneralDOAException(e);
		}

		IEntity entity = null;
		if (TYPE_DOCUMENT.equals(entityType)) {
			String definitionLocation;
			try {
				definitionLocation = object.getString("definitionLocation");
			} catch (JSONException e) {
				throw new GeneralDOAException(e);
			}
			IEntity definitionEntity = doa
					.lookupEntityByLocation(definitionLocation);
			if (definitionEntity == null
					|| !(definitionEntity instanceof IDocumentDefinition)) {
				throw new GeneralDOAException(
						"Unable to find document definition!");
			}
			IDocumentDefinition definition = (IDocumentDefinition) definitionEntity;
			String name;
			try {
				name = object.getString("name");
			} catch (JSONException e) {
				throw new GeneralDOAException(e);
			}
			entity = doa.createDocument(name, definition);
		} else if (TYPE_CONTAINER.equals(entityType)) {

		}

		if (entity == null) {
			return null;
		} else
			try {
				return map(object, (T) entity);
			} catch (Throwable e) {
				return null;
			}

	}

	protected T map(JSONObject object, T entity) throws Exception {
		return null;
	}

}

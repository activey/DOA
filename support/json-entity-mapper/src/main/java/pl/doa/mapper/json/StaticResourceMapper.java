/**
 * 
 */
package pl.doa.mapper.json;

import org.json.JSONException;
import org.json.JSONObject;

import pl.doa.GeneralDOAException;
import pl.doa.resource.IStaticResource;

/**
 * @author activey
 * 
 */
public class StaticResourceMapper extends BasicEntityMapper<IStaticResource> {

	@Override
	public void map(IStaticResource entity, JSONObject object)
			throws GeneralDOAException {
		super.map(entity, object);

		try {
			object.put("mimeType", entity.getMimetype());
			object.put("size", entity.getContentSize());
		} catch (JSONException e) {
			throw new GeneralDOAException(e);
		}

	}

	@Override
	protected IStaticResource map(JSONObject object, IStaticResource entity)
			throws Exception {
		super.map(object, entity);
		return entity;
	}

}

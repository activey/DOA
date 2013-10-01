/**
 * 
 */
package pl.doa.wicket.mount;

import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.doa.entity.IEntity;

/**
 * @author activey
 * 
 */
public class EntityLocationParameters<T extends IEntity> extends PageParameters {

	public EntityLocationParameters(T entity) {
		add("location", entity.getLocation());
	}

	public EntityLocationParameters(IModel<T> entityModel) {
		T entity = entityModel.getObject();
		if (entity == null) {
			return;
		}
		add("location", entity.getLocation());
	}
}

/**
 * 
 */
package pl.doa.wicket.model;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import pl.doa.entity.IEntity;

/**
 * @author activey
 * 
 */
public class EntityLocationModel<T extends IEntity> extends
		AbstractReadOnlyModel<String> {

	private final IModel<T> entityModel;
	private final String defaultValue;

	public EntityLocationModel(IModel<T> entityModel) {
		this(entityModel, null);
	}

	public EntityLocationModel(IModel<T> entityModel, String defaultValue) {
		this.entityModel = entityModel;
		this.defaultValue = defaultValue;
	}

	@Override
	public String getObject() {
		T entity = entityModel.getObject();
		if (entity == null) {
			return defaultValue;
		}
		return entity.getLocation();
	}

}

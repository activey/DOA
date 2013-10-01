/**
 * 
 */
package pl.doa.wicket.model;

import java.util.Date;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import pl.doa.entity.IEntity;

/**
 * @author activey
 * 
 */
public class EntityModifiedModel<T extends IEntity> extends
		AbstractReadOnlyModel<Date> {

	private final IModel<T> entityModel;

	public EntityModifiedModel(IModel<T> entityModel) {
		this.entityModel = entityModel;
	}

	@Override
	public Date getObject() {
		T entity = entityModel.getObject();
		if (entity == null) {
			return null;
		}
		return entity.getLastModified();
	}

}

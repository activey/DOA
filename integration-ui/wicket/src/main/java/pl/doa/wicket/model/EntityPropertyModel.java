/**
 * 
 */
package pl.doa.wicket.model;

import java.io.Serializable;

import org.apache.wicket.model.IModel;

import pl.doa.entity.IEntity;

/**
 * @author activey
 * 
 */
public abstract class EntityPropertyModel<T extends Serializable> implements
		IModel<T> {

	private final IModel<? extends IEntity> entityModel;

	public EntityPropertyModel(IModel<? extends IEntity> entityModel) {
		this.entityModel = entityModel;
	}

	@Override
	public void detach() {
	}

	@Override
	public final T getObject() {
		return getPropertyValue(entityModel);
	}

	protected abstract T getPropertyValue(IModel<? extends IEntity> entityModel);

	@Override
	public void setObject(T object) {
		setPropertyValue(entityModel, object);
	}

	protected abstract void setPropertyValue(
			IModel<? extends IEntity> entityModel, T propertyValue);

}

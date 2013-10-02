package pl.doa.wicket.decorator;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import pl.doa.entity.IEntity;

public interface IEntityDecorator<T extends IEntity> {

	public Component decorate(IModel<T> entityModel, String componentId)
			throws Exception;

	
}

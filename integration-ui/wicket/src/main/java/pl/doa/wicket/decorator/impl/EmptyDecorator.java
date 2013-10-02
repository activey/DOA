/**
 * 
 */
package pl.doa.wicket.decorator.impl;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import pl.doa.entity.IEntity;
import pl.doa.wicket.decorator.IEntityDecorator;

/**
 * @author activey
 * 
 */
public class EmptyDecorator implements IEntityDecorator<IEntity> {

	@Override
	public Component decorate(IModel<IEntity> entityModel, String componentId)
			throws Exception {
		return new EmptyDecoratorPanel(componentId, entityModel);
	}

}

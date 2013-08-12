/**
 * 
 */
package pl.doa.wicket.form;

import org.apache.wicket.model.IModel;

import pl.doa.entity.IEntity;
import pl.doa.wicket.model.EntityModel;

/**
 * @author activey
 * 
 */
public class EntityForm<T extends IEntity> extends UpdatableForm<T> {

	public EntityForm(String id, IModel<T> model) {
		super(id, model);
	}

	public EntityForm(String id, String entityLocation) {
		super(id, new EntityModel<T>(entityLocation));
	}

	@Override
	protected void initForm() throws Exception {
	}

}

/**
 * 
 */
package pl.doa.wicket.ui.button;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.IModel;

import pl.doa.entity.IEntity;

/**
 * @author activey
 * 
 */
public class EntityActionButton<T extends IEntity> extends Button {

	private final IModel<T> model;

	public EntityActionButton(String id, IModel<T> model) {
		super(id);
		this.model = model;
	}

	@Override
	public final void onSubmit() {
		onSubmit(model.getObject());
	}

	public void onSubmit(T entity) {

	}

	@Override
	protected final void detachModel() {
		super.detachModel();
		this.model.detach();
	}

}

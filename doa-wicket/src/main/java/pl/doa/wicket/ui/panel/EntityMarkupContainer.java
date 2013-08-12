/**
 * 
 */
package pl.doa.wicket.ui.panel;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import pl.doa.entity.IEntity;

/**
 * @author activey
 * 
 */
public class EntityMarkupContainer<T extends IEntity> extends WebMarkupContainer {

	public EntityMarkupContainer(String id, IModel<T> model) {
		super(id, model);
		setOutputMarkupId(true);
	}

	public IModel<T> getModel() {
		return (IModel<T>) getDefaultModel();
	}

	public final T getModelObject() {
		return (T) getDefaultModelObject();
	}

	@Override
	protected final void onInitialize() {
		super.onInitialize();
		initializeContainer();
	}

	protected void initializeContainer() {
	}
}

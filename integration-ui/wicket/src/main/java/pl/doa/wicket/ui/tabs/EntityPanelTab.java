/**
 * 
 */
package pl.doa.wicket.ui.tabs;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import pl.doa.entity.IEntity;
import pl.doa.wicket.model.EntityModel;
import pl.doa.wicket.ui.panel.EntityPanel;

/**
 * @author activey
 * 
 */
public abstract class EntityPanelTab<T extends IEntity> implements ITab {

	private IModel<T> entityModel;

	public EntityPanelTab(IModel<T> entityModel) {
		this.entityModel = entityModel;
	}

	public EntityPanelTab(T entity) {
		this(new EntityModel<T>(entity));
	}

	public EntityPanelTab(String entityLocation) {
		this(new EntityModel<T>(entityLocation));
	}

	protected abstract EntityPanel<T> getEntityPanel(String panelId);

	@Override
	public final WebMarkupContainer getPanel(String containerId) {
		return getEntityPanel(containerId);
	}

	public final IModel<T> getModel() {
		return this.entityModel;
	}

	public boolean isVisible() {
		return true;
	}

}

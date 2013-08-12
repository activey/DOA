/**
 * 
 */
package pl.doa.wicket.decorator.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.entity.IEntity;
import pl.doa.wicket.WicketDOAApplication;
import pl.doa.wicket.decorator.IEntityDecorator;
import pl.doa.wicket.decorator.IEntityTabBuilder;
import pl.doa.wicket.decorator.IEntityTabLabel;
import pl.doa.wicket.model.EntityModel;
import pl.doa.wicket.ui.panel.EntityPanel;
import pl.doa.wicket.ui.tabs.EntityPanelTab;
import pl.doa.wicket.ui.tabs.TabbedPanel;

/**
 * @author activey
 * 
 */
public class TabbedEntityDecorator<T extends IEntity> implements
		IEntityDecorator<T> {

	private static final long serialVersionUID = 1L;

	private final static Logger log = LoggerFactory
			.getLogger(TabbedEntityDecorator.class);

	private List<ITab> collectedTabs = new ArrayList<ITab>();

	public TabbedPanel decorate(IModel<T> model, String componentId)
			throws Exception {
		// tworzenie zakladek
		collectTabs(model);

		return createTabbedPanel(componentId, collectedTabs, model);
	}

	/**
	 * Metoda uruchamiana jest przez dekorator zakladkowy w celu zebrania
	 * kolekcji zakladek, ktore nalezy wyrenderowac.
	 * 
	 * Aby utworzyc zakladke, nalezy przeciazyc ta metode wykonujac w jej ciele
	 * metode createTab()
	 * 
	 * @throws Exception
	 */
	protected void collectTabs(IModel<T> model) throws Exception {
	}

	protected TabbedPanel createTabbedPanel(String componentId,
			List<ITab> collectedTabs, IModel<T> model) {
		return new TabbedPanel(componentId, collectedTabs);
	}

	protected final <S extends IEntity> EntityPanelTab<S> createTab(
			IModel<S> entityModel, IEntityTabBuilder<S> tabBuilder) {
		String nextTabId = "tab_" + collectedTabs.size() + 1;
		return createTab(nextTabId, entityModel, tabBuilder);
	}

	protected final void createTabs(Iterable<? extends IEntity> entities,
			final IEntityTabLabel<IEntity> label) {
		for (IEntity entity : entities) {
			createTab(new EntityModel<IEntity>(entity),
					new IEntityTabBuilder<IEntity>() {

						@Override
						public EntityPanel<IEntity> buildTabPanel(
								IModel<IEntity> entity, String panelId) {
							try {
								return (EntityPanel<IEntity>) WicketDOAApplication
										.get().decorateEntity(entity, panelId);
							} catch (Exception e) {
								log.error("", e);
								return null;
							}
						}

						@Override
						public String getTabLabel(IModel<IEntity> entity) {
							return (label == null) ? entity.getObject()
									.getName() : label.getTabLabel(entity);
						}
					});
		}
	}

	protected final <S extends IEntity> EntityPanelTab<S> createTab(
			String tabId, final IModel<S> entityModel,
			IEntityTabBuilder<S> tabBuilder) {
		EntityPanelTab<S> newTab = new EntityPanelTab<S>(entityModel) {

			private IEntityTabBuilder<S> builder;

			public IModel<String> getTitle() {
				return new AbstractReadOnlyModel<String>() {

					@Override
					public String getObject() {
						String label = builder.getTabLabel(entityModel);
						IModel<String> resourceModel =
								new StringResourceModel(label, null);
						try {
							return resourceModel.getObject();
						} catch (Throwable t) {
							return label;
						}
					}
				};

			}

			@Override
			protected EntityPanel<S> getEntityPanel(String panelId) {
				return builder.buildTabPanel(entityModel, panelId);
			}

			public EntityPanelTab<S> setTabBuilder(IEntityTabBuilder<S> builder) {
				this.builder = builder;
				return this;
			}

		}.setTabBuilder(tabBuilder);
		collectedTabs.add(newTab);
		return newTab;
	}
}

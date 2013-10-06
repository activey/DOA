/**
 * 
 */
package com.olender.webapp.admin.pages.images;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.IAjaxCallListener;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.wicket.model.EntityModel;
import pl.doa.wicket.model.container.ContainerEntitiesProvider;
import pl.doa.wicket.ui.link.EntityAjaxLink;
import pl.doa.wicket.ui.link.EntityLink;
import pl.doa.wicket.ui.page.EntityPage;
import pl.doa.wicket.ui.panel.EntityMarkupContainer;
import pl.doa.wicket.ui.panel.container.EntitiesContainerPanel;

import com.olender.webapp.admin.pages.ImagesPage;
import com.olender.webapp.components.ajax.ConfirmListener;
import com.olender.webapp.components.ajax.FormCallListener;

/**
 * @author activey
 * 
 */
public class ImagesContainerPanel extends EntitiesContainerPanel {

	private final static String ENTITY_LOCATION = "/images/application";

	private class SelectedModifier extends AttributeModifier {

		public SelectedModifier(final IEntity containerEntity) {
			super("class", new AbstractReadOnlyModel<String>() {

				@Override
				public String getObject() {
					Page currentPage = getPage();
					if (currentPage instanceof EntityPage) {
						EntityPage<? extends IEntity> entityPage =
								(EntityPage<? extends IEntity>) currentPage;
						IEntity currentEntity = entityPage.getModelObject();
						if (currentEntity == null) {
							return null;
						}
						if (currentEntity.equals(containerEntity)) {
							return "active";
						}

					}
					return null;
				}
			});
		}
	}

	private final IModel<IEntitiesContainer> baseContainerModel;

	public ImagesContainerPanel(String id,
			IModel<IEntitiesContainer> containerModel) {
		super(id, containerModel);
		this.baseContainerModel =
				new EntityModel<IEntitiesContainer>(ENTITY_LOCATION);
		setOutputMarkupId(true);

	}

	protected AbstractLink createFolderLink(String linkId,
			IModel<IEntitiesContainer> containerModel) {
		return new EntityLink<IEntitiesContainer>(linkId, containerModel,
				ImagesPage.class);
	}

	@Override
	protected void initContainerPanel() throws Exception {
		add(new EntityMarkupContainer<IEntitiesContainer>("parent_container_entity",
				getModel()) {
			@Override
			protected void initializeContainer() {
				add(createFolderLink("parent_container_link",
						new AbstractReadOnlyModel<IEntitiesContainer>() {

							private IModel<IEntitiesContainer> model;

							@Override
							public IEntitiesContainer getObject() {
								return model.getObject().getContainer();
							}

							public IModel<IEntitiesContainer> setCurrentModel(
									IModel<IEntitiesContainer> model) {
								this.model = model;
								return this;
							}
						}.setCurrentModel(getModel())));
			}

			public boolean isVisible() {
				return getModelObject()
						.isInside(baseContainerModel.getObject());
			}
		});

		// lista obiektow z aktualnego kontenera
		DataView<IEntitiesContainer> containerEntities =
				new DataView<IEntitiesContainer>("container_entity",
						new ContainerEntitiesProvider<IEntitiesContainer>(
								getModel(), new IEntityEvaluator() {

									public boolean isReturnableEntity(
											IEntity currentEntity) {
										return currentEntity instanceof IEntitiesContainer;
									}
								})) {

					@Override
					protected void populateItem(Item<IEntitiesContainer> item) {
						IModel<IEntitiesContainer> model = item.getModel();
						AbstractLink entityLink =
								createFolderLink("entity_link", model);
						IEntitiesContainer containerEntity = model.getObject();
						entityLink.add(new Label("entity_name", new Model(
								containerEntity.getName())));
						item.add(entityLink);
						item.add(new SelectedModifier(containerEntity));

						item.add(new EntityAjaxLink<IEntitiesContainer>(
								"container_remove", item.getModel(), true) {

							@Override
							public void onClick(
									IModel<IEntitiesContainer> container,
									AjaxRequestTarget target) {
								container.getObject().remove(true);
								target.add(ImagesContainerPanel.this);
							}

							@Override
							protected void populateListeners(
									List<IAjaxCallListener> listeners) {
								listeners.add(new ConfirmListener());
								listeners.add(new FormCallListener());
							}
						});

					}

				};

		add(containerEntities);
	}
}

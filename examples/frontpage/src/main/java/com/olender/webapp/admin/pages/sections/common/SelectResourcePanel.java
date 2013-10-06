/**
 * 
 */
package com.olender.webapp.admin.pages.sections.common;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;

import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.resource.IStaticResource;
import pl.doa.wicket.model.container.ContainerEntitiesProvider;
import pl.doa.wicket.ui.link.EntityAjaxLink;
import pl.doa.wicket.ui.panel.EntityMarkupContainer;
import pl.doa.wicket.ui.panel.ReturnableEntityPanel;
import pl.doa.wicket.ui.resource.StaticResourceReference;
import pl.doa.wicket.ui.window.IReturnable;

import com.olender.webapp.admin.pages.images.AjaxImagesContainerPanel;

/**
 * @author activey
 * 
 */
public class SelectResourcePanel extends ReturnableEntityPanel<IStaticResource> {

	public SelectResourcePanel(String id,
			IModel<IEntitiesContainer> containerModel,
			IReturnable<IStaticResource> returnable) {
		super(id, containerModel, returnable);
	}

	@Override
	protected void initEntityPanel() throws Exception {

		add(new EntityMarkupContainer<IEntitiesContainer>("folders_container",
				getModel()) {
			@Override
			protected void initializeContainer() {
				add(new AjaxImagesContainerPanel("panel_current_container",
						getModel()) {
					@Override
					protected void containerChanged(
							IModel<IEntitiesContainer> newContainer,
							AjaxRequestTarget target) {
						SelectResourcePanel.this.getModel().setObject(
								newContainer.getObject());
						target.add(SelectResourcePanel.this
								.get("images_container"));
						target.add(SelectResourcePanel.this
								.get("folders_container"));
					}
				});
			}
		});

		add(new EntityMarkupContainer<IEntitiesContainer>("images_container",
				getModel()) {
			@Override
			protected void initializeContainer() {
				add(new DataView<IStaticResource>("placeholder_image",
						new ContainerEntitiesProvider<IStaticResource>(
								getModel(), new IEntityEvaluator() {

									public boolean isReturnableEntity(
											IEntity currentEntity) {
										return currentEntity instanceof IStaticResource;
									}
								}, false)) {
					@Override
					protected void populateItem(final Item<IStaticResource> item) {
						final IStaticResource res = item.getModelObject();
						item.add(new Label("resource_name", res.getName()));

						item.add(new Image("resource_thumbnail",
								new StaticResourceReference(item.getModel())));

						EntityAjaxLink<IStaticResource> removeLink =
								new EntityAjaxLink<IStaticResource>(
										"link_select", item.getModel()) {

									@Override
									public void onClick(
											IModel<IStaticResource> resource,
											AjaxRequestTarget target) {
										SelectResourcePanel.this.publishResult(
												target, resource.getObject());
									}

								};
						item.add(removeLink);
					}
				});
			}
		});

	}

}

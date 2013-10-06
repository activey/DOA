/**
 * 
 */
package com.olender.webapp.admin.pages.images;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;

import pl.doa.container.IEntitiesContainer;
import pl.doa.wicket.ui.link.EntityAjaxLink;

/**
 * @author activey
 * 
 */
public class AjaxImagesContainerPanel extends ImagesContainerPanel {

	public AjaxImagesContainerPanel(String id,
			IModel<IEntitiesContainer> containerModel) {
		super(id, containerModel);
	}

	protected void containerChanged(IModel<IEntitiesContainer> newContainer,
			AjaxRequestTarget target) {

	}

	@Override
	protected AbstractLink createFolderLink(String linkId,
			IModel<IEntitiesContainer> containerModel) {
		return new EntityAjaxLink<IEntitiesContainer>(linkId, containerModel) {

			@Override
			public void onClick(IModel<IEntitiesContainer> container,
					AjaxRequestTarget target) {
				AjaxImagesContainerPanel.this.containerChanged(container,
						target);
			}
		};
	}
}

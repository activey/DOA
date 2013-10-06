/**
 * 
 */
package com.olender.webapp.admin.pages.images;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import pl.doa.resource.IStaticResource;
import pl.doa.wicket.ui.panel.EntityPanel;
import pl.doa.wicket.ui.window.EntityWindow;

/**
 * @author activey
 * 
 */
public class MoveResourceWindow extends EntityWindow<IStaticResource> {

	public MoveResourceWindow(String id, IModel<IStaticResource> model,
			IModel<String> titleModel) {
		super(id, model, titleModel);
	}

	public MoveResourceWindow(String id, IStaticResource entity,
			IModel<String> titleModel) {
		super(id, entity, titleModel);
	}

	public MoveResourceWindow(String id, String entityLocation,
			IModel<String> titleModel) {
		super(id, entityLocation, titleModel);
	}

	@Override
	protected EntityPanel<IStaticResource> createEntityPanel(String panelId) {
		return new MoveResourcePanel(panelId, getModel()) {
			@Override
			protected void resourceMoved(AjaxRequestTarget target) {
				MoveResourceWindow.this.close(target);
				
				MoveResourceWindow.this.resourceMoved(target);
			}
		};
	}

	protected void resourceMoved(AjaxRequestTarget target) {
	}

}

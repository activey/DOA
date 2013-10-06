/**
 * 
 */
package com.olender.webapp.admin.pages.images;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import pl.doa.container.IEntitiesContainer;
import pl.doa.resource.IStaticResource;
import pl.doa.wicket.model.container.EntitiesContainerModel;
import pl.doa.wicket.ui.panel.EntityPanel;

/**
 * @author activey
 * 
 */
public class MoveResourcePanel extends EntityPanel<IStaticResource> {

	private IModel<IEntitiesContainer> containerModel =
			new EntitiesContainerModel("/images/application");

	public MoveResourcePanel(String id, IModel<IStaticResource> entityModel) {
		super(id, entityModel);
	}

	public MoveResourcePanel(String id, IStaticResource entity) {
		super(id, entity);
	}

	public MoveResourcePanel(String id, String entityLocation) {
		super(id, entityLocation);
	}

	@Override
	protected void initEntityPanel() throws Exception {
		
		
		add(new MoveResourceForm("move_form", getModel(), containerModel) {
			@Override
			protected void resourceMoved(AjaxRequestTarget target) {
				MoveResourcePanel.this.resourceMoved(target);
			}
		});
	}

	protected void resourceMoved(AjaxRequestTarget target) {
	}
}

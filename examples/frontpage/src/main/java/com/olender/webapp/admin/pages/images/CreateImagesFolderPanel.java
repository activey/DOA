/**
 * 
 */
package com.olender.webapp.admin.pages.images;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import pl.doa.container.IEntitiesContainer;
import pl.doa.service.IRunningService;
import pl.doa.wicket.ui.panel.EntityPanel;

/**
 * @author activey
 * 
 */
public class CreateImagesFolderPanel extends EntityPanel<IEntitiesContainer> {

	public CreateImagesFolderPanel(String id,
			IModel<IEntitiesContainer> entityModel) {
		super(id, entityModel);
	}

	@Override
	protected void initEntityPanel() throws Exception {
		add(new CreateImagesFolderForm("form", getModel()) {
			@Override
			protected void handleRunning(IRunningService running,
					AjaxRequestTarget target) {
				CreateImagesFolderPanel.this.handleRunning(running, target);
			}
		});
	}

	protected void handleRunning(IRunningService running,
			AjaxRequestTarget target) {
	}
}

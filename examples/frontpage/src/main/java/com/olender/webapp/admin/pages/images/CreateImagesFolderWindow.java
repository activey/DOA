/**
 * 
 */
package com.olender.webapp.admin.pages.images;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import pl.doa.container.IEntitiesContainer;
import pl.doa.service.IRunningService;
import pl.doa.wicket.ui.panel.EntityPanel;
import pl.doa.wicket.ui.window.EntityWindow;

/**
 * @author activey
 * 
 */
public class CreateImagesFolderWindow extends EntityWindow<IEntitiesContainer> {

	public CreateImagesFolderWindow(String id,
			IModel<IEntitiesContainer> parentModel) {
		super(id, parentModel, new Model<String>("Utwórz nowy folder"));
		setInitialWidth(450);
		setInitialHeight(115);
	}

	@Override
	protected EntityPanel<IEntitiesContainer> createEntityPanel(String panelId) {
		return new CreateImagesFolderPanel(panelId, getModel()) {
			@Override
			protected void handleRunning(IRunningService running,
					AjaxRequestTarget target) {
				CreateImagesFolderWindow.this.close(target);
			}
		};
	}

}

/**
 * 
 */
package com.olender.webapp.admin.pages.images;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.resource.IStaticResource;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.wicket.form.CallServiceForm;
import pl.doa.wicket.ui.panel.EntityMarkupContainer;

import com.olender.webapp.components.link.ValidatingCallServiceLink;

/**
 * @author activey
 * 
 */
public class MoveResourceForm extends CallServiceForm {

	private final IModel<IStaticResource> resourceModel;
	private final IModel<IEntitiesContainer> containerModel;

	public MoveResourceForm(String id, IModel<IStaticResource> resourceModel,
			IModel<IEntitiesContainer> containerModel) {
		super(id, "/services/application/move_resource");
		this.resourceModel = resourceModel;
		this.containerModel = containerModel;
	}

	@Override
	protected void initForm() throws Exception {
		add(new EntityMarkupContainer<IEntitiesContainer>("folders_container",
				containerModel) {
			@Override
			protected void initializeContainer() {
				add(new AjaxImagesContainerPanel("panel_current_container",
						getModel()) {
					@Override
					protected void containerChanged(
							IModel<IEntitiesContainer> newContainer,
							AjaxRequestTarget target) {
						containerModel.setObject(newContainer.getObject());
						target.add(MoveResourceForm.this
								.get("folders_container"));
					}
				});
			}
		});

		add(new ValidatingCallServiceLink("submit_link") {
			@Override
			protected void onBefeforeRun(IServiceDefinition service,
					IDocument input) throws GeneralDOAException {
				input.setFieldValue("resource", resourceModel.getObject());
				input.setFieldValue("container", containerModel.getObject());
			}

			@Override
			protected void onAfterRun(IRunningService runningService,
					IDocument input, AjaxRequestTarget target)
					throws GeneralDOAException {
				MoveResourceForm.this.resourceMoved(target);
			}

		});
	}

	protected void resourceMoved(AjaxRequestTarget target) {
	}

	@Override
	protected boolean isTransactional() {
		return true;
	}
}

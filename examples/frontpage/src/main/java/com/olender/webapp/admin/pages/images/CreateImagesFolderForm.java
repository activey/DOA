/**
 * 
 */
package com.olender.webapp.admin.pages.images;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.wicket.form.CallServiceForm;
import pl.doa.wicket.ui.widgets.DocumentField;

import com.olender.webapp.components.link.ValidatingCallServiceLink;

/**
 * @author activey
 * 
 */
public class CreateImagesFolderForm extends CallServiceForm {

	private final IModel<IEntitiesContainer> parentModel;

	public CreateImagesFolderForm(String id,
			IModel<IEntitiesContainer> parentModel) {
		super(id, "/services/application/create_folder");
		this.parentModel = parentModel;
	}

	@Override
	protected void initForm() throws Exception {
		add(new DocumentField("folder_name", getModel(), "folder_name"));
		add(new ValidatingCallServiceLink("submit_link") {
			@Override
			protected void onBefeforeRun(IServiceDefinition service,
					IDocument input) throws GeneralDOAException {
				input.setFieldValue("parent", parentModel.getObject());
			}

			@Override
			protected void onAfterRun(IRunningService runningService,
					IDocument input, AjaxRequestTarget target)
					throws GeneralDOAException {
				CreateImagesFolderForm.this.handleRunning(runningService,
						target);
			}

		});
	}

	protected void handleRunning(IRunningService running,
			AjaxRequestTarget target) {
	}
	
	@Override
	protected boolean isTransactional() {
		return true;
	}
}

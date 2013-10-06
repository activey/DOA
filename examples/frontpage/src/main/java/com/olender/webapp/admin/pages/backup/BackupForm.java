package com.olender.webapp.admin.pages.backup;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.service.IRunningService;
import pl.doa.wicket.form.CallServiceForm;
import pl.doa.wicket.ui.link.FormCallServiceLink;
import pl.doa.wicket.ui.widgets.InputReferenceField;

import com.olender.webapp.components.link.ValidatingCallServiceLink;

public class BackupForm extends CallServiceForm {

	public BackupForm(String id) {
		super(id, "/services/application/backup");
	}

	@Override
	protected void initForm() throws Exception {
		FormCallServiceLink link = new ValidatingCallServiceLink("link_backup") {

			@Override
			protected void onAfterRun(IRunningService runningService,
					IDocument input, AjaxRequestTarget target)
					throws GeneralDOAException {
				BackupForm.this.handleRunning(runningService, target);
			}
		};
		add(link);

		add(new InputReferenceField("backupFile", getModel(), "backupFile"));
	}

	protected void handleRunning(IRunningService running,
			AjaxRequestTarget target) {
	}
}

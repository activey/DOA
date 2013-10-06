package com.olender.webapp.admin.pages.backup;

import org.apache.wicket.model.IModel;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.resource.IStaticResource;
import pl.doa.service.IServiceDefinition;
import pl.doa.wicket.form.CallServiceForm;

import com.olender.webapp.components.link.ValidatingCallServiceLink;

public class RestoreForm extends CallServiceForm {

	private final IModel<IStaticResource> restoreFile;

	public RestoreForm(String id, IModel<IStaticResource> restoreFile) {
		super(id, "/services/application/restore");
		this.restoreFile = restoreFile;
	}

	@Override
	protected void initForm() throws Exception {
		add(new ValidatingCallServiceLink("link_restore_archive") {
			@Override
			protected void onBefeforeRun(IServiceDefinition service,
					IDocument input) throws GeneralDOAException {
				input.setFieldValue("backupFile", restoreFile.getObject());
			}
		});
	}

	@Override
	protected boolean isTransactional() {
		return true;
	}
}

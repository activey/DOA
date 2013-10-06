/**
 * 
 */
package com.olender.webapp.admin.pages.sections.gallery;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.document.IDocument;
import pl.doa.service.IRunningService;
import pl.doa.wicket.model.document.DocumentModel;
import pl.doa.wicket.ui.panel.EntityPanel;

/**
 * @author activey
 * 
 */
public class EditLinkPanel extends EntityPanel<IDocument> {

	private final static Logger log = LoggerFactory
			.getLogger(EditLinkPanel.class);

	public EditLinkPanel(String id, IModel<IDocument> entityModel) {
		super(id, entityModel);
	}

	public EditLinkPanel(String id, IDocument entity) {
		super(id, entity);
	}

	public EditLinkPanel(String id, String entityLocation) {
		super(id, entityLocation);
	}

	@Override
	protected void initEntityPanel() throws Exception {
		setOutputMarkupId(true);
		add(new EditLinkForm("form_link", new DocumentModel(getModelObject()
				.createCopy())) {
			@Override
			protected void handleRunning(IRunningService running,
					AjaxRequestTarget target) {
				EntityPanel<IDocument> panel =
						new ViewLinkPanel(EditLinkPanel.this.getId(),
								EditLinkPanel.this.getModel());
				EditLinkPanel.this.replaceWith(panel);
				target.add(panel);
			}

			@Override
			protected void onCancel(AjaxRequestTarget target) {
				EntityPanel<IDocument> panel =
						new ViewLinkPanel(EditLinkPanel.this.getId(),
								EditLinkPanel.this.getModel());
				EditLinkPanel.this.replaceWith(panel);
				target.add(panel);
			}
		});
	}
}

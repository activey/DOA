/**
 * 
 */
package com.olender.webapp.admin.pages.sections.contact;

import org.apache.wicket.model.IModel;

import pl.doa.document.IDocument;
import pl.doa.wicket.model.document.DocumentModel;
import pl.doa.wicket.ui.panel.EntityPanel;

/**
 * @author activey
 * 
 */
public class ContactSectionDataPanel extends EntityPanel<IDocument> {

	public ContactSectionDataPanel(String id, IModel<IDocument> entityModel) {
		super(id, entityModel);
		setOutputMarkupId(true);
	}

	@Override
	protected void initEntityPanel() throws Exception {
		add(new UpdateContactSectionForm("form_section_details",
				new DocumentModel(getModelObject(), true)));

	}
}

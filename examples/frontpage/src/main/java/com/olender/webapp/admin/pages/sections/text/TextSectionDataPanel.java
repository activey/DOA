/**
 * 
 */
package com.olender.webapp.admin.pages.sections.text;

import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.document.IDocument;
import pl.doa.wicket.model.document.DocumentModel;
import pl.doa.wicket.ui.panel.EntityPanel;

/**
 * @author activey
 * 
 */
public class TextSectionDataPanel extends EntityPanel<IDocument> {

	private final static Logger log = LoggerFactory
			.getLogger(TextSectionDataPanel.class);

	public TextSectionDataPanel(String id, IModel<IDocument> entityModel) {
		super(id, entityModel);
		setOutputMarkupId(true);
	}

	@Override
	protected void initEntityPanel() throws Exception {
		add(new UpdateTextSectionForm("form_section_details",
				new DocumentModel(getModelObject(), true)));
	}
}

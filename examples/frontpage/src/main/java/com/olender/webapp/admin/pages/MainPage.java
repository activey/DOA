/**
 * 
 */
package com.olender.webapp.admin.pages;

import pl.doa.document.IDocument;
import pl.doa.wicket.model.document.DocumentModel;

import com.olender.webapp.admin.BasePage;
import com.olender.webapp.admin.pages.main.UpdateMainDataForm;

/**
 * @author activey
 * 
 */
public class MainPage extends BasePage<IDocument> {

	public MainPage() {
		super("/documents/application/main");
	}

	@Override
	protected void initPage() throws Exception {
		add(new UpdateMainDataForm("form", new DocumentModel(getModelObject(),
				true)));
	}
}
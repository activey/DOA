package com.olender.webapp.admin.pages.sections;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.doa.document.IDocument;
import pl.doa.utils.PathIterator;
import pl.doa.wicket.decorator.impl.TabbedEntityDecorator;

import com.olender.webapp.admin.pages.SectionDetailsPage;

public class ContactSectionDetailsPage extends SectionDetailsPage {

	public ContactSectionDetailsPage(IDocument entity) {
		super(entity);
	}

	public ContactSectionDetailsPage(PageParameters parameters) {
		super(parameters);
	}

	public ContactSectionDetailsPage(PathIterator<String> entityPath) {
		super(entityPath);
	}

	public ContactSectionDetailsPage(String entityLocation) {
		super(entityLocation);
	}

	@Override
	protected TabbedEntityDecorator<IDocument> createDecorator() {
		return new ContactSectionDetailsDecorator();
	}

}

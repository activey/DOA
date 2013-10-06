package com.olender.webapp.admin.pages.sections;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.doa.document.IDocument;
import pl.doa.utils.PathIterator;
import pl.doa.wicket.decorator.impl.TabbedEntityDecorator;

import com.olender.webapp.admin.pages.SectionDetailsPage;

public class CustomersSectionDetailsPage extends SectionDetailsPage {

	public CustomersSectionDetailsPage(IDocument entity) {
		super(entity);
	}

	public CustomersSectionDetailsPage(PageParameters parameters) {
		super(parameters);
	}

	public CustomersSectionDetailsPage(PathIterator<String> entityPath) {
		super(entityPath);
	}

	public CustomersSectionDetailsPage(String entityLocation) {
		super(entityLocation);
	}

	@Override
	protected TabbedEntityDecorator<IDocument> createDecorator() {
		return new CustomersSectionDetailsDecorator();
	}

}

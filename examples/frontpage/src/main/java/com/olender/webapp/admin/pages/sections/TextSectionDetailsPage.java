package com.olender.webapp.admin.pages.sections;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.doa.document.IDocument;
import pl.doa.utils.PathIterator;
import pl.doa.wicket.decorator.impl.TabbedEntityDecorator;

import com.olender.webapp.admin.pages.SectionDetailsPage;

public class TextSectionDetailsPage extends SectionDetailsPage {

	public TextSectionDetailsPage(IDocument entity) {
		super(entity);
	}

	public TextSectionDetailsPage(PageParameters parameters) {
		super(parameters);
	}

	public TextSectionDetailsPage(PathIterator<String> entityPath) {
		super(entityPath);
	}

	public TextSectionDetailsPage(String entityLocation) {
		super(entityLocation);
	}

	@Override
	protected TabbedEntityDecorator<IDocument> createDecorator() {
		return new TextSectionDetailsDecorator();
	}

}

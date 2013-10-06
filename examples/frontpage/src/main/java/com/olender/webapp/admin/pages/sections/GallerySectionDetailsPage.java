package com.olender.webapp.admin.pages.sections;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.doa.document.IDocument;
import pl.doa.utils.PathIterator;
import pl.doa.wicket.decorator.impl.TabbedEntityDecorator;

import com.olender.webapp.admin.pages.SectionDetailsPage;

public class GallerySectionDetailsPage extends SectionDetailsPage {

	public GallerySectionDetailsPage(IDocument entity) {
		super(entity);
	}

	public GallerySectionDetailsPage(PageParameters parameters) {
		super(parameters);
	}

	public GallerySectionDetailsPage(PathIterator<String> entityPath) {
		super(entityPath);
	}

	public GallerySectionDetailsPage(String entityLocation) {
		super(entityLocation);
	}

	@Override
	protected TabbedEntityDecorator<IDocument> createDecorator() {
		return new GallerySectionDetailsDecorator();
	}

}

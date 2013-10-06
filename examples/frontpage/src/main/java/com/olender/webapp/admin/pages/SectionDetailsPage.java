/**
 * 
 */
package com.olender.webapp.admin.pages;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.doa.document.IDocument;
import pl.doa.utils.PathIterator;
import pl.doa.wicket.decorator.impl.TabbedEntityDecorator;
import pl.doa.wicket.form.UpdatableForm;
import pl.doa.wicket.model.document.field.DocumentFieldModel;

import com.olender.webapp.admin.BasePage;

/**
 * @author activey
 * 
 */
public abstract class SectionDetailsPage extends BasePage<IDocument> {

	public SectionDetailsPage(PageParameters parameters) {
		super(parameters);
	}

	public SectionDetailsPage(IDocument entity) {
		super(entity);
	}

	public SectionDetailsPage(PathIterator<String> entityPath) {
		super(entityPath);
	}

	public SectionDetailsPage(String entityLocation) {
		super(entityLocation);
	}

	protected abstract TabbedEntityDecorator<IDocument> createDecorator();

	@Override
	protected void initPage() throws Exception {
		final TabbedEntityDecorator<IDocument> decorator = createDecorator();
		add(new UpdatableForm<IDocument>("form_section_tabs") {

			@Override
			protected void initForm() throws Exception {
				add(new Label("section_name_header", new DocumentFieldModel(
						SectionDetailsPage.this.getModel(), "name")));

				add(decorator.decorate(SectionDetailsPage.this.getModel(),
						"tabs_section"));
			}
		});

	}
}
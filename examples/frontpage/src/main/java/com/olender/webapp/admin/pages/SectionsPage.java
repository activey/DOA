/**
 * 
 */
package com.olender.webapp.admin.pages;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.wicket.form.CallServiceForm;
import pl.doa.wicket.ui.link.EntityLink;

import com.olender.webapp.admin.BasePage;
import com.olender.webapp.admin.pages.sections.ContactSectionDetailsPage;
import com.olender.webapp.admin.pages.sections.CustomersSectionDetailsPage;
import com.olender.webapp.admin.pages.sections.GallerySectionDetailsPage;
import com.olender.webapp.admin.pages.sections.TextSectionDetailsPage;

/**
 * @author activey
 * 
 */
public class SectionsPage extends BasePage<IEntitiesContainer> {

	public SectionsPage() {
		super("/documents/application/sections");
	}

	@Override
	protected void initPage() throws Exception {
		WebMarkupContainer listTextContainer = new SectionsPanel(
				"sections_text_list", getModel(), "text") {

			protected EntityLink<IDocument> sectionLink(
					IModel<IDocument> sectionModel) {
				return new EntityLink<IDocument>("link_section_details",
						sectionModel, TextSectionDetailsPage.class);
			}

		};
		add(listTextContainer);

		WebMarkupContainer listGalleryContainer = new SectionsPanel(
				"sections_gallery_list", getModel(), "gallery") {

			@Override
			protected EntityLink<IDocument> sectionLink(
					IModel<IDocument> sectionModel) {
				return new EntityLink<IDocument>("link_section_details",
						sectionModel, GallerySectionDetailsPage.class);
			}

		};
		add(listGalleryContainer);

		WebMarkupContainer listCustomerContainer = new SectionsPanel(
				"sections_customers_list", getModel(), "customers") {

			@Override
			protected EntityLink<IDocument> sectionLink(
					IModel<IDocument> sectionModel) {
				return new EntityLink<IDocument>("link_section_details",
						sectionModel, CustomersSectionDetailsPage.class);
			}

		};
		add(listCustomerContainer);

		WebMarkupContainer listContactContainer = new SectionsPanel(
				"sections_contact_list", getModel(), "contact") {

			@Override
			protected EntityLink<IDocument> sectionLink(
					IModel<IDocument> sectionModel) {
				return new EntityLink<IDocument>("link_section_details",
						sectionModel, ContactSectionDetailsPage.class);
			}

		};
		add(listContactContainer);

		CallServiceForm createSectionForm = new CreateSectionForm(
				"form_section_create");
		add(createSectionForm);

	}

}
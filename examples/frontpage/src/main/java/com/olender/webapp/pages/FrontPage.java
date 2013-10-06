/**
 * 
 */
package com.olender.webapp.pages;

import java.text.MessageFormat;

import com.olender.webapp.behavior.LoadContentBehavior;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.resource.JQueryResourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.entity.sort.AbstractEntitiesSortComparator;
import pl.doa.entity.sort.IEntitiesSortComparator;
import pl.doa.resource.IStaticResource;
import pl.doa.wicket.WicketDOAApplication;
import pl.doa.wicket.model.EntityModel;
import pl.doa.wicket.model.container.ContainerEntitiesProvider;
import pl.doa.wicket.model.document.DocumentModel;
import pl.doa.wicket.model.document.field.DocumentFieldModel;
import pl.doa.wicket.ui.page.EntityPage;
import pl.doa.wicket.ui.panel.EntityMarkupContainer;
import pl.doa.wicket.ui.resource.StaticResourceReference;

/**
 * @author activey
 * 
 */
public class FrontPage extends EntityPage<IEntitiesContainer> {

	private final static Logger log = LoggerFactory.getLogger(FrontPage.class);

	public FrontPage() {
		super("/documents/application/sections");
	}

	public FrontPage(final PageParameters parameters) {
		super(parameters);
	}

	private class MediaLink extends EntityMarkupContainer<IDocument> {
		private final String fieldName;

		public MediaLink(String fieldName, IModel<IDocument> documentModel) {
			super(fieldName, documentModel);
			this.fieldName = fieldName;
		}

		@Override
		protected void initializeContainer() {
			add(new AttributeModifier("href", getModelObject()
					.getFieldValueAsString(fieldName)));
		}

		@Override
		public boolean isVisible() {
			return getModelObject().getField(fieldName) != null;
		}
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		response.render(JavaScriptHeaderItem
				.forReference(JQueryResourceReference.get()));
	}

	@Override
	protected void initEntityPage() throws Exception {
		IModel<IDocument> mainPageModel = new DocumentModel(
				"/documents/application/main");
		add(new MediaLink("twitterUrl", mainPageModel));
		add(new MediaLink("facebookUrl", mainPageModel));
		add(new MediaLink("googlePlusUrl", mainPageModel));

		add(new EntityMarkupContainer<IDocument>("logo", mainPageModel) {

			@Override
			protected void initializeContainer() {
				IStaticResource logoImage = (IStaticResource) getModelObject()
						.getFieldValue("logo");

				if (logoImage != null) {
					ResourceReference imagesResourceReference = new StaticResourceReference(
							logoImage);
					CharSequence urlForImage = getRequestCycle().urlFor(
							imagesResourceReference, null);
					add(new AttributeModifier("style", MessageFormat.format(
							"background-image:url({0});", urlForImage)));
				}
			}

		});

		final DataView<IDocument> backgrounds = new DataView<IDocument>(
				"placeholder_section_background",
				new ContainerEntitiesProvider<IDocument>(getModel(),
						new IEntityEvaluator() {

							public boolean isReturnableEntity(
									IEntity currentEntity) {
								if (!(currentEntity instanceof IDocument)) {
									return false;
								}
								IDocument sectionDoc = (IDocument) currentEntity;
								Boolean front = (Boolean) sectionDoc
										.getFieldValue("front", false);
								return front;
							}
						}, true)) {

			@Override
			protected void populateItem(final Item<IDocument> item) {
				IDocument sectionDocument = item.getModelObject();

				IStaticResource sectionBackground = (IStaticResource) sectionDocument
						.getFieldValue("backgroundImage");

				if (sectionBackground != null) {
					ResourceReference imagesResourceReference = new StaticResourceReference(
							sectionBackground);
					CharSequence urlForImage = getRequestCycle().urlFor(
							imagesResourceReference, null);
					item.add(new AttributeModifier(
							"style",
							MessageFormat
									.format("background:url({0}) center 0 no-repeat; background-size:cover;",
											urlForImage)));
					item.add(new AttributeModifier("id", ""
							+ sectionBackground.getId()));

				}
			}
		};
		add(backgrounds);

		final DataView<IDocument> sections = new DataView<IDocument>(
				"placeholder_section",
				new ContainerEntitiesProvider<IDocument>(getModel(),
						new IEntityEvaluator() {

							public boolean isReturnableEntity(
									IEntity currentEntity) {
								if (!(currentEntity instanceof IDocument)) {
									return false;
								}
								IDocument sectionDoc = (IDocument) currentEntity;
								Boolean front = (Boolean) sectionDoc
										.getFieldValue("front", false);
								return front;
							}
						}, true) {
					protected IEntitiesSortComparator getSortComparator() {
						return new AbstractEntitiesSortComparator<IDocument>() {

							@Override
							public boolean isBefore(IDocument entity1,
									IDocument entity2) {
								long priority1 = (Long) entity1.getFieldValue(
										"priority", 0L);
								long priority2 = (Long) entity2.getFieldValue(
										"priority", 0L);
								return priority1 > priority2;
							}
						};
					}
				}) {

			@Override
			protected void populateItem(final Item<IDocument> item) {
				IDocument sectionDocument = item.getModelObject();
				Label sectionLabel = new Label("link_section",
						new DocumentFieldModel(sectionDocument, "name"));
				if (sectionDocument != null) {
					sectionLabel.add(new AttributeModifier("href",
							"#!/section_"
									+ sectionDocument
											.getFieldValueAsString("href")));
					item.add(sectionLabel);
				}

				IStaticResource sectionBackground = (IStaticResource) sectionDocument
						.getFieldValue("backgroundImage");
				if (sectionBackground != null) {
					item.add(new AttributeModifier("data-type", "#"
							+ sectionBackground.getId()));
				}

				final DataView<IDocument> subsections = new LinkedEntitiesDataView(
						"placeholder_subsection",
						new EntityModel<IEntitiesContainer>(
								"/documents/application/links/subsections"),
						new EntityModel<IEntity>(sectionDocument), true);
				item.add(subsections);
			}
		};
		add(sections);

		final DataView<IDocument> sectionsPages = new DataView<IDocument>(
				"placeholder_section_body",
				new ContainerEntitiesProvider<IDocument>(getModel(),
						new IEntityEvaluator() {

							public boolean isReturnableEntity(
									IEntity currentEntity) {
								return currentEntity instanceof IDocument;
							}
						}, true)) {

			@Override
			protected void populateItem(final Item<IDocument> item) {
                IDocument sectionDocument = item.getModelObject();
				item.add(new AttributeModifier("id", "section_"
						+ sectionDocument.getFieldValueAsString("href")));

				Component decoratedSection;
				try {
					decoratedSection = WicketDOAApplication.get()
							.decorateEntity(item.getModel(),
									"placeholder_section");

                    item.add(decoratedSection);
                    item.add(new LoadContentBehavior(decoratedSection));
                } catch (Exception e) {
					log.error("", e);
					return;
				}
			}
		};
		add(sectionsPages);
	}
}
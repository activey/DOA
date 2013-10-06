/**
 * 
 */
package com.olender.webapp.decorators;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;

import pl.doa.document.IDocument;
import pl.doa.document.field.IListDocumentFieldValue;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.entity.sort.AbstractEntitiesSortComparator;
import pl.doa.entity.sort.IEntitiesSortComparator;
import pl.doa.resource.IStaticResource;
import pl.doa.wicket.model.container.ContainerEntitiesProvider;
import pl.doa.wicket.model.document.field.DocumentFieldModel;
import pl.doa.wicket.ui.panel.EntityPanel;
import pl.doa.wicket.ui.resource.StaticResourceReference;

/**
 * @author activey
 * 
 */
public class GallerySectionPanel extends EntityPanel<IDocument> {

	public GallerySectionPanel(String id, IDocument entity) {
		super(id, entity);
	}

	public GallerySectionPanel(String id, IModel<IDocument> entityModel) {
		super(id, entityModel);
	}

	public GallerySectionPanel(String id, String entityLocation) {
		super(id, entityLocation);
	}

	@Override
	protected void initEntityPanel() throws Exception {
		add(new Label("section_name",
				new DocumentFieldModel(getModel(), "name")));

		DataView<IDocument> galleryImages = new DataView<IDocument>(
				"gallery_image", new ContainerEntitiesProvider<IDocument>(
						"/documents/application/links/galleries",
						new IEntityEvaluator() {

							public boolean isReturnableEntity(
									IEntity currentEntity) {
								if (!(currentEntity instanceof IDocument)) {
									return false;
								}
								IDocument linkDoc = (IDocument) currentEntity;
								IDocument fromSection = (IDocument) linkDoc
										.getFieldValue("fromEntity");

								return fromSection.equals(getModelObject());
							}
						}) {
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
			protected void populateItem(Item<IDocument> item) {
				IDocument imageLink = item.getModelObject();

				IListDocumentFieldValue linked = (IListDocumentFieldValue) imageLink
						.getField("toEntities");
				IStaticResource smallImage = null;
				CharSequence urlForImage = null;
				if (linked != null) {
					IStaticResource bigImage = (IStaticResource) linked
							.getListField("bigImage").getFieldValue();
					if (bigImage != null) {
						urlForImage = getRequestCycle().urlFor(
								new StaticResourceReference(bigImage), null);
					}

					smallImage = (IStaticResource) linked.getListField(
							"smallImage").getFieldValue();
				}

				WebMarkupContainer linkContainer = new WebMarkupContainer(
						"resource");
				if (urlForImage != null) {
					linkContainer.add(new AttributeModifier("href", urlForImage
							.toString()));
				}
				linkContainer.add(new AttributeModifier("title", imageLink
						.getFieldValueAsString("description")));
				linkContainer.add(new AttributeModifier("data-gal", "gallery-"
						+ GallerySectionPanel.this.getModelObject().getId()));
				linkContainer.add(new Image("resource_thumbnail",
						new StaticResourceReference(smallImage))
						.add(new AttributeModifier("alt", imageLink
								.getFieldValueAsString("description"))));

				item.add(linkContainer);
			}
		};
		add(galleryImages);

	}
}

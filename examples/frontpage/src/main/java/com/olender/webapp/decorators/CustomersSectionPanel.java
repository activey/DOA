/**
 * 
 */
package com.olender.webapp.decorators;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;

import pl.doa.document.IDocument;
import pl.doa.document.field.IListDocumentFieldValue;
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
public class CustomersSectionPanel extends EntityPanel<IDocument> {

	public CustomersSectionPanel(String id, IDocument entity) {
		super(id, entity);
	}

	public CustomersSectionPanel(String id, IModel<IDocument> entityModel) {
		super(id, entityModel);
	}

	public CustomersSectionPanel(String id, String entityLocation) {
		super(id, entityLocation);
	}

	@Override
	protected void initEntityPanel() throws Exception {
		add(new Label("section_name",
				new DocumentFieldModel(getModel(), "name")));
		add(new Label("description", new DocumentFieldModel(getModel(),
				"description")).setEscapeModelStrings(false));

		DataView<IDocument> logos =
				new DataView<IDocument>("logo_container",
						new ContainerEntitiesProvider<IDocument>(
								"/documents/application/links/logos") {
							protected IEntitiesSortComparator getSortComparator() {
								return new AbstractEntitiesSortComparator<IDocument>() {

									@Override
									public boolean isBefore(IDocument entity1,
											IDocument entity2) {
										long priority1 =
												(Long) entity1.getFieldValue(
														"priority", 0L);
										long priority2 =
												(Long) entity2.getFieldValue(
														"priority", 0L);
										return priority1 > priority2;
									}
								};
							}
						}) {

					@Override
					protected void populateItem(Item<IDocument> item) {
						IDocument logoLink = item.getModelObject();
						IListDocumentFieldValue linked =
								(IListDocumentFieldValue) logoLink
										.getField("toEntities");
						IStaticResource logo =
								(IStaticResource) linked.getListField(
										"customerLogo").getFieldValue();

						item.add(new AttributeModifier("href", logoLink
								.getFieldValueAsString("customerUrl")));
						item.add(new Image("logo", new StaticResourceReference(
								logo)));
					}
				};
		add(logos);

	}
}

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
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.wicket.model.container.ContainerEntitiesProvider;
import pl.doa.wicket.model.document.field.DocumentFieldModel;
import pl.doa.wicket.model.document.field.DocumentReferenceFieldModel;
import pl.doa.wicket.ui.panel.EntityPanel;
import pl.doa.wicket.ui.resource.StaticResourceReference;

/**
 * @author activey
 * 
 */
public class TextSectionPanel extends EntityPanel<IDocument> {

	public TextSectionPanel(String id, IDocument entity) {
		super(id, entity);
	}

	public TextSectionPanel(String id, IModel<IDocument> entityModel) {
		super(id, entityModel);
	}

	public TextSectionPanel(String id, String entityLocation) {
		super(id, entityLocation);
	}

	@Override
	protected void initEntityPanel() throws Exception {
		add(new Label("section_name",
				new DocumentFieldModel(getModel(), "name")));

		add(new Label("section_header", new DocumentFieldModel(getModel(),
				"header")));
		add(new Label("section_description", new DocumentFieldModel(getModel(),
				"description")).setEscapeModelStrings(false));

		/*
		 * po dodaniu tego komponentu kazde przeladowanie strony powoduje
		 * zwiekszenie identyfikatora wersji strony (+2)
		 */
		add(new Image("section_image", new StaticResourceReference(
				new DocumentReferenceFieldModel(getModel(), "leftImage"))) {
			@Override
			public boolean isVisible() {
				IDocument sectionDoc = getModelObject();
				return sectionDoc.getFieldValue("leftImage") != null;
			}
		});

		//add(new Image("section_image", ""));

		final IDocument sectionDocument = getModelObject();

		DataView<IDocument> sectionLinks =
				new DataView<IDocument>("placeholder_section_link",
						new ContainerEntitiesProvider<IDocument>(
								"/documents/application/links/links",
								new IEntityEvaluator() {

									public boolean isReturnableEntity(
											IEntity currentEntity) {
										if (!(currentEntity instanceof IDocument)) {
											return false;
										}
										IDocument linkDoc =
												(IDocument) currentEntity;
										IDocument fromSection =
												(IDocument) linkDoc
														.getFieldValue("fromEntity");

										return fromSection
												.equals(sectionDocument);
									}
								}, false)) {

					@Override
					protected void populateItem(final Item<IDocument> item) {
						IDocument linkDoc = item.getModelObject();
						IListDocumentFieldValue entities =
								(IListDocumentFieldValue) linkDoc
										.getField("toEntities");
						IDocument toSection =
								(IDocument) entities.getListField("toSection")
										.getFieldValue();

						Label link =
								new Label("section_link",
										new DocumentFieldModel(toSection,
												"name"));
						if (toSection != null) {
							link.add(new AttributeModifier(
									"href",
									"#!/section_"
											+ toSection
													.getFieldValueAsString("href")));
						}
						item.add(link);
					}
				};
		add(sectionLinks);

	}
}

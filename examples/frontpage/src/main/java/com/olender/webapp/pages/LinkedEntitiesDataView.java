package com.olender.webapp.pages;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;

import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.field.IListDocumentFieldValue;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.entity.sort.AbstractEntitiesSortComparator;
import pl.doa.entity.sort.IEntitiesSortComparator;
import pl.doa.wicket.model.EntityModel;
import pl.doa.wicket.model.container.ContainerEntitiesProvider;
import pl.doa.wicket.model.document.field.DocumentFieldModel;

public class LinkedEntitiesDataView extends DataView<IDocument> {

	private final boolean goDeeper;
	private int subsectionsCount;
	private final IModel<IEntitiesContainer> linksContainer;

	protected LinkedEntitiesDataView(String id,
			IModel<IEntitiesContainer> linksContainer,
			IModel<IEntity> parentEntity, boolean goDeeper) {
		super(id, new ContainerEntitiesProvider<IDocument>(linksContainer,
				new LinkedEntitiesEvaluator(parentEntity)) {
			protected IEntitiesSortComparator<IDocument> getSortComparator() {
				return new AbstractEntitiesSortComparator<IDocument>() {

					@Override
					public boolean isBefore(IDocument entity1, IDocument entity2) {
						long priority1 =
								(Long) entity1.getFieldValue("priority", 0L);
						long priority2 =
								(Long) entity2.getFieldValue("priority", 0L);
						return priority1 > priority2;
					}
				};
			}
		});
		this.linksContainer = linksContainer;
		this.goDeeper = goDeeper;

		this.subsectionsCount =
				linksContainer.getObject().countEntities(
						new LinkedEntitiesEvaluator(parentEntity), true);
	}

	protected LinkedEntitiesDataView(String id,
			IModel<IEntitiesContainer> subsectionsContainer,
			IModel<IEntity> parentEntity) {
		this(id, subsectionsContainer, parentEntity, true);
	}

	@Override
	public boolean isVisible() {
		return subsectionsCount > 0;
	}

	@Override
	protected void populateItem(Item<IDocument> item) {
		IDocument sectionLinkDocument = item.getModelObject();
		IListDocumentFieldValue linkedEntities =
				(IListDocumentFieldValue) sectionLinkDocument
						.getField("toEntities");

		IDocument sectionDocument =
				(IDocument) linkedEntities.getListField("toSection")
						.getFieldValue();

		Label sectionLabel =
				new Label("link_subsection", new DocumentFieldModel(
						sectionDocument, "name"));
		if (sectionDocument != null) {
			sectionLabel.add(new AttributeModifier("href", "#!/section_"
					+ sectionDocument.getFieldValueAsString("href")));
		}
		item.add(sectionLabel);

		if (!goDeeper) {
			return;
		}
		final DataView<IDocument> subsections =
				new LinkedEntitiesDataView("placeholder_subsubsection",
						linksContainer, new EntityModel<IEntity>(
								sectionDocument), false);
		item.add(subsections);
	}

	private final static class LinkedEntitiesEvaluator implements
			IEntityEvaluator {
		private final IModel<IEntity> parentEntity;

		private LinkedEntitiesEvaluator(IModel<IEntity> parentEntity) {
			this.parentEntity = parentEntity;
		}

		public boolean isReturnableEntity(IEntity currentEntity) {
			if (!(currentEntity instanceof IDocument)) {
				return false;
			}
			IDocument linkDoc = (IDocument) currentEntity;
			IEntity fromEntity =
					(IDocument) linkDoc.getFieldValue("fromEntity");
			IEntity thisEntity = parentEntity.getObject();
			return thisEntity.equals(fromEntity);
		}
	}

}

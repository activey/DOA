package com.olender.webapp.admin.pages;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;

import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.entity.sort.AbstractEntitiesSortComparator;
import pl.doa.entity.sort.IEntitiesSortComparator;
import pl.doa.wicket.model.container.ContainerEntitiesProvider;
import pl.doa.wicket.model.document.field.DocumentFieldModel;
import pl.doa.wicket.ui.link.EntityLink;
import pl.doa.wicket.ui.panel.EntityPanel;

public abstract class SectionsPanel extends EntityPanel<IEntitiesContainer> {

	private final String sectionType;

	public SectionsPanel(String id, IModel<IEntitiesContainer> entityModel,
			String sectionType) {
		super(id, entityModel);
		this.sectionType = sectionType;
	}

	@Override
	protected void initEntityPanel() throws Exception {
		setOutputMarkupId(true);

		IEntitiesContainer sectionsContainer =
				(IEntitiesContainer) getModelObject().getEntityByName(
						sectionType);

		DataView<IDocument> textSections =
				new DataView<IDocument>("placeholder_section",
						new ContainerEntitiesProvider<IDocument>(
								sectionsContainer, new IEntityEvaluator() {

									public boolean isReturnableEntity(
											IEntity currentEntity) {
										return currentEntity instanceof IDocument;
									}
								}, false) {
							@Override
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
					protected void populateItem(final Item<IDocument> item) {
						final IDocument section = item.getModelObject();
						item.add(new Label("name", new DocumentFieldModel(
								section, "name")));
						item.add(new Label("priority", new DocumentFieldModel(
								section, "priority")));

						item.add(new PriorityChangeForm("form_priority", item
								.getModel()) {

							protected void priorityChanged(IDocument section,
									AjaxRequestTarget target) {
								target.add(getPage().get(
										SectionsPanel.this.getId()));
							}
						});

						item.add(new RemoveSectionForm("form_remove", item
								.getModel()) {
							protected void sectionRemoved(boolean removed,
									AjaxRequestTarget target) {
								target.add(getPage().get(
										SectionsPanel.this.getId()));
							}

							@Override
							protected EntityLink<IDocument> sectionLink(
									IModel<IDocument> sectionModel) {
								return SectionsPanel.this.sectionLink(sectionModel);
							};
						});

					}
				};
		add(textSections);
	}

	protected abstract EntityLink<IDocument> sectionLink(
			IModel<IDocument> sectionModel);

	protected void sectionRemoved(boolean removed, AjaxRequestTarget target) {
		target.add(getPage().get(SectionsPanel.this.getId()));
	}

}

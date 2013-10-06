/**
 * 
 */
package com.olender.webapp.admin.pages.sections.gallery;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.document.IDocument;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.entity.sort.AbstractEntitiesSortComparator;
import pl.doa.entity.sort.IEntitiesSortComparator;
import pl.doa.service.IRunningService;
import pl.doa.wicket.model.container.ContainerEntitiesProvider;
import pl.doa.wicket.ui.panel.EntityPanel;

/**
 * @author activey
 * 
 */
public class SectionImagesPanel extends EntityPanel<IDocument> {

	private final static Logger log = LoggerFactory
			.getLogger(SectionImagesPanel.class);

	public SectionImagesPanel(String id, IModel<IDocument> entityModel) {
		super(id, entityModel);
		setOutputMarkupId(true);
	}

	@Override
	protected void initEntityPanel() throws Exception {
		add(new AddImageForm("form_link", getModel()) {
			@Override
			protected void handleRunnning(IRunningService running,
					AjaxRequestTarget target) {
				target.add(SectionImagesPanel.this.get("container_images"));
			}
		});

		WebMarkupContainer listContainer =
				new WebMarkupContainer("container_images");
		listContainer.setOutputMarkupId(true);

		DataView<IDocument> images =
				new DataView<IDocument>("placeholder_image",
						new ContainerEntitiesProvider<IDocument>(
								"/documents/application/links/galleries",
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
										IDocument thisSection =
												SectionImagesPanel.this
														.getModelObject();
										return thisSection.equals(fromSection);
									}
								}, false) {
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
						item.add(new ViewLinkPanel("link_panel", item
								.getModel()) {
							@Override
							protected void linkRemoved(AjaxRequestTarget target) {
								target.add(SectionImagesPanel.this
										.get("container_images"));
							}

							protected void priorityChanged(IDocument section,
									AjaxRequestTarget target) {
								target.add(SectionImagesPanel.this
										.get("container_images"));
							};
						});

					}
				};
		listContainer.add(images);
		add(listContainer);

	}
}

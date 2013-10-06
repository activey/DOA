/**
 *
 */
package com.olender.webapp.admin.pages.sections.customers;

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
public class SectionCustomersPanel extends EntityPanel<IDocument> {

	private final static Logger log = LoggerFactory
			.getLogger(SectionCustomersPanel.class);

	public SectionCustomersPanel(String id, IModel<IDocument> entityModel) {
		super(id, entityModel);
		setOutputMarkupId(true);
	}

	@Override
	protected void initEntityPanel() throws Exception {
		add(new UpdateCustomersSectionForm("form_section_details", getModel()));

		add(new CreateCustomerForm("form_link", getModel()) {
			@Override
			protected void handleRunning(IRunningService running,
					AjaxRequestTarget target) {
				target.add(SectionCustomersPanel.this.get("container_images"));
			}
		});

		WebMarkupContainer listContainer =
				new WebMarkupContainer("container_images");
		listContainer.setOutputMarkupId(true);

		DataView<IDocument> images =
				new DataView<IDocument>("placeholder_image",
						new ContainerEntitiesProvider<IDocument>(
								"/documents/application/links/logos",
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
												SectionCustomersPanel.this
														.getModelObject();
										return thisSection.equals(fromSection);
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
					protected void populateItem(Item<IDocument> item) {
						item.add(new ViewLinkPanel("link_panel", item
								.getModel()));

					}
				};
		listContainer.add(images);
		add(listContainer);

	}
}

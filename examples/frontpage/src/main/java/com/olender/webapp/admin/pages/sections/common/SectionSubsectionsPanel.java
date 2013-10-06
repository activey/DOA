/**
 * 
 */
package com.olender.webapp.admin.pages.sections.common;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.IAjaxCallListener;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.document.field.IListDocumentFieldValue;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.entity.sort.AbstractEntitiesSortComparator;
import pl.doa.entity.sort.IEntitiesSortComparator;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.wicket.form.CallServiceForm;
import pl.doa.wicket.model.container.ContainerEntitiesProvider;
import pl.doa.wicket.model.document.field.DocumentFieldModel;
import pl.doa.wicket.ui.link.EntityAjaxLink;
import pl.doa.wicket.ui.panel.EntityPanel;

import com.olender.webapp.admin.pages.PriorityChangeForm;
import com.olender.webapp.components.ajax.ConfirmListener;
import com.olender.webapp.components.ajax.FormCallListener;
import com.olender.webapp.components.link.ValidatingCallServiceLink;

/**
 * @author activey
 * 
 */
public class SectionSubsectionsPanel extends EntityPanel<IDocument> {

	public SectionSubsectionsPanel(String id, IModel<IDocument> entityModel) {
		super(id, entityModel);
		setOutputMarkupId(true);
	}

	@Override
	protected void initEntityPanel() throws Exception {
		WebMarkupContainer linkedContainer = new WebMarkupContainer(
				"linked_sections_list");
		linkedContainer.setOutputMarkupId(true);

		DataView<IDocument> subsections = new DataView<IDocument>(
				"placeholder_subsection",
				new ContainerEntitiesProvider<IDocument>(
						"/documents/application/links/subsections",
						new IEntityEvaluator() {

							public boolean isReturnableEntity(
									IEntity currentEntity) {
								if (!(currentEntity instanceof IDocument)) {
									return false;
								}
								IDocument linkDoc = (IDocument) currentEntity;
								IDocument fromSection = (IDocument) linkDoc
										.getFieldValue("fromEntity");
								IDocument thisSection = getModelObject();
								return thisSection.equals(fromSection);
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
				final IDocument sectionLink = item.getModelObject();
				IListDocumentFieldValue linkedEntities = (IListDocumentFieldValue) sectionLink
						.getField("toEntities");

				IDocument toSection = (IDocument) linkedEntities.getListField(
						"toSection").getFieldValue();

				item.add(new Label("name", new DocumentFieldModel(toSection,
						"name")));

				item.add(new EntityAjaxLink<IDocument>(
						"link_subsection_removelink", item.getModel(), true) {

					@Override
					public void onClick(IModel<IDocument> doc,
							AjaxRequestTarget target) {
						final IDocument linkDoc = doc.getObject();
						linkDoc.remove();

						target.add(SectionSubsectionsPanel.this
								.get("sections_list"));
						target.add(SectionSubsectionsPanel.this
								.get("linked_sections_list"));
					}

					@Override
					protected void populateListeners(
							List<IAjaxCallListener> listeners) {
						listeners.add(new ConfirmListener());
						listeners.add(new FormCallListener());
					}

				});

				item.add(new Label("priority", new DocumentFieldModel(item
						.getModel(), "priority")));

				item.add(new PriorityChangeForm("form_priority", item
						.getModel()) {

					protected void priorityChanged(IDocument section,
							AjaxRequestTarget target) {
						target.add(SectionSubsectionsPanel.this);
					}
				});

			}
		};
		linkedContainer.add(subsections);
		add(linkedContainer);

		WebMarkupContainer sectionsContainer = new WebMarkupContainer(
				"sections_list");
		sectionsContainer.setOutputMarkupId(true);
		final CallServiceForm linkSectionForm = new CallServiceForm(
				"sections_form", "/services/application/section_link") {

			@Override
			protected void initForm() throws Exception {
				DataView<IDocument> sections = new DataView<IDocument>(
						"placeholder_section",
						new ContainerEntitiesProvider<IDocument>(
								"/documents/application/sections",
								new IEntityEvaluator() {

									public boolean isReturnableEntity(
											IEntity currentEntity) {
										if (!(currentEntity instanceof IDocument)) {
											return false;
										}
										IDocument sectionDoc = (IDocument) currentEntity;
										IDocument thisSection = SectionSubsectionsPanel.this
												.getModelObject();
										return !sectionDoc.equals(thisSection);
									}
								}, true)) {

					@Override
					protected void populateItem(Item<IDocument> item) {
						final IDocument section = item.getModelObject();
						item.add(new Label("name", new DocumentFieldModel(
								section, "name")));

						item.add(new ValidatingCallServiceLink(
								"link_section_addsubsection") {

							protected void onBefeforeRun(
									IServiceDefinition service, IDocument input)
									throws GeneralDOAException {
								input.setFieldValue("linkType", "subsection");
								input.setFieldValue("fromEntity",
										SectionSubsectionsPanel.this
												.getModelObject());

								IListDocumentFieldValue entities = (IListDocumentFieldValue) input
										.getField("toEntities", true);
								entities.addReferenceField("toSection", section);
							}

							@Override
							protected void onAfterRun(
									IRunningService runningService,
									IDocument input, AjaxRequestTarget target)
									throws GeneralDOAException {
								target.add(SectionSubsectionsPanel.this
										.get("sections_list"));
								target.add(SectionSubsectionsPanel.this
										.get("linked_sections_list"));
							}

						});

					}
				};
				add(sections);
			}
		};

		sectionsContainer.add(linkSectionForm);
		add(sectionsContainer);
	}
}

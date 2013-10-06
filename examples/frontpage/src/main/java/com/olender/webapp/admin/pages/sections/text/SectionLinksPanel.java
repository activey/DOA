/**
 * 
 */
package com.olender.webapp.admin.pages.sections.text;

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
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.wicket.form.CallServiceForm;
import pl.doa.wicket.model.container.ContainerEntitiesProvider;
import pl.doa.wicket.model.document.field.DocumentFieldModel;
import pl.doa.wicket.ui.link.EntityAjaxLink;
import pl.doa.wicket.ui.panel.EntityPanel;

import com.olender.webapp.components.ajax.ConfirmListener;
import com.olender.webapp.components.ajax.FormCallListener;
import com.olender.webapp.components.link.ValidatingCallServiceLink;

/**
 * @author activey
 * 
 */
public class SectionLinksPanel extends EntityPanel<IDocument> {

	public SectionLinksPanel(String id, IModel<IDocument> entityModel) {
		super(id, entityModel);
		setOutputMarkupId(true);
	}

	@Override
	protected void initEntityPanel() throws Exception {
		WebMarkupContainer linkedContainer = new WebMarkupContainer(
				"linked_sections_list");
		linkedContainer.setOutputMarkupId(true);

		DataView<IDocument> linkedSections = new DataView<IDocument>(
				"placeholder_section",
				new ContainerEntitiesProvider<IDocument>(
						"/documents/application/links/links",
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
						})) {

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
						"link_section_removelink", item.getModel(), true) {

					@Override
					public void onClick(IModel<IDocument> docModel,
							AjaxRequestTarget target) {
						final IDocument linkDoc = docModel.getObject();
						linkDoc.remove();
						target.add(SectionLinksPanel.this.get("sections_list"));
						target.add(SectionLinksPanel.this
								.get("linked_sections_list"));
					}

					@Override
					protected void populateListeners(
							List<IAjaxCallListener> listeners) {
						listeners.add(new ConfirmListener());
						listeners.add(new FormCallListener());
					}

				});

			}
		};

		linkedContainer.add(linkedSections);
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
										IDocument thisSection = SectionLinksPanel.this
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
								"link_section_addlink") {

							protected void onBefeforeRun(
									IServiceDefinition service, IDocument input)
									throws GeneralDOAException {
								input.setFieldValue("linkType", "link");
								input.setFieldValue("fromEntity",
										SectionLinksPanel.this.getModelObject());

								IListDocumentFieldValue linkedEntities = (IListDocumentFieldValue) input
										.getField("toEntities", true);

								linkedEntities.addReferenceField("toSection",
										section);
							}

							@Override
							protected void onAfterRun(
									IRunningService runningService,
									IDocument input, AjaxRequestTarget target)
									throws GeneralDOAException {
								target.add(SectionLinksPanel.this
										.get("sections_list"));
								target.add(SectionLinksPanel.this
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

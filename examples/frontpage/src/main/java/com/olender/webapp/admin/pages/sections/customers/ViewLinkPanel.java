/**
 * 
 */
package com.olender.webapp.admin.pages.sections.customers;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.IAjaxCallListener;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.document.field.IListDocumentFieldValue;
import pl.doa.resource.IStaticResource;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.wicket.form.CallServiceForm;
import pl.doa.wicket.model.EntityModel;
import pl.doa.wicket.model.document.DocumentModel;
import pl.doa.wicket.model.document.field.DocumentFieldModel;
import pl.doa.wicket.ui.link.EntityAjaxLink;
import pl.doa.wicket.ui.panel.EntityPanel;
import pl.doa.wicket.ui.resource.StaticResourceReference;

import com.olender.webapp.components.ajax.ConfirmListener;
import com.olender.webapp.components.ajax.FormCallListener;
import com.olender.webapp.components.link.ValidatingCallServiceLink;

/**
 * @author activey
 * 
 */
public class ViewLinkPanel extends EntityPanel<IDocument> {

	public ViewLinkPanel(String id, IModel<IDocument> entityModel) {
		super(id, entityModel);
		setOutputMarkupId(true);
	}

	public ViewLinkPanel(String id, IDocument entity) {
		super(id, entity);
		setOutputMarkupId(true);
	}

	public ViewLinkPanel(String id, String entityLocation) {
		super(id, entityLocation);
		setOutputMarkupId(true);
	}

	@Override
	protected void initEntityPanel() throws Exception {
		final IDocument link = getModelObject();

		IListDocumentFieldValue linked = (IListDocumentFieldValue) link
				.getField("toEntities", false);
		final IStaticResource res = (IStaticResource) linked.getListField(
				"customerLogo").getFieldValue();

		WebMarkupContainer linkContainer = new WebMarkupContainer("site_url");
		linkContainer.add(new AttributeModifier("href", link
				.getFieldValueAsString("customerUrl")));
		linkContainer.add(new Image("resource_thumbnail",
				new StaticResourceReference(res)));
		add(linkContainer);

		add(new Label("resource_name", new AbstractReadOnlyModel<String>() {

			@Override
			public String getObject() {
				return (res == null) ? "" : res.getName();
			}
		}));

		add(new Label("priority", new DocumentFieldModel(link, "priority")));

		add(new CallServiceForm("form_priority",
				new EntityModel<IServiceDefinition>(
						"/services/application/priority_change")) {
			@Override
			protected void initForm() throws Exception {

				add(new ValidatingCallServiceLink("link_priority_up") {
					@Override
					protected void onBefeforeRun(IServiceDefinition service,
							IDocument input) throws GeneralDOAException {
						input.setFieldValue("section", link);
						input.setFieldValue("delta", 1L);
					}

					@Override
					protected void onAfterRun(IRunningService runningService,
							IDocument input, AjaxRequestTarget target)
							throws GeneralDOAException {
						target.add(findParent(SectionCustomersPanel.class));
					}

				});

				add(new ValidatingCallServiceLink("link_priority_down") {
					@Override
					protected void onBefeforeRun(IServiceDefinition service,
							IDocument input) throws GeneralDOAException {
						input.setFieldValue("section", link);
						input.setFieldValue("delta", -1L);
					}

					@Override
					protected void onAfterRun(IRunningService runningService,
							IDocument input, AjaxRequestTarget target)
							throws GeneralDOAException {
						target.add(findParent(SectionCustomersPanel.class));
					}

				});

				add(new EntityAjaxLink<IDocument>("edit_link",
						new DocumentModel(link)) {

					@Override
					public void onClick(IModel<IDocument> doc,
							AjaxRequestTarget target) {
						EntityPanel<IDocument> panel = new EditLinkPanel(
								ViewLinkPanel.this.getId(), doc);
						ViewLinkPanel.this.replaceWith(panel);
						target.add(panel);
					}
				});

				EntityAjaxLink<IDocument> removeLink = new EntityAjaxLink<IDocument>(
						"remove_link", new DocumentModel(link), true) {

					@Override
					public void onClick(IModel<IDocument> docModel,
							AjaxRequestTarget target) {
						IDocument doc = docModel.getObject();
						doc.remove();
						target.add(findParent(SectionCustomersPanel.class));
					}

					@Override
					protected void populateListeners(
							List<IAjaxCallListener> listeners) {
						listeners.add(new ConfirmListener());
						listeners.add(new FormCallListener());
					}

				};
				add(removeLink);
			}
		});

	}
}

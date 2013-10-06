/**
 * 
 */
package com.olender.webapp.admin.pages.sections.gallery;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.IAjaxCallListener;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import pl.doa.document.IDocument;
import pl.doa.document.field.IListDocumentFieldValue;
import pl.doa.resource.IStaticResource;
import pl.doa.wicket.model.document.field.DocumentFieldModel;
import pl.doa.wicket.ui.link.EntityAjaxLink;
import pl.doa.wicket.ui.panel.EntityPanel;
import pl.doa.wicket.ui.resource.StaticResourceReference;

import com.olender.webapp.admin.pages.PriorityChangeForm;
import com.olender.webapp.components.ajax.ConfirmListener;
import com.olender.webapp.components.ajax.FormCallListener;

/**
 * @author activey
 * 
 */
public class ViewLinkPanel extends EntityPanel<IDocument> {

	public ViewLinkPanel(String id, IModel<IDocument> entityModel) {
		super(id, entityModel);
	}

	public ViewLinkPanel(String id, IDocument entity) {
		super(id, entity);
	}

	public ViewLinkPanel(String id, String entityLocation) {
		super(id, entityLocation);
	}

	@Override
	protected void initEntityPanel() throws Exception {
		setOutputMarkupId(true);
		IDocument link = getModelObject();

		IListDocumentFieldValue linked = (IListDocumentFieldValue) link
				.getField("toEntities", false);
		final IStaticResource res = (linked == null) ? null
				: (IStaticResource) linked.getListField("smallImage")
						.getFieldValue();
		add(new Label("resource_name", new AbstractReadOnlyModel<String>() {

			@Override
			public String getObject() {
				return (res == null) ? "" : res.getName();
			}
		}));
		add(new Image("resource_thumbnail", new StaticResourceReference(res)));

		add(new EntityAjaxLink<IDocument>("edit_link", getModel()) {

			@Override
			public void onClick(IModel<IDocument> docModel,
					AjaxRequestTarget target) {
				EntityPanel<IDocument> panel = new EditLinkPanel(
						ViewLinkPanel.this.getId(), docModel);
				ViewLinkPanel.this.replaceWith(panel);
				target.add(panel);
			}
		});

		add(new EntityAjaxLink<IDocument>("remove_link", getModel(), true) {

			@Override
			public void onClick(IModel<IDocument> docModel,
					AjaxRequestTarget target) {
				IDocument doc = docModel.getObject();
				doc.remove();
				ViewLinkPanel.this.linkRemoved(target);
			}

			@Override
			protected void populateListeners(List<IAjaxCallListener> listeners) {
				listeners.add(new ConfirmListener());
				listeners.add(new FormCallListener());
			}

		});

		add(new Label("priority",
				new DocumentFieldModel(getModel(), "priority")));
		add(new PriorityChangeForm("form_priority", getModel()) {

			protected void priorityChanged(IDocument section,
					AjaxRequestTarget target) {
				ViewLinkPanel.this.priorityChanged(section, target);
			}
		});
	}

	protected void priorityChanged(IDocument section, AjaxRequestTarget target) {
	}

	protected void linkRemoved(AjaxRequestTarget target) {

	}

}

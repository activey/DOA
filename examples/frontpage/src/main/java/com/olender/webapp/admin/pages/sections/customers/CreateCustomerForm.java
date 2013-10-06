/**
 * 
 */
package com.olender.webapp.admin.pages.sections.customers;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.resource.IStaticResource;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.wicket.form.CallServiceForm;
import pl.doa.wicket.model.EntityModel;
import pl.doa.wicket.model.document.field.DocumentListFieldModel;
import pl.doa.wicket.ui.widgets.DocumentField;
import pl.doa.wicket.ui.widgets.DocumentReferenceField;
import pl.doa.wicket.ui.window.ReturnableEntityWindow;

import com.olender.webapp.components.form.ImageReferenceField;
import com.olender.webapp.components.link.ValidatingCallServiceLink;

/**
 * @author activey
 * 
 */
public class CreateCustomerForm extends CallServiceForm {

	private final IModel<IDocument> customerSection;

	public CreateCustomerForm(String id, IModel<IDocument> customerSection) {
		super(id, "/services/application/customer_logo_link");
		this.customerSection = customerSection;
	}

	@Override
	protected final void initForm() throws Exception {
		DocumentReferenceField<IStaticResource> logoField = new ImageReferenceField(
				"customerLogo", new DocumentListFieldModel(getModel(),
						"toEntities", "customerLogo", true,
						DocumentFieldDataType.reference));
		ReturnableEntityWindow<IEntitiesContainer, IStaticResource> selectLogoWindow = logoField
				.createPopupWindow("placeholder_popup",
						new EntityModel<IEntitiesContainer>(
								"/images/application"), new Model<String>(
								"Wybierz logo"));
		add(selectLogoWindow);
		add(selectLogoWindow.showWindowLink("link_popup_show"));
		add(logoField.setOutputMarkupId(true));

		add(new DocumentField("customerUrl", getModel(), "customerUrl"));

		add(new ValidatingCallServiceLink("create_image_link") {

			protected void onBefeforeRun(IServiceDefinition service,
					IDocument input) throws GeneralDOAException {
				input.setFieldValue("fromEntity", customerSection.getObject());
			}
			
			@Override
			protected void onAfterRun(IRunningService runningService,
					IDocument input, AjaxRequestTarget target)
					throws GeneralDOAException {
				CreateCustomerForm.this.handleRunning(runningService, target);
			}

		});
	}

	protected void handleRunning(IRunningService running,
			AjaxRequestTarget target) {
	}
}

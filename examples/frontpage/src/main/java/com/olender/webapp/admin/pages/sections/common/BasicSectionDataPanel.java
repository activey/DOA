/**
 * 
 */
package com.olender.webapp.admin.pages.sections.common;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.resource.IStaticResource;
import pl.doa.service.IServiceDefinition;
import pl.doa.wicket.form.CallServiceForm;
import pl.doa.wicket.model.EntityModel;
import pl.doa.wicket.model.document.DocumentModel;
import pl.doa.wicket.model.document.field.DocumentFieldModel;
import pl.doa.wicket.ui.panel.EntityPanel;
import pl.doa.wicket.ui.widgets.DocumentCheckbox;
import pl.doa.wicket.ui.widgets.DocumentField;
import pl.doa.wicket.ui.widgets.DocumentReferenceField;
import pl.doa.wicket.ui.window.ReturnableEntityWindow;

import com.olender.webapp.components.form.ImageReferenceField;
import com.olender.webapp.components.link.ValidatingCallServiceLink;

/**
 * @author activey
 * 
 */
public class BasicSectionDataPanel extends EntityPanel<IDocument> {

	private final class BasicDataForm extends CallServiceForm {
		private BasicDataForm(String id, IModel<IDocument> exisingData) {
			super(id, new EntityModel<IServiceDefinition>(
					"/services/application/section_update"), exisingData);
		}

		@Override
		protected void initForm() throws Exception {
			add(new DocumentField("section_name", getModel(), "name"));
			add(new DocumentField("section_href", getModel(), "href"));
			add(new DocumentCheckbox("section_front", getModel(), "front"));

			DocumentReferenceField<IStaticResource> backgroundImageField = new ImageReferenceField(
					"section_background", new DocumentFieldModel(getModel(),
							"backgroundImage", true));
			ReturnableEntityWindow<IEntitiesContainer, IStaticResource> selectWindow = backgroundImageField
					.createPopupWindow("placeholder_popup",
							new EntityModel<IEntitiesContainer>(
									"/images/application"), new Model<String>(
									"Wybierz tło sekcji"));
			add(selectWindow);
			add(selectWindow.showWindowLink("link_popup_show"));
			add(backgroundImageField.setOutputMarkupId(true));

			add(new ValidatingCallServiceLink("call_service"));

		}
		
		@Override
		protected boolean isTransactional() {
			return true;
		}
	}

	public BasicSectionDataPanel(String id, IModel<IDocument> entityModel) {
		super(id, entityModel);
		setOutputMarkupId(true);
	}

	@Override
	protected void initEntityPanel() throws Exception {
		IDocument input = getModelObject();
		try {
			input = input.createCopy();
		} catch (Exception e) {
			//
		}

		add(new BasicDataForm("form_section_details", new DocumentModel(input)));
	}
}

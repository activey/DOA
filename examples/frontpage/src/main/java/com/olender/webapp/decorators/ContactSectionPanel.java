/**
 * 
 */
package com.olender.webapp.decorators;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import pl.doa.document.IDocument;
import pl.doa.wicket.model.document.field.DocumentFieldModel;
import pl.doa.wicket.ui.panel.EntityPanel;

import com.olender.webapp.behavior.impl.gmaps.GmapBehavior;
import com.olender.webapp.behavior.impl.gmaps.GmapMarker;

/**
 * @author activey
 * 
 */
public class ContactSectionPanel extends EntityPanel<IDocument> {

	public ContactSectionPanel(String id, IDocument entity) {
		super(id, entity);
	}

	public ContactSectionPanel(String id, IModel<IDocument> entityModel) {
		super(id, entityModel);
	}

	public ContactSectionPanel(String id, String entityLocation) {
		super(id, entityLocation);
	}

	@Override
	protected void initEntityPanel() throws Exception {
		add(new Label("section_name",
				new DocumentFieldModel(getModel(), "name")));

		add(new Label("addr1", new DocumentFieldModel(getModel(), "addr1")));
		add(new Label("addr2", new DocumentFieldModel(getModel(), "addr2")));
		add(new Label("addr3", new DocumentFieldModel(getModel(), "addr3")));
		add(new Label("phone1", new DocumentFieldModel(getModel(), "phone1")));
		add(new Label("phone2", new DocumentFieldModel(getModel(), "phone2")));
		Label email =
				new Label("email", new DocumentFieldModel(getModel(), "email"));
		email.add(new AttributeModifier("href", "mailto:"
				+ getModelObject().getFieldValueAsString("email")));

		WebMarkupContainer mapHolder = new WebMarkupContainer("map_holder");

		Double latitude =
				(Double) getModelObject().getFieldValue("latitude",
						new Double(0));
		Double longitude =
				(Double) getModelObject().getFieldValue("longitude",
						new Double(0));
		String description =
				getModelObject().getFieldValueAsString("description");
		GmapBehavior gmap =
				new GmapBehavior(new AbstractReadOnlyModel<String>() {

					public String getObject() {
						return getModelObject().getFieldValueAsString("apiKey");
					}
				}).setControls(true).setZoom(15);
		gmap.getMarkers().add(
				new GmapMarker(latitude, longitude, description)
						.setPopup(false));

		mapHolder.add(gmap);
		add(mapHolder);

		add(email);
	}
}

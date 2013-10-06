/**
 * 
 */
package com.olender.webapp.behavior.impl.gmaps;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.resource.JQueryResourceReference;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.doa.resource.IStaticResource;
import pl.doa.wicket.model.EntityModel;
import pl.doa.wicket.ui.resource.StaticResourceReference;

import com.olender.webapp.behavior.JQueryBehavior;

/**
 * @author activey
 * 
 */
public class GmapBehavior extends JQueryBehavior {

	private boolean controls;
	private int zoom;
	private final List<GmapMarker> markers;
	private final IModel<String> apiKeyModel;

	public GmapBehavior(String apiKey) {
		this(new Model<String>(apiKey));
	}

	public GmapBehavior(IModel<String> apiKeyModel) {
		super("gMap");
		this.apiKeyModel = apiKeyModel;
		this.markers = new ArrayList<GmapMarker>();
	}

	protected JSONObject getConfiguration() throws JSONException {
		JSONObject config = new JSONObject();

		// tworzenie markerow
		boolean markerSet = false;
		JSONArray markersArray = new JSONArray();
		for (GmapMarker marker : markers) {
			JSONObject markerJson = new JSONObject();
			markerJson.put("latitude", marker.getLatitude());
			markerJson.put("longitude", marker.getLongitude());
			markerJson.put("html", marker.getHtml());
			markerJson.put("popup", marker.isPopup());
			markersArray.put(markerJson);
			if (!markerSet) {
				config.put("latitude", marker.getLatitude());
				config.put("longitude", marker.getLongitude());
				markerSet = true;
			}
		}

		config.put("markers", markersArray);
		config.put("controls", isControls());
		config.put("zoom", zoom);
		return config;
	}

	@Override
	protected void renderResources(IHeaderResponse resources) {
		resources.render(JavaScriptHeaderItem
				.forUrl("http://maps.google.com/maps?file=api&v=2&key="
						+ apiKeyModel.getObject()));
		resources.render(new JavaScriptReferenceHeaderItem(
				new StaticResourceReference(new EntityModel<IStaticResource>(
						"/common/js/jquery.gmap-1.1.0-min.js")), null, "gmap",
				false, "UTF-8", null) {
			@Override
			public Iterable<? extends HeaderItem> getDependencies() {
				List<HeaderItem> deps = new ArrayList<HeaderItem>();
				deps.add(JavaScriptHeaderItem
						.forReference(JQueryResourceReference.get()));
				return deps;
			}
		});
	}

	public boolean isControls() {
		return controls;
	}

	public List<GmapMarker> getMarkers() {
		return markers;
	}

	public GmapBehavior setControls(boolean controls) {
		this.controls = controls;
		return this;
	}

	public int getZoom() {
		return zoom;
	}

	public GmapBehavior setZoom(int zoom) {
		this.zoom = zoom;
		return this;
	}

}

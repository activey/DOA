/**
 * 
 */
package com.olender.webapp.behavior.impl.gmaps;

import java.io.Serializable;

/**
 * @author activey
 * 
 */
public class GmapMarker implements Serializable {

	private static final long serialVersionUID = -1410177307224836557L;

	private double latitude;

	private double longitude;

	private String html;

	private boolean popup;

	public GmapMarker(double latitude, double longitude, String html,
			boolean popup) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.html = html;
		this.popup = popup;

	}

	public GmapMarker(double latitude, double longitude, String html) {
		this(latitude, longitude, html, true);
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public boolean isPopup() {
		return popup;
	}

	public GmapMarker setPopup(boolean popup) {
		this.popup = popup;
		return this;
	}

}

/**
 * 
 */
package com.olender.webapp.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author activey
 * 
 */
public class JQueryBehavior extends Behavior {

	private final static Logger log = LoggerFactory
			.getLogger(JQueryBehavior.class);

	private final String modifierFunction;

	private final String jqueryLocatorSuffix;

	public JQueryBehavior(String modifierFunction) {
		this(modifierFunction, null);
	}

	public JQueryBehavior(String modifierFunction, String jqueryLocatorSuffix) {
		this.modifierFunction = modifierFunction;
		this.jqueryLocatorSuffix = jqueryLocatorSuffix;
	}

	protected JSONObject getConfiguration() throws JSONException {
		return new JSONObject();
	}

	private final String readConfiguration() {
		try {
			return getConfiguration().toString();
		} catch (Exception e) {
			log.error("", e);
			return "";
		}
	}

	protected void renderResources(IHeaderResponse resources) {

	}

	@Override
	public void beforeRender(Component component) {
		component.setOutputMarkupId(true);
	}

	@Override
	public final void renderHead(Component component, IHeaderResponse response) {
		renderResources(response);

		response.render(OnDomReadyHeaderItem.forScript("$('#"
				+ component.getMarkupId()
				+ ((jqueryLocatorSuffix == null) ? "" : jqueryLocatorSuffix)
				+ "')." + modifierFunction + "(" + readConfiguration() + ");"));
	}
}

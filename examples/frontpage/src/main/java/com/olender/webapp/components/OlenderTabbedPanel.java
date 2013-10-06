/**
 * 
 */
package com.olender.webapp.components;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;

import pl.doa.wicket.ui.tabs.TabbedPanel;

import com.olender.webapp.components.ajax.FormCallListener;

/**
 * @author activey
 * 
 */
public class OlenderTabbedPanel extends TabbedPanel {

	public OlenderTabbedPanel(String id, List<ITab> tabs) {
		super(id, tabs);
	}

	@Override
	protected String getTabContainerCssClass() {
		return "nav nav-tabs";
	}

	protected String getSelectedTabCssClass() {
		return "active";
	}

	@Override
	protected WebMarkupContainer newLink(String linkId, final int index) {
		return new AjaxFallbackLink<Void>(linkId) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				setSelectedTab(index);
				if (target != null) {
					target.add(OlenderTabbedPanel.this);
				}
				onAjaxUpdate(target);
			}

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				attributes.getAjaxCallListeners().add(new FormCallListener());
			}
		};
	}
}

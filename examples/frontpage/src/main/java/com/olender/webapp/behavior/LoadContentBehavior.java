package com.olender.webapp.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

/**
 * Created with IntelliJ IDEA.
 * User: activey
 * Date: 07.07.13
 * Time: 15:01
 * To change this template use File | Settings | File Templates.
 */
public class LoadContentBehavior extends AbstractDefaultAjaxBehavior {

    private final Component contentComponent;

    public LoadContentBehavior(Component contentComponent) {
        this.contentComponent = contentComponent;
    }

    @Override
    public void onConfigure(Component component) {
        contentComponent.setOutputMarkupPlaceholderTag(true);
        contentComponent.setVisible(false);
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        tag.getAttributes().put("data-wicket-url", getCallbackUrl());
    }

    @Override
    protected void respond(AjaxRequestTarget target) {
        contentComponent.setVisible(true);
        target.add(contentComponent);
    }
}

package com.olender.webapp.components.ajax;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.attributes.IAjaxCallListener;

public class ConfirmListener implements IAjaxCallListener {

	private static final String DEFAULT_MESSAGE = "Czy jesteś pewien?";

	private final String confirmationMessage;

	public ConfirmListener() {
		this.confirmationMessage = DEFAULT_MESSAGE;
	}

	public ConfirmListener(String confirmationMessage) {
		this.confirmationMessage = confirmationMessage;
	}

	public CharSequence getSuccessHandler(Component component) {
		return null;
	}

	public CharSequence getFailureHandler(Component component) {
		return null;
	}

	public CharSequence getBeforeHandler(Component component) {
		return null;
	}

	public CharSequence getBeforeSendHandler(Component component) {
		return null;
	}

	public CharSequence getAfterHandler(Component component) {
		return null;
	}

	public CharSequence getCompleteHandler(Component component) {
		return null;
	}

	public CharSequence getPrecondition(Component component) {
		return "if(!confirm('" + confirmationMessage + "')) return false;";
	}

}

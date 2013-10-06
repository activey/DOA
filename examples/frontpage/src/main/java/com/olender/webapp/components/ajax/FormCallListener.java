package com.olender.webapp.components.ajax;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.attributes.IAjaxCallListener;

public class FormCallListener implements IAjaxCallListener {

	private static final String DEFAULT_MESSAGE = "Proszę czekać ...";
	private final String message;

	public FormCallListener() {
		this.message = DEFAULT_MESSAGE;
	}

	public FormCallListener(String message) {
		this.message = message;
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
		return FormCallListener.getBlockScript(this.message);
	}

	public CharSequence getAfterHandler(Component component) {
		return null;
	}

	public CharSequence getCompleteHandler(Component component) {
		return FormCallListener.getUnblockScript();
	}

	public CharSequence getPrecondition(Component component) {
		return null;
	}

	public static String getBlockScript(String message) {
		return "jQuery.blockUI({ message: '"
				+ message
				+ "', css: { border: 'none', padding: '15px', backgroundColor: '#000', '-webkit-border-radius': '10px', '-moz-border-radius': '10px', opacity: .5, color: '#fff '}});";
	}

	public static String getBlockScript() {
		return FormCallListener.getBlockScript(DEFAULT_MESSAGE);
	}

	public static String getUnblockScript() {
		return "jQuery.unblockUI();";
	}

}

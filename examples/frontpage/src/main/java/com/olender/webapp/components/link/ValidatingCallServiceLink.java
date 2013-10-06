/**
 * 
 */
package com.olender.webapp.components.link;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.IAjaxCallListener;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.FeedbackMessages;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.wicket.ui.link.FormCallServiceLink;

import com.olender.webapp.components.ajax.FormCallListener;

/**
 * @author activey
 * 
 */
public class ValidatingCallServiceLink extends FormCallServiceLink {

	public ValidatingCallServiceLink(String id) {
		super(id);
	}

	@Override
	protected void populateListeners(List<IAjaxCallListener> listeners) {
		listeners.add(new FormCallListener());
	}

	protected void onError(AjaxRequestTarget target, Form<?> form) {
		StringBuffer errorBuffer = new StringBuffer();
		for (Component component : form) {
			FeedbackMessages messages = component.getFeedbackMessages();
			for (FeedbackMessage message : messages) {
				if (message.isRendered()) {
					continue;
				}
				Serializable errorMessage = message.getMessage();
				errorBuffer.append(errorMessage).append("<br />");
				message.markRendered();
			}
		}
		String script = "jQuery.blockUI({ message: \""
				+ errorBuffer.toString()
				+ "\", cursor: 'arrow', css: { border: 'none', padding: '15px', backgroundColor: '#FF0000', '-webkit-border-radius': '5px', '-moz-border-radius': '5px', opacity: .5, color: '#fff '}}); "
				+ "jQuery('.blockOverlay').click(jQuery.unblockUI);";

		target.appendJavaScript(script);
	}

	@Override
	protected final void onBefeforeRun(IModel<IServiceDefinition> serviceModel,
			IModel<IDocument> inputModel, AjaxRequestTarget target)
			throws GeneralDOAException {
		onBefeforeRun(serviceModel.getObject(), inputModel.getObject());
	}

	protected void onBefeforeRun(IServiceDefinition service, IDocument input)
			throws GeneralDOAException {

	}

	@Override
	protected final void onAfterRun(IModel<IRunningService> runningService,
			AjaxRequestTarget target) throws GeneralDOAException {
		String script = "jQuery.unblockUI();";
		target.appendJavaScript(script);
		
		IRunningService running = runningService.getObject();
		onAfterRun(running, running.getInput(), target);
	}

	protected void onAfterRun(IRunningService runningService, IDocument input,
			AjaxRequestTarget target) throws GeneralDOAException {

	}
}

/**
 * 
 */
package com.olender.webapp.admin.feedback;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;

import pl.doa.wicket.ui.feedback.FeedbackPanel;

/**
 * @author activey
 * 
 */
public class ApplicationFeedbackPanel extends FeedbackPanel {

	public ApplicationFeedbackPanel(String id, IFeedbackMessageFilter filter) {
		super(id, filter);
	}

	public ApplicationFeedbackPanel(String id) {
		super(id);
	}

	@Override
	protected String getMessagesContainerCSSClass(FeedbackMessage message) {
		if (message.getLevel() == FeedbackMessage.INFO) {
			return "label label-success";
		} else if (message.getLevel() == FeedbackMessage.ERROR) {
			return "alert alert-error";
		} else if (message.getLevel() == FeedbackMessage.FATAL) {
			return "alert alert-error";
		}
		return null;
	}

	public boolean isVisible() {
		return anyMessage();
	}
}

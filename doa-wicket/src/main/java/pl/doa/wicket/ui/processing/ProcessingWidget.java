/**
 * 
 */
package pl.doa.wicket.ui.processing;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.wicket.ui.widgets.GenericHtmlElement;

/**
 * @author activey
 * 
 */
public class ProcessingWidget<T extends Object> extends GenericHtmlElement<T> {

	private final static Logger log = LoggerFactory
			.getLogger(ProcessingWidget.class);

	public ProcessingWidget(String id, IModel<T> model) {
		super(id, model);
	}

	protected long getDuration() {
		return 0;
	}

	protected void initElement() {
		add(new Label("duration", new AbstractReadOnlyModel<Long>() {

			@Override
			public Long getObject() {
				return getDuration() / 1000;
			}
		}));
	}
}

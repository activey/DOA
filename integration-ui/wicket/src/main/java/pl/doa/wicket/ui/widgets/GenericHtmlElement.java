/**
 * 
 */
package pl.doa.wicket.ui.widgets;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author activey
 * 
 */
public class GenericHtmlElement<T> extends WebMarkupContainer {

	private final static Logger log = LoggerFactory
			.getLogger(GenericHtmlElement.class);

	public GenericHtmlElement(String id, IModel<T> model) {
		super(id, model);
	}

	public GenericHtmlElement(String id) {
		super(id);
	}

	public final IModel<T> getModel() {
		return (IModel<T>) getDefaultModel();
	}

	public final T getModelObject() {
		return (T) getDefaultModelObject();
	}

	protected void initElement() {

	}

	@Override
	protected final void onInitialize() {
		super.onInitialize();
		try {
			initElement();
		} catch (Exception e) {
			log.error("", e);
		}
	}
}

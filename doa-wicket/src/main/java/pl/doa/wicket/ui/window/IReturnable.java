package pl.doa.wicket.ui.window;

import org.apache.wicket.ajax.AjaxRequestTarget;

public interface IReturnable<T> {

	public void publishResult(AjaxRequestTarget target, T result);
}

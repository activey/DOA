/**
 * 
 */
package pl.doa.wicket.ui.tabs;

import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.LoopItem;

/**
 * @author activey
 * 
 */
public class TabbedPanel extends AjaxTabbedPanel<ITab> {

	public TabbedPanel(String id, List<ITab> tabs) {
		super(id, tabs);
		setOutputMarkupId(true);
	}

	@Override
	protected void onComponentTag(ComponentTag tag) {
		Form form = Form.findForm(this);
		if (form == null) {
			String msg =
					String.format("Component [%s] (path = [%s]) must be "
							+ "inside form!,", getId(), getPath());

			findMarkupStream().throwMarkupException(msg);
		}
		super.onComponentTag(tag);
	}

	protected String getSelectedTabCssClass() {
		return "selected";
	}

	protected LoopItem newTabContainer(final int tabIndex) {
		return new LoopItem(tabIndex) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onComponentTag(final ComponentTag tag) {
				super.onComponentTag(tag);
				String cssClass = tag.getAttribute("class");
				if (cssClass == null) {
					cssClass = " ";
				}
				cssClass += " tab" + getIndex();

				if (getIndex() == getSelectedTab()) {
					cssClass += " " + getSelectedTabCssClass();
				}
				if (getIndex() == getTabs().size() - 1) {
					cssClass += " last";
				}
				tag.put("class", cssClass.trim());
			}

			@Override
			public boolean isVisible() {
				return getTabs().get(tabIndex).isVisible();
			}
		};
	}

	/*protected WebMarkupContainer newLink(String linkId, final int index) {
		return new AjaxSubmitLink(linkId) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if (form instanceof UpdatableForm) {
					UpdatableForm<?> updatable = (UpdatableForm<?>) form;
					updatable.updateFormModel();
				}
				setSelectedTab(index);
				target.add(form);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}

		}.setDefaultFormProcessing(false);

	}*/

}

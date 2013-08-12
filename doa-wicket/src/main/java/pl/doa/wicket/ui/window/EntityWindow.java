/**
 * 
 */
package pl.doa.wicket.ui.window;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;

import pl.doa.entity.IEntity;
import pl.doa.wicket.form.UpdatableForm;
import pl.doa.wicket.model.EntityModel;
import pl.doa.wicket.ui.panel.EntityPanel;

/**
 * @author activey
 * 
 */
public abstract class EntityWindow<T extends IEntity> extends ModalWindow {

	public EntityWindow(String id, IModel<T> model, IModel<String> titleModel) {
		super(id, model);
		setTitle(titleModel);
		setContent(createEntityPanel(getContentId()));
		setInitialHeight(450);
		setInitialWidth(500);
	}

	public EntityWindow(String id, T entity, IModel<String> titleModel) {
		this(id, new EntityModel<T>(entity), titleModel);
	}

	public EntityWindow(String id, String entityLocation,
			IModel<String> titleModel) {
		this(id, new EntityModel<T>(entityLocation), titleModel);
	}

	protected abstract EntityPanel<T> createEntityPanel(String panelId);

	public final IModel<T> getModel() {
		return (IModel<T>) getDefaultModel();
	}

	@Override
	public void show(AjaxRequestTarget target) {
		Form<?> form = Form.findForm(this);
		if (form instanceof UpdatableForm) {
			UpdatableForm<?> updatable = (UpdatableForm<?>) form;
			updatable.updateFormModel();
		}
		super.show(target);
	}

	public final T getModelObject() {
		IModel<T> model = getModel();
		if (model == null) {
			return null;
		}
		return model.getObject();
	}

	public final AbstractLink showWindowLink(String linkId) {
		return new AjaxLink<T>(linkId) {

			@Override
			public void onClick(AjaxRequestTarget target) {
				EntityWindow.this.show(target);
			}

		};
	}
}

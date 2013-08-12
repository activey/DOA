/**
 *
 */
package pl.doa.wicket.ui.window;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.wicket.ui.panel.EntityPanel;
import pl.doa.wicket.ui.panel.ReturnableEntityPanel;

/**
 * @author activey
 */
public abstract class ReturnableEntityWindow<T extends IEntitiesContainer, S extends IEntity>
        extends EntityWindow<T> implements IReturnable<S> {

    public ReturnableEntityWindow(String id, IModel<T> containerModel,
                                  IModel<String> titleModel) {
        super(id, containerModel, titleModel);
    }

    public ReturnableEntityWindow(String id, String containerLocation,
                                  IModel<String> titleModel) {
        super(id, containerLocation, titleModel);
    }

    public ReturnableEntityWindow(String id, T container,
                                  IModel<String> titleModel) {
        super(id, container, titleModel);
    }

    @Override
    public final void publishResult(AjaxRequestTarget target, S result) {
        onWindowClose(target, result);
        close(target);
    }

    protected void onWindowClose(AjaxRequestTarget target, S result) {

    }

    protected abstract ReturnableEntityPanel<S> createReturnablePanel(
            String panelId, IReturnable<S> returnable);

    @Override
    protected final EntityPanel<T> createEntityPanel(String panelId) {
        return (EntityPanel<T>) createReturnablePanel(panelId, this);
    }

}

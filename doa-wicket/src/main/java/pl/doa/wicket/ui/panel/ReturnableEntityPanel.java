/**
 *
 */
package pl.doa.wicket.ui.panel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.wicket.ui.window.IReturnable;

/**
 * @author activey
 */
public class ReturnableEntityPanel<T extends IEntity>
        extends EntityPanel<IEntitiesContainer> implements IReturnable<T> {

    private final IReturnable<T> returnable;

    public ReturnableEntityPanel(String id, IModel<IEntitiesContainer> containerModel,
                                 IReturnable<T> returnable) {
        super(id, containerModel);
        this.returnable = returnable;
    }

    public ReturnableEntityPanel(String id, String containerLocation,
                                 IReturnable<T> returnable) {
        super(id, containerLocation);
        this.returnable = returnable;
    }

    public ReturnableEntityPanel(String id, IEntitiesContainer container,
                                 IReturnable<T> returnable) {
        super(id, container);
        this.returnable = returnable;
    }

    @Override
    public void publishResult(AjaxRequestTarget target, T result) {
        if (returnable != null) {
            returnable.publishResult(target, result);
        }
    }

}

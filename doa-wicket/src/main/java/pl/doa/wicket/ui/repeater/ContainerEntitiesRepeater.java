/**
 *
 */
package pl.doa.wicket.ui.repeater;

import org.apache.wicket.model.IModel;

import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.wicket.model.container.ContainerEntitiesModel;
import pl.doa.wicket.ui.list.IteratorView;

/**
 * @author activey
 */
public abstract class ContainerEntitiesRepeater<T extends IEntity> extends IteratorView<T> {

    public ContainerEntitiesRepeater(String id,
                                     IModel<IEntitiesContainer> containerModel,
                                     IEntityEvaluator evaluator) {
        super(id, new ContainerEntitiesModel<T>(containerModel, evaluator));
    }

    public ContainerEntitiesRepeater(String id,
                                     IEntitiesContainer container,
                                     IEntityEvaluator evaluator) {
        super(id, new ContainerEntitiesModel<T>(container, evaluator));
    }

    public ContainerEntitiesRepeater(String id,
                                     IModel<IEntitiesContainer> containerModel) {
        super(id, new ContainerEntitiesModel<T>(containerModel, null));
    }

    public ContainerEntitiesRepeater(String id,
                                     IEntitiesContainer container) {
        super(id, new ContainerEntitiesModel<T>(container, null));
    }
}

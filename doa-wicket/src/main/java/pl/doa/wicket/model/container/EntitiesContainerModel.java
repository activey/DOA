/**
 *
 */
package pl.doa.wicket.model.container;

import pl.doa.container.IEntitiesContainer;
import pl.doa.wicket.model.EntityModel;

/**
 * @author activey
 */
public class EntitiesContainerModel extends EntityModel<IEntitiesContainer> {

    public EntitiesContainerModel(IEntitiesContainer entity) {
        super(entity);
    }

    public EntitiesContainerModel(long entityId) {
        super(entityId);
    }

    public EntitiesContainerModel(String entityLocation) {
        super(entityLocation);
    }

}

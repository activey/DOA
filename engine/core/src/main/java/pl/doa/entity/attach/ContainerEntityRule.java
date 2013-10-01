/**
 *
 */
package pl.doa.entity.attach;

import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityAttachRule;

/**
 * @author activey
 */
public class ContainerEntityRule<T extends IEntity> implements
        IEntityAttachRule<T> {

    private final IEntitiesContainer container;
    private final String entityName;
    private final Class<T> type;

    public ContainerEntityRule(IEntitiesContainer container, String entityName,
                               Class<T> type) {
        this.container = container;
        this.entityName = entityName;
        this.type = type;

    }

    @Override
    public T attachEntity() {
        return (container == null || entityName == null) ? null : container
                .getEntityByName(entityName, type);
    }
}

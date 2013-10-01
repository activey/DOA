/**
 *
 */
package pl.doa.entity.evaluator;

import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;

/**
 * @author activey
 */
public class EntityTypeEvaluator implements IEntityEvaluator {

    private final IEntityEvaluator otherEvaluator;

    private final Class<? extends IEntity> entityType;

    public EntityTypeEvaluator(Class<? extends IEntity> entityType, IEntityEvaluator otherEvaluator) {
        this.entityType = entityType;
        this.otherEvaluator = otherEvaluator;

    }

    /* (non-Javadoc)
     * @see pl.doa.entity.IEntityEvaluator#isReturnableEntity(pl.doa.entity.IEntity)
     */
    @Override
    public boolean isReturnableEntity(IEntity currentEntity) {
        if (entityType.isAssignableFrom(currentEntity.getClass())) {
            if (otherEvaluator != null) {
                return otherEvaluator.isReturnableEntity(currentEntity);
            }
            return true;
        }
        return false;
    }

}

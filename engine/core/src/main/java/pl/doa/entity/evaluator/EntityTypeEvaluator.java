/**
 *
 */
package pl.doa.entity.evaluator;

import pl.doa.artifact.IArtifact;
import pl.doa.document.IDocument;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;

/**
 * @author activey
 */
public class EntityTypeEvaluator<T extends IEntity> implements IEntityEvaluator {

    private final IEntityEvaluator otherEvaluator;

    private final Class<T> entityType;

    private EntityTypeEvaluator(Class<T> entityType, IEntityEvaluator otherEvaluator) {
        this.entityType = entityType;
        this.otherEvaluator = otherEvaluator;
    }

    private EntityTypeEvaluator(Class<T> entityType) {
        this(entityType, null);
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

    public static EntityTypeEvaluator typeOf(Class<? extends IEntity> entityType) {
        return new EntityTypeEvaluator(entityType, null);
    }

    public static EntityTypeEvaluator typeOfDocument() {
        return EntityTypeEvaluator.typeOf(IDocument.class);
    }

    public static EntityTypeEvaluator typeOfArtifact() {
        return EntityTypeEvaluator.typeOfArtifact(null);
    }

    public static EntityTypeEvaluator typeOfArtifact(IEntityEvaluator otherEvaluator) {
        return new EntityTypeEvaluator(IArtifact.class, otherEvaluator);
    }

}

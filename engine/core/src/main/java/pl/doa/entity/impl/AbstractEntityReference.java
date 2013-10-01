/**
 *
 */
package pl.doa.entity.impl;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.entity.DetachedEntity;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityReference;

/**
 * @author activey
 */
public abstract class AbstractEntityReference extends AbstractEntity implements
        IEntityReference {

    public AbstractEntityReference(IDOA doa) {
        super(doa);
    }

    @Override
    public final void setEntity(IEntity entity) throws GeneralDOAException {
        if (entity instanceof DetachedEntity) {
            DetachedEntity detached = (DetachedEntity) entity;
            if (!detached.isStored()) {
                throw new GeneralDOAException("Entity is not stored yet!");
            }
            setEntityImpl(detached.getStoredEntity());
        } else {
            setEntityImpl(entity);
        }
    }

    protected abstract void setEntityImpl(IEntity entity);

}

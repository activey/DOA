/**
 *
 */
package pl.doa.artifact.impl;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.artifact.IArtifact;
import pl.doa.entity.DetachedEntity;
import pl.doa.entity.IEntity;
import pl.doa.entity.impl.AbstractEntity;

/**
 * @author activey
 */
public abstract class AbstractArtifact extends AbstractEntity implements
        IArtifact {

    public AbstractArtifact(IDOA doa) {
        super(doa);
    }

    protected abstract void registerEntityImpl(IEntity entity);

    public void registerEntity(IEntity entity) throws GeneralDOAException {
        IEntity toRegister = entity;
        if (toRegister instanceof DetachedEntity) {
            DetachedEntity detached = (DetachedEntity) toRegister;
            toRegister = detached.getStoredEntity();
        }
        if (toRegister != null && entity.getArtifact() == null) {
            registerEntityImpl(toRegister);
        }
    }
}

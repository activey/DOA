package pl.doa.mapper;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.entity.IEntity;

public abstract class EntityMapper<T extends IEntity, S> {

	public abstract void map(T entity, S object) throws GeneralDOAException;

	public abstract T map(S object, IDOA doa) throws GeneralDOAException;
}

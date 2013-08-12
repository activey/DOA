package pl.doa.container;

import pl.doa.GeneralDOAException;
import pl.doa.entity.IEntity;

public interface IEntitiesIterator {

	public void next(IEntity entity) throws GeneralDOAException;
}

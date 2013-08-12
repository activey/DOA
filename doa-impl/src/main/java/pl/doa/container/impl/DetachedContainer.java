package pl.doa.container.impl;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.container.IEntitiesContainer;
import pl.doa.container.IEntitiesIterator;
import pl.doa.document.impl.DetachedDocument;
import pl.doa.entity.DetachedEntity;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.entity.IEntityProxy;
import pl.doa.entity.sort.IEntitiesSortComparator;
import pl.doa.utils.PathIterator;

public class DetachedContainer extends DetachedEntity implements
        IEntitiesContainer, IEntityProxy<IEntitiesContainer>, Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory
            .getLogger(DetachedDocument.class);

    public DetachedContainer(IDOA doa, IEntitiesContainer entity) {
        super(doa, entity);
    }

    @Override
    public Iterable<? extends IEntity> getEntities() {
        IEntitiesContainer storedEntity =
                (IEntitiesContainer) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.getEntities();
        } else {
            return null;
        }
    }

    @Override
    public Iterable<? extends IEntity> getEntities(IEntityEvaluator evaluator) {
        IEntitiesContainer storedEntity =
                (IEntitiesContainer) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.getEntities(evaluator);
        } else {
            return null;
        }
    }

    @Override
    public Iterable<? extends IEntity> getEntities(int start, int howMany,
                                                   IEntitiesSortComparator comparator, IEntityEvaluator evaluator) {
        IEntitiesContainer storedEntity =
                (IEntitiesContainer) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.getEntities(start, howMany, comparator,
                    evaluator);
        } else {
            return null;
        }
    }

    @Override
    public Iterable<? extends IEntity> getEntities(int start, int howMany,
                                                   IEntitiesSortComparator comparator, IEntityEvaluator evaluator,
                                                   boolean deep) {
        IEntitiesContainer storedEntity =
                (IEntitiesContainer) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.getEntities(start, howMany, comparator,
                    evaluator, deep);
        } else {
            return null;
        }
    }

    @Override
    public int countEntities() {
        IEntitiesContainer storedEntity =
                (IEntitiesContainer) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.countEntities();
        } else {
            return 0;
        }
    }

    @Override
    public int countEntities(IEntityEvaluator evaluator) {
        IEntitiesContainer storedEntity =
                (IEntitiesContainer) getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.countEntities(evaluator);
        } else {
            return 0;
        }
    }

    @Override
    public int countEntities(IEntityEvaluator evaluator, boolean deep) {
        IEntitiesContainer storedEntity =
                (IEntitiesContainer) getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.countEntities(evaluator, deep);
        } else {
            return 0;
        }
    }

    @Override
    public IEntity addEntity(IEntity doaEntity) throws GeneralDOAException {
        IEntitiesContainer storedEntity =
                (IEntitiesContainer) getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.addEntity(doaEntity);
        } else {
            return null;
        }
    }

    @Override
    public IEntity addEntity(IEntity doaEntity, boolean publishEvent)
            throws GeneralDOAException {
        IEntitiesContainer storedEntity =
                (IEntitiesContainer) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.addEntity(doaEntity, publishEvent);
        } else {
            return null;
        }
    }

    @Override
    public boolean hasEntity(String entityName) {
        IEntitiesContainer storedEntity =
                (IEntitiesContainer) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.hasEntity(entityName);
        } else {
            return false;
        }
    }

    @Override
    public <T extends IEntity> T getEntityByName(String name,
                                                 Class<T> entityType) {
        IEntitiesContainer storedEntity =
                (IEntitiesContainer) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.getEntityByName(name, entityType);
        } else {
            return null;
        }
    }

    @Override
    public IEntity getEntityByName(String name) {
        IEntitiesContainer storedEntity =
                (IEntitiesContainer) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.getEntityByName(name);
        } else {
            return null;
        }
    }

    @Override
    public IEntity lookupForEntity(IEntityEvaluator evaluator,
                                   boolean lookupDeep) {
        IEntitiesContainer storedEntity =
                (IEntitiesContainer) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.lookupForEntity(evaluator, lookupDeep);
        } else {
            return null;
        }
    }

    @Override
    public Iterable<IEntity> lookupForEntities(IEntityEvaluator evaluator,
                                               boolean lookupDeep) {
        IEntitiesContainer storedEntity =
                (IEntitiesContainer) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.lookupForEntities(evaluator, lookupDeep);
        } else {
            return null;
        }
    }

    @Override
    public IEntity lookup(String startLocation,
                          IEntityEvaluator returnableEvaluator) {
        IEntitiesContainer storedEntity =
                (IEntitiesContainer) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.lookup(startLocation, returnableEvaluator);
        } else {
            return null;
        }
    }

    @Override
    public Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation, int start, int howMany) {
        IEntitiesContainer storedEntity =
                (IEntitiesContainer) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.lookupEntitiesByLocation(entityLocation, start,
                    howMany);
        } else {
            return null;
        }
    }

    @Override
    public Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation) {
        IEntitiesContainer storedEntity =
                (IEntitiesContainer) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.lookupEntitiesByLocation(entityLocation);
        } else {
            return null;
        }
    }

    @Override
    public Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation, IEntityEvaluator evaluator) {
        IEntitiesContainer storedEntity =
                (IEntitiesContainer) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.lookupEntitiesByLocation(entityLocation,
                    evaluator);
        } else {
            return null;
        }
    }

    @Override
    public Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation, int start, int howMany,
            IEntitiesSortComparator comparator, IEntityEvaluator customEvaluator) {
        IEntitiesContainer storedEntity =
                (IEntitiesContainer) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.lookupEntitiesByLocation(entityLocation, start,
                    howMany);
        } else {
            return null;
        }
    }

    @Override
    public IEntity lookupEntityByLocation(String entityLocation) {
        IEntitiesContainer storedEntity =
                (IEntitiesContainer) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.lookupEntityByLocation(entityLocation);
        } else {
            return null;
        }
    }

    @Override
    public IEntity lookupEntityFromLocation(String fromLocation,
                                            IEntityEvaluator evaluator, boolean lookupDeep) {
        IEntitiesContainer storedEntity =
                (IEntitiesContainer) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.lookupEntityFromLocation(fromLocation,
                    evaluator, lookupDeep);
        } else {
            return null;
        }
    }

    @Override
    public Iterable<IEntity> lookupEntitiesFromLocation(String fromLocation,
                                                        IEntityEvaluator evaluator, boolean lookupDeep) {
        IEntitiesContainer storedEntity =
                (IEntitiesContainer) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.lookupEntitiesFromLocation(fromLocation,
                    evaluator, lookupDeep);
        } else {
            return null;
        }
    }

    @Override
    public IEntity lookupEntityByLocation(PathIterator<String> locationEntries) {
        IEntitiesContainer storedEntity =
                (IEntitiesContainer) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.lookupEntityByLocation(locationEntries);
        } else {
            return null;
        }
    }

    @Override
    public boolean hasEntities() {
        IEntitiesContainer storedEntity =
                (IEntitiesContainer) getStoredEntity();

        if (storedEntity != null) {
            return storedEntity.hasEntities();
        } else {
            return false;
        }
    }

    @Override
    public IEntitiesContainer get() {

        IEntitiesContainer stored = (IEntitiesContainer) getStoredEntity();
        if (stored == null) {
            try {
                return (IEntitiesContainer) store("/tmp");
            } catch (GeneralDOAException e) {
                log.error("", e);
            }
        }
        return stored;
    }

    @Override
    protected IEntity buildAttached(IEntitiesContainer container)
            throws GeneralDOAException {
        return null;
    }

    @Override
    public void purge(IEntityEvaluator evaluator) {
        throw new RuntimeException("Not implemented yet!");
    }

    @Override
    public void iterateEntities(IEntitiesIterator iterator,
                                IEntityEvaluator evaluator) throws GeneralDOAException {
        throw new RuntimeException("Not implemented yet!");
    }

    @Override
    public void iterateEntities(IEntitiesIterator iterator)
            throws GeneralDOAException {
        throw new RuntimeException("Not implemented yet!");
    }

}

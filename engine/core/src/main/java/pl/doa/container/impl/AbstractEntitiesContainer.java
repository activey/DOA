/*******************************************************************************
 * Copyright 2011 Inhibi Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are
 * permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright 
 * notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright 
 * notice, this list
 *        of conditions and the following disclaimer in the documentation 
 * and/or other materials
 *        provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY INHIBI LTD ``AS IS'' AND ANY 
 * EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * INHIBI LTD OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation 
 * are those of the authors and should not be interpreted as representing 
 * official policies, either expressed or implied, of Inhibi Ltd.
 *
 * Contributors:
 *    Inhibi Ltd - initial API and implementation
 *******************************************************************************/
/**
 *
 */
package pl.doa.container.impl;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.DetachedEntity;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.entity.event.impl.EntityCreatedEvent;
import pl.doa.entity.impl.AbstractEntity;
import pl.doa.entity.sort.IEntitiesSortComparator;
import pl.doa.utils.PathIterator;

import java.text.MessageFormat;

/**
 * @author activey
 */
public abstract class AbstractEntitiesContainer extends AbstractEntity
        implements IEntitiesContainer {

    public AbstractEntitiesContainer(IDOA doa) {
        super(doa);
    }

    protected abstract Iterable<? extends IEntity> getEntitiesImpl(int start,
            int howMany, IEntitiesSortComparator<? extends IEntity> comparator,
            IEntityEvaluator evaluator, boolean deep);

    @Override
    public final Iterable<? extends IEntity> getEntities(int start,
            int howMany, IEntitiesSortComparator<? extends IEntity> comparator,
            IEntityEvaluator evaluator, boolean deep) {
        Iterable<IEntity> result =
                (Iterable<IEntity>) getEntitiesImpl(start, howMany, comparator,
                        evaluator, deep);
        return result;
    }

    protected abstract Iterable<? extends IEntity> getEntitiesImpl(int start,
            int howMany, IEntitiesSortComparator<? extends IEntity> comparator,
            IEntityEvaluator evaluator);

    @Override
    public final Iterable<? extends IEntity> getEntities(int start,
            int howMany, IEntitiesSortComparator<? extends IEntity> comparator,
            IEntityEvaluator evaluator) {
        Iterable<IEntity> result =
                (Iterable<IEntity>) getEntitiesImpl(start, howMany, comparator,
                        evaluator);
        return result;
    }

    protected abstract Iterable<? extends IEntity> getEntitiesImpl();

    public final Iterable<? extends IEntity> getEntities() {
        return getEntitiesImpl();
    }

    protected abstract Iterable<? extends IEntity> getEntitiesImpl(
            IEntityEvaluator evaluator);

    public final Iterable<? extends IEntity> getEntities(
            IEntityEvaluator evaluator) {
        return getEntitiesImpl(evaluator);
    }

    protected abstract int countEntitiesImpl();

    public final int countEntities() {
        return countEntitiesImpl();
    }

    protected abstract int countEntitiesImpl(IEntityEvaluator evaluator);

    @Override
    public final int countEntities(IEntityEvaluator evaluator) {
        return countEntitiesImpl(evaluator);
    }

    protected abstract int countEntitiesImpl(IEntityEvaluator evaluator,
            boolean deep);

    @Override
    public final int countEntities(IEntityEvaluator evaluator, boolean deep) {
        return countEntitiesImpl(evaluator, deep);
    }

    protected abstract boolean hasEntityImpl(String entityName);

    @Override
    public final boolean hasEntity(String entityName) {
        if (entityName == null) {
            return false;
        }
        return hasEntityImpl(entityName);
    }

    protected abstract IEntity addEntityImpl(IEntity doaEntity)
            throws Throwable;

    public <T extends IEntity> T addEntity(T doaEntity)
            throws GeneralDOAException {
        return (T) addEntity(doaEntity, true);
    }

    public final IEntity addEntity(IEntity doaEntity, boolean publishEvent)
            throws GeneralDOAException {
        if (hasEntity(doaEntity.getName())) {
            throw new GeneralDOAException(MessageFormat.format(
                    "Entity with name [{0}] already exists in container under location {1}!",
                    doaEntity.getName(), getLocation()));
        }
        if (doaEntity instanceof DetachedEntity) {
            DetachedEntity detached = (DetachedEntity) doaEntity;
            detached.setContainer(this);
            if (publishEvent) {
                getDoa().publishEvent(new EntityCreatedEvent(doaEntity, this));
            }
            return detached.getStoredEntity();
        }
        try {
            IEntity added = addEntityImpl(doaEntity);
            if (publishEvent) {
                getDoa().publishEvent(new EntityCreatedEvent(added, this));
            }
            return added;
        } catch (Throwable t) {
            throw new GeneralDOAException(t);
        }
    }

    @Override
    public final IEntity lookupForEntity(IEntityEvaluator evaluator,
            boolean lookupDeep) {
        return getDoa().lookupEntityFromLocation(getLocation(), evaluator,
                lookupDeep);
    }

    @Override
    public final Iterable<IEntity> lookupForEntities(
            IEntityEvaluator evaluator, boolean lookupDeep) {
        return getDoa().lookupEntitiesFromLocation(getLocation(), evaluator,
                lookupDeep);
    }

    public final IEntity lookup(String startLocation,
            IEntityEvaluator returnableEvaluator) {
        return getDoa().lookup(startLocation, returnableEvaluator);
    }

    @Override
    public final Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation, int start, int howMany) {
        if (entityLocation.startsWith("/")) {
            return getDoa().lookupEntitiesByLocation(entityLocation, start, howMany);
        }
        String relativeLocation = entityLocation;
        if (relativeLocation.startsWith("./")) {
            relativeLocation = relativeLocation.substring(2);
        }
        String location =
                MessageFormat.format("{0}{1}", getLocation(), relativeLocation);
        return getDoa().lookupEntitiesByLocation(location, start, howMany);
    }

    @Override
    public final Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation) {
        if (entityLocation.startsWith("/")) {
            return getDoa().lookupEntitiesByLocation(entityLocation);
        }
        String relativeLocation = entityLocation;
        if (relativeLocation.startsWith("./")) {
            relativeLocation = relativeLocation.substring(2);
        }
        String location =
                MessageFormat.format("{0}{1}", getLocation(), relativeLocation);
        return getDoa().lookupEntitiesByLocation(location);
    }

    @Override
    public Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation, IEntityEvaluator evaluator) {
        if (entityLocation.startsWith("/")) {
            return getDoa().lookupEntitiesByLocation(entityLocation, evaluator);
        }
        String relativeLocation = entityLocation;
        if (relativeLocation.startsWith("./")) {
            relativeLocation = relativeLocation.substring(2);
        }
        String location =
                MessageFormat.format("{0}{1}", getLocation(), relativeLocation);
        return getDoa().lookupEntitiesByLocation(location, evaluator);
    }

    @Override
    public Iterable<? extends IEntity> lookupEntitiesByLocation(
            String entityLocation, int start, int howMany,
            IEntitiesSortComparator<? extends IEntity> comparator,
            IEntityEvaluator customEvaluator) {
        if (entityLocation.startsWith("/")) {
            return getDoa().lookupEntitiesByLocation(entityLocation, start, howMany,
                    comparator, customEvaluator);
        }
        String relativeLocation = entityLocation;
        if (relativeLocation.startsWith("./")) {
            relativeLocation = relativeLocation.substring(2);
        }
        String location =
                MessageFormat.format("{0}{1}", getLocation(), relativeLocation);
        return getDoa().lookupEntitiesByLocation(location, start, howMany,
                comparator, customEvaluator);
    }

    public final IEntity lookupEntityByLocation(String entityLocation) {
        String location =
                MessageFormat.format("{0}{1}", getLocation(), entityLocation);
        return getDoa().lookupEntityByLocation(location);
    }

    public final IEntity lookupEntityFromLocation(String fromLocation,
            IEntityEvaluator evaluator, boolean lookupDeep) {
        /*if (fromLocation.startsWith("/")) {
            return getDoa().lookupEntityFromLocation(fromLocation, evaluator,
					lookupDeep);
		}*/
        String relativeLocation = fromLocation;
        if (relativeLocation.startsWith("./")) {
            relativeLocation = relativeLocation.substring(1);
        }
        String location =
                MessageFormat.format("{0}{1}", getLocation(), relativeLocation);
        return getDoa().lookupEntityFromLocation(location, evaluator, lookupDeep);
    }

    @Override
    public final Iterable<IEntity> lookupEntitiesFromLocation(
            String fromLocation, IEntityEvaluator evaluator, boolean lookupDeep) {
        if (fromLocation.startsWith("/")) {
            return getDoa().lookupEntitiesFromLocation(fromLocation, evaluator,
                    lookupDeep);
        }
        String relativeLocation = fromLocation;
        if (relativeLocation.startsWith("./")) {
            relativeLocation = relativeLocation.substring(2);
        }
        String location =
                MessageFormat.format("{0}{1}", getLocation(), relativeLocation);
        return getDoa().lookupEntitiesFromLocation(location, evaluator, lookupDeep);
    }

    @Override
    public final IEntity lookupEntityByLocation(
            PathIterator<String> pathIterator) {
        if (pathIterator.isRootPath() || pathIterator.hasPrevious()) {
            String uri = pathIterator.getRemainingPath();
            IEntity entity = lookupEntityByLocation(uri);
            if (entity == null) {
                pathIterator.previous();
                return lookupEntityByLocation(pathIterator);
            }
            return entity;
        }
        return null;
    }

    public final IEntity getEntityByName(final String name) {
        IEntity entity = lookupEntityFromLocation("./", new IEntityEvaluator() {

            @Override
            public boolean isReturnableEntity(IEntity currentEntity) {
                return currentEntity.getName().equals(name);
            }
        }, false);
        return entity;
    }

    @Override
    public final <T extends IEntity> T getEntityByName(final String name,
            final Class<T> entityType) {
        IEntity entity = lookupEntityFromLocation("./", new IEntityEvaluator() {

            @Override
            public boolean isReturnableEntity(IEntity currentEntity) {
                return currentEntity.getName().equals(name)
                        && entityType.isAssignableFrom(currentEntity.getClass());
            }
        }, false);
        return (T) entity;
    }

    @Override
    public void purge(IEntityEvaluator evaluator) {
        Iterable<IEntity> toPurge = lookupForEntities(evaluator, true);
        for (IEntity entity : toPurge) {
            entity.remove(true);
        }
    }


}

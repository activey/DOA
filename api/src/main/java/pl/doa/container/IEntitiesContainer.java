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
package pl.doa.container;

import pl.doa.GeneralDOAException;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.entity.sort.IEntitiesSortComparator;
import pl.doa.utils.PathIterator;

public interface IEntitiesContainer extends IEntity {

	public static final String DEFINITION_ATTR = "definition";

	public void iterateEntities(IEntitiesIterator iterator,
			IEntityEvaluator evaluator) throws GeneralDOAException;

	public void iterateEntities(IEntitiesIterator iterator)
			throws GeneralDOAException;

	public Iterable<? extends IEntity> getEntities();

	public Iterable<? extends IEntity> getEntities(IEntityEvaluator evaluator);

	public Iterable<? extends IEntity> getEntities(int start, int howMany,
			IEntitiesSortComparator<? extends IEntity> comparator,
			IEntityEvaluator evaluator);

	public Iterable<? extends IEntity> getEntities(int start, int howMany,
			IEntitiesSortComparator<? extends IEntity> comparator,
			IEntityEvaluator evaluator, boolean deep);

	public int countEntities();

	public int countEntities(IEntityEvaluator evaluator);

	public int countEntities(IEntityEvaluator evaluator, boolean deep);

	public <T extends IEntity> T addEntity(T doaEntity)
			throws GeneralDOAException;

	public IEntity addEntity(IEntity doaEntity, boolean publishEvent)
			throws GeneralDOAException;

	public boolean hasEntity(String entityName);

	public <T extends IEntity> T getEntityByName(final String name,
			Class<T> entityType);

	public IEntity getEntityByName(final String name);

	public IEntity lookupForEntity(IEntityEvaluator evaluator,
			boolean lookupDeep);

	public Iterable<IEntity> lookupForEntities(IEntityEvaluator evaluator,
			boolean lookupDeep);

	public IEntity lookup(String startLocation,
			IEntityEvaluator returnableEvaluator);

	public Iterable<? extends IEntity> lookupEntitiesByLocation(
			String entityLocation, int start, int howMany);

	public Iterable<? extends IEntity> lookupEntitiesByLocation(
			String entityLocation);

	public Iterable<? extends IEntity> lookupEntitiesByLocation(
			String entityLocation, IEntityEvaluator evaluator);

	public Iterable<? extends IEntity> lookupEntitiesByLocation(
			String entityLocation, int start, int howMany,
			IEntitiesSortComparator<? extends IEntity> comparator,
			IEntityEvaluator customEvaluator);

	public IEntity lookupEntityByLocation(String entityLocation);

	public IEntity lookupEntityFromLocation(String fromLocation,
			IEntityEvaluator evaluator, boolean lookupDeep);

	public Iterable<IEntity> lookupEntitiesFromLocation(String fromLocation,
			IEntityEvaluator evaluator, boolean lookupDeep);

	public IEntity lookupEntityByLocation(PathIterator<String> locationEntries);

	public boolean hasEntities();

	public void purge(IEntityEvaluator evaluator);
}
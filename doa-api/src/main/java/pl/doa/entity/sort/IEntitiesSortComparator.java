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
package pl.doa.entity.sort;

import java.util.Comparator;

import pl.doa.entity.IEntity;

public interface IEntitiesSortComparator<T extends IEntity> extends
		Comparator<T> {

	public static IEntitiesSortComparator<IEntity> BY_NAME_ASC =
			new IEntitiesSortComparator<IEntity>() {

				@Override
				public int compare(IEntity entity1, IEntity entity2) {
					return entity1.getName().compareTo(entity2.getName());
				}
			};

	public static IEntitiesSortComparator<IEntity> BY_NAME_DESC =
			new IEntitiesSortComparator<IEntity>() {

				@Override
				public int compare(IEntity entity1, IEntity entity2) {
					return entity2.getName().compareTo(entity1.getName());
				}
			};

	public static IEntitiesSortComparator<IEntity> BY_CREATION_DATE_ASC =
			new IEntitiesSortComparator<IEntity>() {

				@Override
				public int compare(IEntity entity1, IEntity entity2) {
					return entity1.getCreated().compareTo(entity2.getCreated());
				}
			};

	public static IEntitiesSortComparator<IEntity> BY_CREATION_DATE_DESC =
			new IEntitiesSortComparator<IEntity>() {

				@Override
				public int compare(IEntity entity1, IEntity entity2) {
					return entity2.getCreated().compareTo(entity1.getCreated());
				}
			};

	public int compare(T entity1, T entity2);

}
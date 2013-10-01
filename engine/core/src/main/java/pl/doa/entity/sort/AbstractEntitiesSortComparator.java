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

import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.entity.IEntity;

public abstract class AbstractEntitiesSortComparator<T extends IEntity>
        implements IEntitiesSortComparator<T> {

    private final static Logger log = LoggerFactory
            .getLogger(AbstractEntitiesSortComparator.class);

    protected boolean ascending = true;

    /* (non-Javadoc)
     * @see pl.doa.entity.sort.IEntitiesSortComparator#compare(pl.doa.entity.IEntity, pl.doa.entity.IEntity)
     */
    @Override
    public final int compare(T entity1, T entity2) {
        if (ascending) {
            return isBefore(entity1, entity2) ? -1 : 1;
        } else {
            return !isBefore(entity1, entity2) ? -1 : 1;
        }
    }

    /* (non-Javadoc)
     * @see pl.doa.entity.sort.IEntitiesSortComparator#isBefore(pl.doa.entity.IEntity, pl.doa.entity.IEntity)
     */
    public abstract boolean isBefore(T entity1, T entity2);

    public static IEntitiesSortComparator createDOASortComparator(
            String className, String compare, boolean asc) {
        Class<? extends AbstractEntitiesSortComparator> clazz;
        try {
            clazz =
                    (Class<? extends AbstractEntitiesSortComparator>) Class
                            .forName(className);
        } catch (Throwable t) {
            log.error("", t);
            return null;
        }
        try {
            Constructor<? extends AbstractEntitiesSortComparator> constructor =
                    clazz.getConstructor(String.class, boolean.class);
            return constructor.newInstance(compare, asc);
        } catch (Exception ex) {
            log.error("", ex);
            return null;
        }
    }

}

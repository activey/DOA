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
package pl.doa.neo.utils;

import java.util.Iterator;

import org.neo4j.graphdb.Path;
import org.neo4j.helpers.collection.PagingIterator;

import pl.doa.IDOA;
import pl.doa.entity.IEntity;

/**
 * @author activey
 */
public class EntitiesListIterable implements Iterable<IEntity> {

    private final Iterator<Path> traverser;
    private final IDOA doa;
    private final int start;
    private final int howMany;

    public EntitiesListIterable(IDOA doa, Iterator<Path> traverser) {
        this.doa = doa;
        this.traverser = traverser;
        this.start = 0;
        this.howMany = 0;
    }

    public EntitiesListIterable(IDOA doa, Iterator<Path> traverser, int start,
                                int howMany) {
        this.doa = doa;
        this.traverser = traverser;
        this.start = start;
        this.howMany = howMany;
    }

    @Override
    public Iterator<IEntity> iterator() {
        EntitiesListIterator iterator =
                new EntitiesListIterator(doa, traverser);
        if (howMany <= 0) {
            return iterator;
        }
        return new PagingIterator<IEntity>(iterator, howMany);
    }

}

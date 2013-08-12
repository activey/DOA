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
package pl.doa.entity.impl.neo;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import pl.doa.entity.IEntityAttribute;
import pl.doa.neo.NodeDelegate;

/**
 * @author activey
 * 
 */
public class NeoEntityAttribute extends NodeDelegate implements
		IEntityAttribute {

	public static final String PROP_NAME = "name";
	public static final String PROP_VALUE = "value";

	public NeoEntityAttribute(Node underlyingNode) {
		super(underlyingNode);
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.temp.entity.IDOAAttribute#getName()
	 */
	/*
	 * (non-Javadoc)
	 * @see pl.doa.entity.IEntityAttribute#getName()
	 */
	@Override
	public String getName() {
		return (String) getProperty(PROP_NAME);
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.temp.entity.IDOAAttribute#setName(java.lang.String)
	 */
	/*
	 * (non-Javadoc)
	 * @see pl.doa.entity.IEntityAttribute#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		setProperty(PROP_NAME, name);
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.temp.entity.IDOAAttribute#getValue()
	 */
	/*
	 * (non-Javadoc)
	 * @see pl.doa.entity.IEntityAttribute#getValue()
	 */
	@Override
	public Object getValue() {
		return getProperty(PROP_VALUE);
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.temp.entity.IDOAAttribute#setValue(java.lang.Object)
	 */
	/*
	 * (non-Javadoc)
	 * @see pl.doa.entity.IEntityAttribute#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {
		setProperty(PROP_VALUE, value);
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.entity.IEntityAttribute#remove()
	 */
	@Override
	public boolean remove() {
		for (Relationship relationship : this
				.getRelationships(Direction.INCOMING)) {
			relationship.delete();
		}
		this.delete();
		return true;
	}

}

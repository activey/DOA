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
package pl.doa.artifact.tag;

import java.text.MessageFormat;

import nu.xom.Nodes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.IDOA;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.templates.tags.Tag;

public class LookupTag extends Tag {

	private final static Logger log = LoggerFactory
			.getLogger(LookupTag.class);

	private String name;
	private String location;
	private String var;
	private String className;
	private IDOA doa;

	@Override
	public void processTagStart() throws Exception {
		if (location == null) {
			location = "/";
		}
		IEntity entity =
				getDoa().lookupEntityFromLocation(location,
						new IEntityEvaluator() {

							@Override
							public boolean isReturnableEntity(
									IEntity currentEntity) {
								return currentEntity.getName().equals(name);
							}

						}, false);
		if (entity == null) {
			throw new Exception(MessageFormat.format(
					"Unable to find entity under location [{0}/{1}]...",
					location, name));
		}
		log.debug("setting found entity " + entity + " for var = " + var);
		context.setVariable(var, entity);

	}

	@Override
	public Nodes processTagEnd() throws Exception {
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public IDOA getDoa() {
		if (this.doa != null) {
			return doa;
		}
		return (IDOA) context.getVariable("doa");
	}

	public void setDoa(IDOA doa) {
		this.doa = doa;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}

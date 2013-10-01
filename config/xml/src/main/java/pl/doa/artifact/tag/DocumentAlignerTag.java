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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.alignment.IDocumentAligner;
import pl.doa.entity.IEntity;

public class DocumentAlignerTag extends EntityTag {
	private final static Logger log = LoggerFactory
			.getLogger(DocumentAlignerTag.class);
	private String logicClass;
	private IDocumentDefinition fromDefinition;
	private IDocumentDefinition toDefinition;

	public String getLogicClass() {
		return logicClass;
	}

	public void setLogicClass(String logicClass) {
		this.logicClass = logicClass;
	}

	public IDocumentDefinition getFromDefinition() {
		return fromDefinition;
	}

	public void setFromDefinition(IDocumentDefinition fromDefinition) {
		this.fromDefinition = fromDefinition;
	}

	public IDocumentDefinition getToDefinition() {
		return toDefinition;
	}

	public void setToDefinition(IDocumentDefinition toDefinition) {
		this.toDefinition = toDefinition;
	}

	@Override
	public IEntity createEntity() throws GeneralDOAException {
		IDocumentAligner aligner =
				getDoa().createDocumentAligner(getName(), fromDefinition,
						toDefinition);
		aligner.setLogicClass(getLogicClass());
		return aligner;
	}

}

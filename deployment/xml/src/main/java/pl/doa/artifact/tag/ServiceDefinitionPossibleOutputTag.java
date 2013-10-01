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
package pl.doa.artifact.tag;

import nu.xom.Nodes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.IDOA;
import pl.doa.document.IDocumentDefinition;
import pl.doa.templates.tags.Tag;

/**
 * @author activey
 */
public class ServiceDefinitionPossibleOutputTag extends Tag {

    private final static Logger log = LoggerFactory
            .getLogger(ServiceDefinitionPossibleOutputTag.class);
    private IDOA doa;
    private IDocumentDefinition definition;

    @Override
    public void processTagStart() throws Exception {
        Tag parent = getParent();
        if (parent != null && parent instanceof ServiceDefinitionTag) {
            IDocumentDefinition definition = this.definition;
            if (definition == null) {
                log.error("Can't find any possible definition to add ...");
                return;
            }
            ServiceDefinitionTag serviceTag =
                    (ServiceDefinitionTag) parent;
            serviceTag.addPossibleOutput(definition);
        }
    }

    @Override
    public Nodes processTagEnd() throws Exception {
        return null;
    }

    public void setDoa(IDOA doa) {
        this.doa = doa;
    }

    public IDOA getDoa() {
        if (this.doa != null) {
            return doa;
        }
        return (IDOA) context.getVariable("doa");
    }

    public IDocumentDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(IDocumentDefinition definition) {
        this.definition = definition;
    }
}

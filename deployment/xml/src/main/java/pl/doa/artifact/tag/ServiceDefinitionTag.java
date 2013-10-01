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
import pl.doa.service.IServiceDefinition;

public class ServiceDefinitionTag extends EntityTag {

    private final static Logger log = LoggerFactory
            .getLogger(ServiceDefinitionTag.class);

    private String logicClass;

    private IDocumentDefinition inputDefinition;

    public String getLogicClass() {
        return logicClass;
    }

    public void setLogicClass(String logicClass) {
        this.logicClass = logicClass;
    }

    @Override
    public IServiceDefinition createEntity() throws GeneralDOAException {
        IServiceDefinition service = null;
        try {
            if (ancestor == null) {
                if (logicClass == null) {
                    throw new GeneralDOAException(
                            "Logic class must be set for service [{0}]",
                            getName());
                }
                service =
                        getDoa().createServiceDefinition(getName(), logicClass);
            } else {
                service =
                        getDoa().createServiceDefinition(
                                (IServiceDefinition) ancestor, getName());
            }
            if (this.inputDefinition != null) {
                service.setInputDefinition(this.inputDefinition);
            }
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
        return service;
    }

    public IDocumentDefinition getInputDefinition() {
        return inputDefinition;
    }

    public void setInputDefinition(IDocumentDefinition inputDefinition) {
        this.inputDefinition = inputDefinition;
    }

    public void addPossibleOutput(IDocumentDefinition definition) {
        IServiceDefinition serviceDef = (IServiceDefinition) this.entity;
        serviceDef.addPossibleOutputDefinition(definition);
    }

}

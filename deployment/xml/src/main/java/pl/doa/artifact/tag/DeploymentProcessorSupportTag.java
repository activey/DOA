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

import pl.doa.GeneralDOAException;
import pl.doa.agent.IAgent;
import pl.doa.artifact.IArtifact;
import pl.doa.artifact.deploy.IDeploymentProcessor;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityReference;
import pl.doa.templates.tags.Tag;

/**
 * @author activey
 */
public abstract class DeploymentProcessorSupportTag extends Tag {

    public static final String VAR_DOA = "doa";

    public static final String VAR_ARTIFACT = "artifact";

    private IDeploymentProcessor processor;

    protected IArtifact getArtifact() {
        return (IArtifact) context.getVariable(VAR_ARTIFACT);
    }

    public void setProcessor(IDeploymentProcessor processor) {
        this.processor = processor;
    }

    public IEntitiesContainer createEntitiesContainer(String name) throws GeneralDOAException {
        return processor.createEntitiesContainer(name, getParentContainer());
    }

    public IEntityReference createReference(String name, IEntity entity) throws GeneralDOAException {
        return processor.createReference(name, entity, getParentContainer());
    }

    public IAgent createAgent(String name) throws GeneralDOAException {
        return processor.createAgent(name, getParentContainer());
    }

    private IEntitiesContainer getParentContainer() {
        Tag parent = getParent();
        if (parent == null) {
            return null;
        }
        if (parent instanceof EntitiesContainerTag) {
            EntitiesContainerTag containerTag = (EntitiesContainerTag) parent;
            return containerTag.getContainer();
        } else if (parent instanceof FieldValueTag) {
            // TODO do it somehow ...
            //((FieldValueTag) parent).setValue(this.entity);
        } else if (parent instanceof DeployTag) {
            DeployTag deployTag = (DeployTag) parent;
            IEntitiesContainer defaultContainer =
                    deployTag.getDefaultContainer();
            return defaultContainer;
        }
        return null;
    }
}

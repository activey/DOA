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
import pl.doa.IDOA;
import pl.doa.agent.IAgent;
import pl.doa.artifact.deploy.DeploymentContext;
import pl.doa.artifact.deploy.IDeploymentProcessor;
import pl.doa.channel.IChannel;
import pl.doa.channel.IIncomingChannel;
import pl.doa.channel.IOutgoingChannel;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.alignment.IDocumentAligner;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityReference;
import pl.doa.entity.event.EntityEventType;
import pl.doa.entity.event.IEntityEventListener;
import pl.doa.entity.event.IEntityEventReceiver;
import pl.doa.renderer.IRenderer;
import pl.doa.resource.IStaticResource;
import pl.doa.service.IServiceDefinition;
import pl.doa.templates.tags.Tag;

/**
 * @author activey
 */
public abstract class DeploymentProcessorSupportTag extends Tag {


    protected IDeploymentProcessor getProcessor() {
        return (IDeploymentProcessor) context.getVariable(DeploymentContext.VAR_PROCESSOR);
    }

    protected DeploymentContext getContext() {
        return (DeploymentContext) context;
    }

    public IEntitiesContainer createEntitiesContainer(String name) throws GeneralDOAException {
        return getProcessor().createEntitiesContainer(name, getParentContainer());
    }

    public IEntitiesContainer createEntitiesContainer(String name, IEntitiesContainer parent) throws GeneralDOAException {
        return getProcessor().createEntitiesContainer(name, parent);
    }

    public IEntityReference createReference(String name, IEntity entity) throws GeneralDOAException {
        return getProcessor().createReference(name, entity, getParentContainer());
    }

    public IAgent createAgent(String name) throws GeneralDOAException {
        return getProcessor().createAgent(name, getParentContainer());
    }

    public IDOA createDOA(String name, String logicClass) throws GeneralDOAException {
        return getProcessor().createDOA(name, logicClass, getParentContainer());
    }

    public IRenderer createRenderer(String name, String logicClass, String mimetype) throws GeneralDOAException {
        return getProcessor().createRenderer(name, logicClass, mimetype, getParentContainer());
    }

    public IChannel createChannel(String name, String logicClass) throws GeneralDOAException {
        return getProcessor().createChannel(name, logicClass, getParentContainer());
    }

    public IIncomingChannel createIncomingChannel(String name, String logicClass) throws GeneralDOAException {
        return getProcessor().createIncomingChannel(name, logicClass, getParentContainer());
    }

    public IOutgoingChannel createOutgoingChannel(String name, String logicClass) throws GeneralDOAException {
        return getProcessor().createOutgoingChannel(name, logicClass, getParentContainer());
    }

    public IStaticResource createStaticResource(String name, String mimetype) throws GeneralDOAException {
        return getProcessor().createStaticResource(name, mimetype, getParentContainer());
    }

    public IStaticResource createStaticResource(String name, String mimetype, IEntitiesContainer parent) throws GeneralDOAException {
        return getProcessor().createStaticResource(name, mimetype, parent);
    }

    public IDocument createDocument(IDocumentDefinition definition, String name) throws GeneralDOAException {
        return getProcessor().createDocument(definition, name, getParentContainer());
    }

    public IDocumentDefinition createDocumentDefinition(String name) throws GeneralDOAException {
        return getProcessor().createDocumentDefinition(name, getParentContainer());
    }

    public IDocumentDefinition createDocumentDefinition(String name, IDocumentDefinition ancestor) throws GeneralDOAException {
        return getProcessor().createDocumentDefinition(name, getParentContainer(), ancestor);
    }

    public IServiceDefinition createServiceDefinition(String name, String logicClass) throws GeneralDOAException {
        return getProcessor().createServiceDefinition(name, logicClass, getParentContainer());
    }

    public IServiceDefinition createServiceDefinition(IServiceDefinition ancestor, String name) throws GeneralDOAException {
        return getProcessor().createServiceDefinition(ancestor, name, getParentContainer());
    }

    public IDocumentAligner createDocumentAligner(String name, IDocumentDefinition fromDefinition, IDocumentDefinition toDefinition) throws GeneralDOAException {
        return getProcessor().createDocumentAligner(name, fromDefinition, toDefinition, getParentContainer());
    }

    public IEntityEventListener createEntityEventListener(IEntity sourceEntity, IEntityEventReceiver receiver, EntityEventType eventType) throws GeneralDOAException {
        return getProcessor().createEntityEventListener(sourceEntity, receiver, eventType, getParentContainer());
    }

    public IEntitiesContainer getDeploymentRoot() {
        return (IEntitiesContainer) context.getVariable(DeploymentContext.VAR_ROOT);
    }

    protected IEntitiesContainer getParentContainer() {
        Tag parent = getParent();
        if (parent == null) {
            return null;
        }
        if (parent instanceof EntitiesContainerTag) {
            EntitiesContainerTag containerTag = (EntitiesContainerTag) parent;
            return containerTag.getContainer();
        } else if (parent instanceof FieldValueTag) {
            throw new UnsupportedOperationException("Setting values in value tag not supported yet!");
            // TODO do it somehow ...
            //((FieldValueTag) parent).setValue(this.entity);
        }
        return getDeploymentRoot();
    }
}

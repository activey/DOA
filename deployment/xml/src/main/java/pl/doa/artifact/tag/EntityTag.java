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

import nu.xom.Nodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.artifact.deploy.IDeploymentProcessor;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.templates.xml.element.BaseElement;

public abstract class EntityTag<T extends IEntity> extends DeploymentProcessorSupportTag {

    private final static Logger log = LoggerFactory.getLogger(EntityTag.class);
    protected String location;
    protected String var;
    protected T entity;
    protected IEntity ancestor;
    private String name;

    @Override
    public final void processTagStart() throws Exception {
        log.debug(String.format("Processing tag: %s", this.getClass().getName()));
        this.entity = createEntity();
        if (entity == null) {
            return;
        }
        context.setVariable(getVar(), entity);
    }

    @Override
    public final Nodes processTagEnd() throws Exception {
        BaseElement deployedEntityElement = createElement("entity");
        deployedEntityElement.addAttribute("class", entity.getClass().getName());
        deployedEntityElement.appendChildren(element.getChildNodes());
        return new Nodes(deployedEntityElement);
    }

    public String getName() {
        if (name == null || name.isEmpty()) {
            if (ancestor != null) {
                return ancestor.getName();
            }
            return null;
        }
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
        if (var == null || var.isEmpty()) {
            IDeploymentProcessor processor = getProcessor();
            IEntitiesContainer root = getDeploymentRoot();
            String entityLocation = entity.getLocation();
            String relativeLocation = entityLocation.substring(root.getLocation().length());
            return relativeLocation;
        }
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void setAncestor(IEntity ancestor) {
        this.ancestor = ancestor;
    }

    public void setAttribute(String attrName, String attrValue) {
        entity.setAttribute(attrName, attrValue);
    }

    public abstract T createEntity() throws GeneralDOAException;

    public final T getEntity() {
        return this.entity;
    }
}

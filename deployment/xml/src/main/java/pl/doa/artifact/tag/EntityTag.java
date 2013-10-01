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
import java.util.List;

import nu.xom.Nodes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.artifact.IArtifact;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.entity.IEntityReference;
import pl.doa.entity.startable.IStartableEntity;
import pl.doa.templates.tags.Tag;

public abstract class EntityTag<T extends IEntity> extends ArtifactSupport {

    private final static Logger log = LoggerFactory.getLogger(EntityTag.class);
    private String name;
    protected String location;
    protected String var;
    protected T entity;
    protected IEntity ancestor;

    @Override
    public final void processTagStart() throws Exception {
        this.entity = createEntity();
        if (entity == null) {
            return;
        }
        log.debug("Process tag: " + entity.getName());

        if (getVar() != null) {
            context.setVariable(getVar(), entity);
        }

        if (this.location != null) {
            this.entity = store(this.location);
            getArtifact().registerEntity(entity);
            return;
        }

        Tag parent = getParent();
        if (parent == null) {
            return;
        }
        if (parent instanceof EntitiesContainerTag) {
            EntitiesContainerTag containerTag = (EntitiesContainerTag) parent;
            this.entity = containerTag.add(this.entity);
        } else if (parent instanceof FieldValueTag) {
            ((FieldValueTag) parent).setValue(this.entity);
        } else if (parent instanceof DeployTag) {
            DeployTag deployTag = (DeployTag) parent;
            IEntitiesContainer defaultContainer =
                    deployTag.getDefaultContainer();
            if (defaultContainer.hasEntity(this.entity.getName())) {
                this.entity =
                        store(defaultContainer.getLocation());
            } else {
                this.entity = defaultContainer.addEntity(this.entity);
            }
        } else {
            if (this.location != null) {
                this.entity = store(this.location);
            }
        }
        getArtifact().registerEntity(entity);
    }

    @Override
    public final Nodes processTagEnd() throws Exception {
        return null;
    }

    public final IArtifact getArtifact() {
        return (IArtifact) context.getVariable("artifact");
    }

    public void setName(String name) {
        this.name = name;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getVar() {
        if (var == null || var.isEmpty()) {
            return null;
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

    protected T store(String location)
            throws GeneralDOAException {
        final String nodeName = entity.getName();

        IEntity found =
                getDoa().lookupEntityFromLocation(location,
                        new IEntityEvaluator() {

                            @Override
                            public boolean isReturnableEntity(
                                    IEntity currentEntity) {
                                return currentEntity.getName().equals(nodeName);
                            }
                        }, false);
        if (found == null) {
            log.debug("Storing new entity: " + nodeName);
            return getDoa().store(location, entity);
        }
        log.debug("Found existing entity under location: " + location);
        return (T) found;
    }

    protected void addStartable(IStartableEntity startableEntity) {
        log.debug(MessageFormat.format("registering startable entity: {0}",
                startableEntity.getLocation()));
        // dodawanie referencji do entity w /autostart
        IEntityReference reference;
        try {
            reference =
                    getDoa().createReference(startableEntity.getName(),
                            startableEntity);
            reference.store(IDOA.AUTOSTART_CONTAINER);
        } catch (GeneralDOAException e) {
            log.error("", e);
        }
        List<IStartableEntity> startableEntities =
                (List<IStartableEntity>) context.getVariable("autostart");
        startableEntities.add(startableEntity);
    }


    public final T getEntity() {
        return this.entity;
    }
}

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
package pl.doa.http.ext.webdav.resource.impl;

import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.resource.CollectionResource;
import io.milton.resource.FolderResource;
import io.milton.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.http.ext.webdav.resource.builder.ResourceBuilderFactory;
import pl.doa.http.ext.webdav.resource.builder.impl.DOAEntityResourceBuilder;
import pl.doa.resource.IStaticResource;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class DOAEntitiesContainerResource extends
        DOAEntityResource<IEntitiesContainer> implements FolderResource {

    private final static Logger log = LoggerFactory
            .getLogger(DOAEntitiesContainerResource.class);

    public DOAEntitiesContainerResource(ResourceBuilderFactory factory, IDOA doa) {
        super(factory, doa);
    }

    public DOAEntitiesContainerResource(ResourceBuilderFactory factory,
                                        IEntitiesContainer container) {
        super(factory, container);
    }

    @Override
    public CollectionResource createCollection(String newName)
            throws NotAuthorizedException, ConflictException {
        if (entity instanceof IEntitiesContainer) {
            IEntity existing = entity.getEntityByName(newName);
            if (existing == null) {
                IEntitiesContainer newContainer = null;
                try {
                    newContainer = factory.getDoa().createContainer(newName);
                    entity.addEntity(newContainer);
                } catch (GeneralDOAException e) {
                    log.error("", e);
                    return null;
                }
                DOAEntityResourceBuilder builder = factory
                        .getBuilder(newContainer);
                if (builder == null) {
                    log.error(MessageFormat
                            .format("Unable to find resource builder for entity type: {0}",
                                    newContainer.getClass().getName()));
                    return null;
                }
                return (CollectionResource) builder.buildResource(newContainer);
            }

        }
        throw new NotAuthorizedException(this);
    }

    @Override
    public Resource child(String childName) {
        IEntity foundEntity = null;
        if (entity instanceof IDOA) {
            IDOA doa = (IDOA) entity;
            foundEntity = doa.lookupEntityByLocation("/" + childName);
        } else if (entity instanceof IEntitiesContainer) {
            foundEntity = entity.getEntityByName(childName);
        }
        if (foundEntity == null) {
            return null;
        }
        DOAEntityResourceBuilder builder = factory.getBuilder(foundEntity);
        if (builder != null) {
            return builder.buildResource(foundEntity);
        }
        DOAEntityResource<IEntity> resource = new DOAEntityResource<IEntity>(
                factory, foundEntity);
        return resource;
    }

    @Override
    public List<? extends Resource> getChildren() {
        List<Resource> children = new ArrayList<Resource>();
        Iterable<? extends IEntity> entities = null;
        if (entity instanceof IDOA) {
            IDOA doa = (IDOA) entity;
            entities = doa.lookupEntitiesByLocation("/");
        } else if (entity instanceof IEntitiesContainer) {
            IEntitiesContainer container = (IEntitiesContainer) entity;
            entities = container.getEntities();
        }
        for (IEntity doaEntity : entities) {
            DOAEntityResourceBuilder builder = factory.getBuilder(doaEntity);
            if (builder != null) {
                children.add(builder.buildResource(doaEntity));
            } else {
                children.add(new DOAEntityResource<IEntity>(factory, doaEntity));
            }
        }
        return children;
    }

    @Override
    public Resource createNew(String newName, InputStream inputStream,
                              Long length, String contentType) throws IOException,
            ConflictException {
        try {
            IStaticResource resource = factory.getDoa().createStaticResource(
                    newName, contentType);
            resource.setContentFromStream(inputStream, length);
            IEntitiesContainer container = (IEntitiesContainer) entity;
            container.addEntity(resource);
            return factory.getBuilder(resource).buildResource(resource);
        } catch (GeneralDOAException e) {
            throw new IOException(e);
        }
    }
}

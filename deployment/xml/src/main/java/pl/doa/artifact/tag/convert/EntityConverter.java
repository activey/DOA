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
package pl.doa.artifact.tag.convert;

import org.apache.commons.beanutils.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.artifact.deploy.DeploymentContext;
import pl.doa.artifact.deploy.IDeploymentProcessor;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.templates.TemplateContext;
import pl.doa.utils.PathIterator;

public class EntityConverter implements Converter {

    private final static Logger log = LoggerFactory
            .getLogger(EntityConverter.class);

    private final TemplateContext context;
    private final IDeploymentProcessor processor;

    public EntityConverter(TemplateContext context) {
        this.context = context;
        this.processor = (IDeploymentProcessor) context
                .getVariable(DeploymentContext.VAR_PROCESSOR);
    }

    @Override
    public Object convert(Class type, Object value) {
        if (value == null) {
            return null;
        }
        if (type.isAssignableFrom(value.getClass())) {
            return value;
        } else if (value instanceof String) {
            String location = (String) value;
            IEntity entity = findEntity(location, IEntitiesContainer.class.isAssignableFrom(type));
            return entity;
        }
        return null;
    }

    protected IEntity findEntity(String lookupString, boolean createIfNull) {
        IEntity found = null;
        if (lookupString.startsWith("#") || lookupString.startsWith("@")) {
            String contextVar = lookupString.substring(1);
            found = (IEntity) context.getVariable(contextVar);
            if (found != null) {
                return found;
            }
        }
        found = processor.lookupEntityByLocation(lookupString);
        if (found == null) {
            log.debug(String.format("Unable to find entity under absolute location: [%s]", lookupString));
        }
        /*
        TODO include cases with non existing entity
        if (found == null) {
            if (createIfNull) {
                try {
                    return createContainer(doa, lookupString);
                } catch (GeneralDOAException e) {
                    log.error("", e);
                }
            }
            log.error(MessageFormat.format("Unable to find entity: [{0}]",
                    lookupString));

        }*/
        return found;
    }

    /*
    TODO not used because of comment in line 94
    protected final IEntitiesContainer createContainer(IEntitiesContainer baseContainer, String containerPath) throws GeneralDOAException {
        PathIterator<String> iterator = new EntityLocationIterator(containerPath);
        return createContainer(baseContainer, iterator);
    } */

    private final IEntitiesContainer createContainer(IEntitiesContainer baseContainer, PathIterator<String> pathIterator) throws GeneralDOAException {
        if (pathIterator.hasNext()) {
            String packagePart = pathIterator.next();
            IEntitiesContainer packageContainer = baseContainer.getEntityByName(packagePart, IEntitiesContainer.class);
            if (packageContainer == null) {
                packageContainer = processor.createEntitiesContainer(packagePart, baseContainer);
            }
            return createContainer(packageContainer, pathIterator);

        }
        return baseContainer;
    }

}

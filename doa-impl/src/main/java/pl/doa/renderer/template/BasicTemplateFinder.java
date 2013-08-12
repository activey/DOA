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
package pl.doa.renderer.template;

import java.text.MessageFormat;

import pl.doa.IDOA;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.renderer.ITemplateFinder;
import pl.doa.resource.IStaticResource;

/**
 * @author activey
 */
public class BasicTemplateFinder implements ITemplateFinder {

    /*
     * (non-Javadoc)
     *
     * @see pl.doa.renderer.ITemplateFinder#findTemplate(pl.doa.container.
     * IEntitiesContainer, pl.doa.document.IDocumentDefinition,
     * pl.doa.agent.IAgent)
     */
    @Override
    public IStaticResource findTemplate(IEntitiesContainer lookupContainer,
                                        IEntity entity, String templateSuffix) {
        String templateLocation = null;
        if (entity.isInside(lookupContainer)
                && !(lookupContainer instanceof IDOA)) {
            templateLocation = MessageFormat.format(
                    ".{0}{1}",
                    entity.getLocation().substring(
                            lookupContainer.getLocation().length()),
                    templateSuffix);
        } else {
            templateLocation = MessageFormat.format("{0}/{1}{2}", entity
                    .getContainer().getLocation(), entity.getName(),
                    templateSuffix);
        }
        IStaticResource resource = (IStaticResource) lookupContainer
                .lookupEntityByLocation(templateLocation);
        return resource;
    }

}

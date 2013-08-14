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
package pl.doa.servlet.renderer;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.renderer.ITemplateFinder;
import pl.doa.resource.IStaticResource;

/**
 * @author activey
 */
public class MultiContainerFinder implements ITemplateFinder {

    private final static Logger log = LoggerFactory
            .getLogger(MultiContainerFinder.class);
    private final IEntitiesContainer[] containers;
    private final ITemplateFinder finder;

    public MultiContainerFinder(ITemplateFinder finder,
                                IEntitiesContainer... containers) {
        this.finder = finder;
        this.containers = containers;
    }

    /* (non-Javadoc)
     * @see pl.doa.renderer.ITemplateFinder#findTemplate(pl.doa.document.IDocument, pl.doa.agent.IAgent)
     */
    @Override
    public IStaticResource findTemplate(IEntitiesContainer fallbackContainer,
                                        IEntity entity, String templateSuffix) {
        IStaticResource found = null;
        for (IEntitiesContainer container : containers) {
            log.debug(MessageFormat.format("Looking up in: {0}",
                    container.getLocation()));
            found = finder.findTemplate(container, entity, templateSuffix);
            if (found != null) {
                return found;
            }
        }
        log.debug(MessageFormat.format("Looking up in: {0}",
                fallbackContainer.getLocation()));
        found =
                finder.findTemplate(fallbackContainer, entity,
                        templateSuffix);
        if (found != null) {
            return found;
        }
        return null;
    }

}

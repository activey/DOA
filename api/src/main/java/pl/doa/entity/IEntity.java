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
package pl.doa.entity;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.artifact.IArtifact;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.event.IEntityEventDescription;
import pl.doa.entity.event.IEntityEventListener;
import pl.doa.renderer.IRenderer;
import pl.doa.resource.IStaticResource;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author activey
 */

public interface IEntity {

    public long getId();

    public IDOA getDoa();

    public String getName();

    public void setName(String name);

    public IEntitiesContainer getContainer();

    public void setContainer(IEntitiesContainer container)
            throws GeneralDOAException;

    public String getLocation();

    public boolean hasAttributes();

    public Collection<String> getAttributeNames();

    public String getAttribute(String attrName);

    public String getAttribute(String attrName, String defaultValue);

    public IEntityAttribute getAttributeObject(final String attrName);

    public void setAttribute(String attrName, String attrValue);

    public void setAttribute(IEntityAttribute attributte);

    public void removeAttributes();

    public boolean remove();

    public boolean remove(boolean forceRemoveContents);

    public boolean isStored();

    public IEntity store(String location) throws GeneralDOAException;

    public void setAttributes(Map<String, String> attributes);

    public boolean equals(IEntity entity);

    public boolean hasEventListeners();

    /**
     * Retrieves list of all event listeners listening on events propagated from this entity.
     * @return
     */
    public List<IEntityEventListener> getEventListeners();

    /**
     * Retrievies list of all event listeners that match criteria of given event.
     *
     * @param event Event object to match by listeners to be retrieved.
     * @return
     */
    public List<IEntityEventListener> getEventListeners(IEntityEventDescription event);

    public boolean isPublic();

    public IArtifact getArtifact();

    public Date getLastModified();

    public Date getCreated();

    public IEntity getAncestor();

    public boolean isInside(IEntitiesContainer container);

    public boolean isDescendantOf(IEntity ancestor);

    public IStaticResource render(String mimeType) throws GeneralDOAException;

    public IStaticResource render(IRenderer renderer)
            throws GeneralDOAException;

    public IStaticResource render(String mimeType, IStaticResource template)
            throws GeneralDOAException;

    public IStaticResource render(IRenderer renderer, IStaticResource template)
            throws GeneralDOAException;
}
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
package pl.doa.entity.impl;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.artifact.IArtifact;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.impl.AbstractDocument;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityAttribute;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.entity.event.IEntityEventDescription;
import pl.doa.entity.event.IEntityEventListener;
import pl.doa.renderer.IRenderer;
import pl.doa.renderer.template.BasicTemplateFinder;
import pl.doa.resource.IStaticResource;

import java.util.*;

/**
 * @author activey
 */
public abstract class AbstractEntity implements IEntity {

    private final IDOA doa;

    public AbstractEntity(IDOA doa) {
        this.doa = doa;
    }

    public final IDOA getDoa() {
        if (this.doa == null && (this instanceof IDOA)) {
            return (IDOA) this;
        }
        return this.doa;
    }

    protected abstract long getIdImpl();

    public final long getId() {
        return getIdImpl();
    }

    protected abstract String getNameImpl();

    public final String getName() {
        return getNameImpl();
    }

    protected abstract void setNameImpl(String name);

    public final void setName(String name) {
        setNameImpl(name);
    }

    protected abstract IEntitiesContainer getContainerImpl();

    public final IEntitiesContainer getContainer() {
        return getContainerImpl();
    }

    protected abstract String getLocationImpl();

    public final String getLocation() {
        return getLocationImpl();
    }

    protected abstract Collection<String> getAttributeNamesImpl();

    public final List<String> getAttributeNames() {
        List<String> allAttrs = new ArrayList<String>();
        IEntity ancestor = getAncestor();
        if (ancestor != null) {
            allAttrs.addAll(ancestor.getAttributeNames());
        }
        allAttrs.addAll(getAttributeNamesImpl());
        return allAttrs;
    }

    protected abstract Object getAttributeImpl(String attrName);

    public final String getAttribute(String attrName) {
        String attrValue = getAttribute(attrName, null);
        if (attrValue == null) {
            IEntity ancestor = getAncestor();
            if (ancestor != null) {
                return ancestor.getAttribute(attrName);
            }
        }
        return attrValue;
    }

    public final String getAttribute(String attrName, String defaultValue) {
        Object attrValue = getAttributeImpl(attrName);
        if (attrValue == null) {
            IEntity ancestor = getAncestor();
            if (ancestor != null) {
                String ancestorValue = ancestor.getAttribute(attrName);
                if (ancestorValue == null) {
                    return defaultValue;
                }
            }
        }
        if (attrValue == null) {
            return defaultValue;
        }
        return attrValue.toString();
    }

    @Override
    public final boolean hasAttributes() {
        boolean hasAny = hasAttributesImpl();
        IEntity ancestor = getAncestor();
        if (ancestor != null) {
            boolean ancestorValue = ancestor.hasAttributes();
            if (ancestorValue) {
                return ancestorValue;
            }
        }
        return hasAny;
    }

    protected abstract boolean hasAttributesImpl();

    protected abstract IEntityAttribute getAttributeObjectImpl(String attrName);

    public final IEntityAttribute getAttributeObject(String attrName) {
        IEntityAttribute attrValue = getAttributeObjectImpl(attrName);
        if (attrValue == null) {
            IEntity ancestor = getAncestor();
            if (ancestor != null) {
                return ancestor.getAttributeObject(attrName);
            }
        }
        return attrValue;
    }

    protected abstract IEntity storeImpl(String location) throws Throwable;

    public final IEntity store(String location) throws GeneralDOAException {
        try {
            return storeImpl(location);
        } catch (Throwable e) {
            throw new GeneralDOAException(e);
        }
    }

    protected abstract void setContainerImpl(IEntitiesContainer container) throws GeneralDOAException;

    public final void setContainer(IEntitiesContainer container) throws GeneralDOAException {
        setContainerImpl(container);
    }

    protected abstract void setAttributesImpl(Map<String, String> attributes);

    public final void setAttributes(Map<String, String> attributes) {
        setAttributesImpl(attributes);
    }

    public final boolean equals(IEntity entity) {
        if (entity == null) {
            return false;
        }
        return entity.getId() == getId();
    }

    protected abstract boolean hasEventListenersImpl();

    public final boolean hasEventListeners() {
        return hasEventListenersImpl();
    }

    @Override
    public List<IEntityEventListener> getEventListeners(IEntityEventDescription event) {
        List<IEntityEventListener> listeners = getEventListeners();
        if (listeners == null || listeners.size() == 0) {
            return null;
        }
        List<IEntityEventListener> filtered = new ArrayList<IEntityEventListener>();
        for (IEntity doaEntity : listeners) {
            final IEntityEventListener listener = (IEntityEventListener) doaEntity;
            if (!listener.eventMatch(event)) {
                continue;
            }
            filtered.add(listener);
        }
        return filtered;
    }

    protected abstract List<IEntityEventListener> getEventListenersImpl();

    public final List<IEntityEventListener> getEventListeners() {
        return getEventListenersImpl();
    }

    protected abstract boolean isPublicImpl();

    public final boolean isPublic() {
        return isPublicImpl();
    }

    protected abstract IArtifact getArtifactImpl();

    public final IArtifact getArtifact() {
        return getArtifactImpl();
    }

    protected abstract Date getLastModifiedImpl();

    public final Date getLastModified() {
        return getLastModifiedImpl();
    }

    protected abstract Date getCreatedImpl();

    public final Date getCreated() {
        return getCreatedImpl();
    }

    protected abstract void setAttributeImpl(String attrName, String attrValue);

    public final void setAttribute(String attrName, String attrValue) {
        setAttributeImpl(attrName, attrValue);
    }

    protected abstract void setAttributeImpl(IEntityAttribute attributte);

    public final void setAttribute(IEntityAttribute attributte) {
        setAttributeImpl(attributte);
    }

    protected abstract void removeAttributesImpl();

    public final void removeAttributes() {
        removeAttributesImpl();
    }

    protected abstract boolean removeImpl(boolean forceRemoveContents);

    public final boolean remove() {
        removeAttributes();
        return removeImpl(false);
    }

    public final boolean remove(boolean forceRemoveContents) {
        return removeImpl(forceRemoveContents);
    }

    protected abstract boolean isStoredImpl();

    public final boolean isStored() {
        return isStoredImpl();
    }

    protected abstract IEntity getAncestorImpl();

    public final IEntity getAncestor() {
        return getAncestorImpl();
    }

    public final boolean isInside(IEntitiesContainer container) {
        String location = getLocation();
        if (location == null) {
            return false;
        }
        String containerLocation = container.getLocation();
        if (containerLocation == null) {
            return false;
        }
        return location.startsWith(containerLocation)
                && location.length() > containerLocation.length();
    }

    @Override
    public String toString() {
        return getLocation();
    }

    /**
     * Verifies if entity represented by this class derives some properties from @ancestor.
     *
     * @param ancestor An object from properties are derived.
     * @return Flag determines if given this object derives from @ancestor
     */
    public final boolean isDescendantOf(IEntity ancestor) {
        return AbstractEntity.isDescendantOf(ancestor, this);
    }

    /**
     * Verifies if given @descendant derives some properties from @ancestor.
     *
     * @param ancestor   An object from properties are derived.
     * @param descendant An object that derives properties from @ancestor.
     * @return Flag determines if given @descendant derives from @ancestor
     */
    public final static boolean isDescendantOf(IEntity ancestor,
                                               IEntity descendant) {
        IEntity thisAncestor = descendant.getAncestor();
        if (thisAncestor == null) {
            return false;
        }
        if (thisAncestor.equals(ancestor)) {
            return true;
        }
        return thisAncestor.isDescendantOf(ancestor);
    }

    @Override
    public final IStaticResource render(final String mimeType)
            throws GeneralDOAException {
        return AbstractDocument.render(this, mimeType, doa, null);
    }

    @Override
    public final IStaticResource render(IRenderer renderer)
            throws GeneralDOAException {
        return AbstractDocument.render(this, renderer, null);
    }

    @Override
    public final IStaticResource render(final String mimeType,
                                        IStaticResource template) throws GeneralDOAException {
        return AbstractDocument.render(this, mimeType, doa, template);
    }

    @Override
    public final IStaticResource render(IRenderer renderer,
                                        IStaticResource template) throws GeneralDOAException {
        return AbstractDocument.render(this, renderer, template);
    }

    public static IStaticResource render(IEntity entity,
                                         final String mimeType, IDOA doa, IStaticResource template)
            throws GeneralDOAException {
        IStaticResource result = null;

        if (mimeType != null) {
            IRenderer renderer =
                    (IRenderer) doa.lookupEntityFromLocation("/renderers",
                            new IEntityEvaluator() {

                                @Override
                                public boolean isReturnableEntity(
                                        IEntity currentEntity) {
                                    if (!(currentEntity instanceof IRenderer)) {
                                        return false;
                                    }
                                    IRenderer renderer =
                                            (IRenderer) currentEntity;

                                    return mimeType.equals(renderer
                                            .getMimetype());

                                }
                            }, true);

            if (renderer != null) {
                if (template == null) {
                    result =
                            renderer.renderEntity(entity,
                                    new BasicTemplateFinder());
                } else {
                    result = renderer.renderEntity(entity, template);
                }
            } else {
                throw new GeneralDOAException("Renderer not found.");
            }
        } else {
            throw new GeneralDOAException("Mime type is null.");
        }

        return result;
    }

    public static IStaticResource render(IEntity entity,
                                         IRenderer renderer, IStaticResource template)
            throws GeneralDOAException {
        IStaticResource result = null;

        if (renderer != null) {
            if (template == null) {
                result =
                        renderer.renderEntity(entity,
                                new BasicTemplateFinder());
            } else {
                result = renderer.renderEntity(entity, template);
            }
        } else {
            throw new GeneralDOAException("Renderer is null.");
        }

        return result;
    }

}

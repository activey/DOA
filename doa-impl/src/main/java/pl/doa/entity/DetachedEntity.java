package pl.doa.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.artifact.IArtifact;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.impl.AbstractDocument;
import pl.doa.entity.event.IEntityEventListener;
import pl.doa.entity.impl.AbstractEntity;
import pl.doa.renderer.IRenderer;
import pl.doa.resource.IStaticResource;

public abstract class DetachedEntity implements IEntity, Serializable {

    private static final long serialVersionUID = 1L;

    private final static Logger log = LoggerFactory
            .getLogger(DetachedEntity.class);

    private String name;
    private long id;

    private Map<String, String> attributes;
    protected transient IDOA doa;

    private Date created;
    private Date lastModified;

    private transient IEntity storedEntity = null;
    private long storedEntityId = -1;

    public DetachedEntity(IDOA doa) {
        this.doa = doa;
        this.created = new Date();
        this.lastModified = new Date();
    }

    public DetachedEntity(IDOA doa, IEntity entity) {
        this.doa = doa;
        this.storedEntityId = entity.getId();
    }

    public void setId(long id) {
        this.id = id;
    }

    public final long getId() {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.getId();
        }
        return this.id;
    }

    @Override
    public final IDOA getDoa() {
        return this.doa;
    }

    @Override
    public final String getName() {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            storedEntity.getName();
        }
        return this.name;
    }

    @Override
    public final void setName(String name) {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            storedEntity.setName(name);
        }
        this.name = name;
    }

    @Override
    public final IEntitiesContainer getContainer() {
        if (!isStored()) {
            return null;
        }
        IEntity storedEntity = getStoredEntity();
        return storedEntity.getContainer();
    }

    @Override
    public final String getLocation() {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.getLocation();
        }
        return null;
    }

    @Override
    public final boolean hasAttributes() {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.hasAttributes();
        }
        return this.attributes != null && this.attributes.size() > 0;
    }

    @Override
    public final Collection<String> getAttributeNames() {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.getAttributeNames();
        }
        if (this.attributes == null) {
            return new ArrayList<String>();
        }
        return this.attributes.keySet();
    }

    @Override
    public final String getAttribute(String attrName) {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            storedEntity.getAttribute(attrName);
        }
        if (this.attributes == null) {
            return null;
        }
        return attributes.get(attrName);
    }

    @Override
    public final String getAttribute(String attrName, String defaultValue) {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.getAttribute(attrName, defaultValue);
        }
        if (this.attributes == null) {
            return defaultValue;
        }
        String val = attributes.get(attrName);
        return (val == null) ? defaultValue : val;
    }

    @Override
    public final IEntityAttribute getAttributeObject(String attrName) {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.getAttributeObject(attrName);
        }
        // TODO !!!
        return null;
    }

    @Override
    public final void setAttribute(String attrName, String attrValue) {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            storedEntity.setAttribute(attrName, attrValue);
        }
        // TODO !!!
    }

    @Override
    public final void setAttribute(IEntityAttribute attributte) {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            storedEntity.setAttribute(attributte);
        }
        // TODO !!!
    }

    @Override
    public final void removeAttributes() {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            storedEntity.removeAttributes();
        }
        // TODO !!!
    }

    @Override
    public final boolean remove() {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            storedEntity.remove();
        }
        // TODO !!!
        return false;
    }

    @Override
    public final boolean remove(boolean forceRemoveContents) {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            storedEntity.remove(forceRemoveContents);
        }
        // TODO !!!
        return false;
    }

    @Override
    public final boolean isStored() {
        return storedEntityId > 0;
    }

    @Override
    public final IEntity store(String location) throws GeneralDOAException {
        return doa.store(location, this);
    }

    public final void setContainer(String containerLocation)
            throws GeneralDOAException {
        IEntitiesContainer container =
                (IEntitiesContainer) doa
                        .lookupEntityByLocation(containerLocation);
        setContainer(container);
    }

    @Override
    public final void setContainer(IEntitiesContainer container)
            throws GeneralDOAException {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            storedEntity.setContainer(container);
            return;
        }
        this.storedEntityId = buildAttached(container).getId();
    }

    protected abstract IEntity buildAttached(IEntitiesContainer container)
            throws GeneralDOAException;

    @Override
    public final void setAttributes(Map<String, String> attributes) {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            storedEntity.setAttributes(attributes);
        }
        this.attributes = attributes;
    }

    @Override
    public final boolean equals(IEntity entity) {
        if (entity == null) {
            return false;
        }
        return entity.getId() == getId();
    }

    @Override
    public final boolean hasEventListeners() {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.hasEventListeners();
        }
        // TODO !!!
        return false;
    }

    @Override
    public final List<IEntityEventListener> getEventListeners() {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.getEventListeners();
        }
        return null;
    }

    @Override
    public final boolean isPublic() {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.isPublic();
        }
        return false;
    }

    @Override
    public final IArtifact getArtifact() {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.getArtifact();
        }
        return null;
    }

    @Override
    public final Date getLastModified() {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.getLastModified();
        }
        return this.lastModified;
    }

    @Override
    public final Date getCreated() {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.getCreated();
        }
        return created;
    }

    @Override
    public final IEntity redeploy(IEntity newEntity) throws GeneralDOAException {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.redeploy(newEntity);
        }
        return null;
    }

    @Override
    public final IEntity getAncestor() {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.getAncestor();
        }
        return null;
    }

    @Override
    public final boolean isInside(IEntitiesContainer container) {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.isInside(container);
        }
        return false;
    }

    public void detach() {
        /**
         * TODO przepisac wartosci spowrotem z pola storedEntity
         */
        this.storedEntityId = -1;
    }

    public final IEntity getStoredEntity() {
        if (storedEntity != null) {
            return storedEntity;
        }
        if (storedEntityId < 0) {
            return null;
        }
        // TODO - zrobic ustawianie doa po deserializacji stanu uslugi
        if (doa == null) {
            return null;
        }
        this.storedEntity = doa.lookupByUUID(storedEntityId);
        return storedEntity;
    }

    public void setDoa(IDOA doa) {
        this.doa = doa;
    }

    @Override
    public boolean isDescendantOf(IEntity ancestor) {
        IEntity storedEntity = getStoredEntity();
        if (storedEntity != null) {
            return storedEntity.isDescendantOf(ancestor);
        }
        return AbstractEntity.isDescendantOf(ancestor, this);
    }

    @Override
    public <T extends IEntity> T attach(IEntityAttachRule<T> rule) {
        return rule.attachEntity();
    }

    @Override
    public final IStaticResource render(final String mimeType)
            throws GeneralDOAException {
        return AbstractEntity.render(this, mimeType, doa, null);
    }

    @Override
    public final IStaticResource render(IRenderer renderer)
            throws GeneralDOAException {
        return AbstractEntity.render(this, renderer, null);
    }

    @Override
    public final IStaticResource render(final String mimeType,
                                        IStaticResource template) throws GeneralDOAException {
        return AbstractEntity.render(this, mimeType, doa, template);
    }

    @Override
    public final IStaticResource render(IRenderer renderer,
                                        IStaticResource template) throws GeneralDOAException {
        return AbstractEntity.render(this, renderer, template);
    }

}

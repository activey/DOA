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
package pl.doa.entity.event.impl.neo;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.INeoObject;
import pl.doa.NeoEntityDelegator;
import pl.doa.artifact.IArtifact;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityAttribute;
import pl.doa.entity.IEntityProxy;
import pl.doa.entity.event.AbstractEntityEventListener;
import pl.doa.entity.event.EntityEventType;
import pl.doa.entity.event.IEntityEventListener;
import pl.doa.entity.event.IEntityEventReceiver;
import pl.doa.relation.DOARelationship;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author activey
 */
public class NeoEntityEventListener extends AbstractEntityEventListener
        implements INeoObject {

    private final static String PROP_EVENT_TYPE = "eventType";
    private final static Logger log = LoggerFactory
            .getLogger(NeoEntityEventListener.class);
    private final NeoEntityDelegator delegate;

    public NeoEntityEventListener(IDOA doa, Node underlyingNode) {
        super(doa);
        this.delegate = new NeoEntityDelegator(doa, underlyingNode);
    }

    public NeoEntityEventListener(IDOA doa, GraphDatabaseService neo,
                                  IEntity sourceEntity, IEntityEventReceiver receiver,
                                  EntityEventType eventType) {
        super(doa);
        this.delegate =
                new NeoEntityDelegator(doa, neo, this.getClass().getName());
        setName(delegate.getId() + "");
        setSourceEntity(sourceEntity);
        setEventReceiver(receiver);
        delegate.getNode().setProperty(PROP_EVENT_TYPE, eventType.name());
    }

    protected String getNameImpl() {
        return delegate.getName();
    }

    protected void setNameImpl(String name) {
        delegate.setName(name);
    }

    protected long getIdImpl() {
        return delegate.getId();
    }

    protected IEntitiesContainer getContainerImpl() {
        return delegate.getContainer();
    }

    protected void setContainerImpl(IEntitiesContainer container) throws GeneralDOAException {
        delegate.setContainer(container);
    }

    protected String getLocationImpl() {
        return delegate.getLocation();
    }

    @Override
    protected boolean hasAttributesImpl() {
        return delegate.hasAttributes();
    }

    protected Collection<String> getAttributeNamesImpl() {
        return delegate.getAttributeNames();
    }

    protected final String getAttributeImpl(String attrName) {
        return delegate.getAttribute(attrName);
    }

    protected final String getAttributeImpl(String attrName, String defaultValue) {
        return delegate.getAttribute(attrName, defaultValue);
    }

    protected IEntityAttribute getAttributeObjectImpl(String attrName) {
        return delegate.getAttributeObject(attrName);
    }

    protected final void setAttributeImpl(String attrName, String attrValue) {
        delegate.setAttribute(attrName, attrValue);
    }

    protected final void setAttributeImpl(IEntityAttribute attributte) {
        delegate.setAttribute(attributte);
    }

    protected void removeAttributesImpl() {
        delegate.removeAttributes();
    }

    protected boolean removeImpl(boolean forceRemoveContents) {
        return delegate.remove();
    }

    protected Date getCreatedImpl() {
        return delegate.getCreated();
    }

    protected boolean isStoredImpl() {
        return delegate.isStored();
    }

    protected IEntity storeImpl(String location) throws GeneralDOAException {
        return delegate.store(location);
    }

    protected Date getLastModifiedImpl() {
        return delegate.getLastModified();
    }

    protected boolean hasEventListenersImpl() {
        return delegate.hasEventListeners();
    }

    protected List<IEntityEventListener> getEventListenersImpl() {
        return delegate.getEventListeners();
    }

    protected IArtifact getArtifactImpl() {
        return delegate.getArtifact();
    }

    protected boolean isPublicImpl() {
        return delegate.isPublic();
    }

    protected void setAttributesImpl(Map<String, String> attributes) {
        delegate.setAttributes(attributes);
    }

    @Override
    protected IEntity getAncestorImpl() {
        return delegate.getAncestor();
    }

    @Override
    public Node getNode() {
        return this.delegate.getNode();
    }

    @Override
    protected IEntityEventReceiver getEventReceiverImpl() {
        if (!delegate.getNode().hasRelationship(DOARelationship.HAS_EVENT_RECEIVER,
                Direction.OUTGOING)) {
            return null;
        }
        Node receiverNode =
                delegate.getNode().getSingleRelationship(
                        DOARelationship.HAS_EVENT_RECEIVER, Direction.OUTGOING)
                        .getEndNode();
        if (receiverNode == null) {
            return null;
        }
        IEntity entity =
                NeoEntityDelegator.createEntityInstance(doa, receiverNode);
        if (entity instanceof IEntityEventReceiver) {
            return (IEntityEventReceiver) entity;
        }
        return null;
    }

    @Override
    protected void setEventReceiverImpl(IEntityEventReceiver eventReceiver) {
        IEntityEventReceiver receiver = eventReceiver;
        if (receiver == null) {
            if (delegate.getNode().hasRelationship(DOARelationship.HAS_EVENT_RECEIVER,
                    Direction.OUTGOING)) {
                Relationship rel =
                        delegate.getNode().getSingleRelationship(
                                DOARelationship.HAS_EVENT_RECEIVER,
                                Direction.OUTGOING);
                rel.delete();
                return;
            }
        }
        if (!(receiver instanceof IEntity)) {
            return;
        }
        if (receiver instanceof IEntityProxy) {
            IEntityProxy proxy = (IEntityProxy) receiver;
            receiver = (IEntityEventReceiver) proxy.get();
        }
        if (!(receiver instanceof INeoObject)) {
            log.warn("Unable to set receciver reference to object different than neo object!");
            return;
        }
        INeoObject neoObj = (INeoObject) receiver;
        delegate.getNode().createRelationshipTo(neoObj.getNode(),
                DOARelationship.HAS_EVENT_RECEIVER);
    }

    @Override
    protected IEntity getSourceEntityImpl() {
        if (!delegate.getNode().hasRelationship(
                DOARelationship.HAS_LISTENER_EVENT_SOURCE, Direction.OUTGOING)) {
            return null;
        }
        Relationship relation =
                delegate.getNode().getSingleRelationship(
                        DOARelationship.HAS_LISTENER_EVENT_SOURCE,
                        Direction.OUTGOING);
        return NeoEntityDelegator.createEntityInstance(doa,
                relation.getEndNode());
    }

    @Override
    protected void setSourceEntityImpl(IEntity sourceEntity) {
        if (sourceEntity == null) {
            if (delegate.getNode().hasRelationship(
                    DOARelationship.HAS_LISTENER_EVENT_SOURCE,
                    Direction.OUTGOING)) {
                Relationship rel =
                        delegate.getNode().getSingleRelationship(
                                DOARelationship.HAS_LISTENER_EVENT_SOURCE,
                                Direction.OUTGOING);
                rel.delete();
                return;
            }
        }

        if (!(sourceEntity instanceof IEntity)) {
            return;
        }
        if (sourceEntity instanceof IEntityProxy) {
            IEntityProxy proxy = (IEntityProxy) sourceEntity;
            sourceEntity = proxy.get();
        }
        if (!(sourceEntity instanceof INeoObject)) {
            log.warn("Unable to set souce entity"
                    + " reference to object different than neo object!");
            return;
        }
        INeoObject neoObj = (INeoObject) sourceEntity;
        delegate.getNode().createRelationshipTo(neoObj.getNode(),
                DOARelationship.HAS_LISTENER_EVENT_SOURCE);

    }

    @Override
    protected EntityEventType getEventTypeImpl() {
        if (!delegate.getNode().hasProperty(PROP_EVENT_TYPE)) {
            return null;
        }
        String eventType = (String) delegate.getNode().getProperty(PROP_EVENT_TYPE);
        return EntityEventType.valueOf(eventType);
    }

    @Override
    protected Iterable<String> getEventPropertyNamesImpl() {
        return delegate.getNode().getPropertyKeys();
    }

    @Override
    protected Object getEventPropertyImpl(String propertyName) {
        if (!delegate.getNode().hasProperty("event_" + propertyName)) {
            return null;
        }
        return delegate.getNode().getProperty("event_" + propertyName);
    }

    @Override
    protected void setEventPropertyImpl(String propertyName,
                                        Object propertyValue) {
        if (delegate.getNode().hasProperty("event_" + propertyName)
                && propertyValue == null) {
            delegate.getNode().removeProperty("event_" + propertyName);
            return;
        }
        delegate.getNode().setProperty("event_" + propertyName, propertyValue);
    }

    @Override
    protected String getStringPropertyImpl(String propertyName) {
        return (String) getEventPropertyImpl(propertyName);
    }

    @Override
    protected void setStringPropertyImpl(String propertyName,
                                         String propertyValue) {
        setEventPropertyImpl(propertyName, propertyValue);
    }

    @Override
    protected Integer getIntPropertyImpl(String propertyName) {
        return (Integer) getEventPropertyImpl(propertyName);
    }

    @Override
    protected void setIntPropertyImpl(String propertyName, int propertyValue) {
        setEventPropertyImpl(propertyName, propertyValue);
    }

    @Override
    protected IEntity getReferencePropertyImpl(String propertyName) {
        // TODO - zaimplementowac
        return null;
    }

    @Override
    protected void setReferencePropertyImpl(String propertyName,
                                            IEntity propertyValue) {
        // TODO - zaimplementowac
    }

}

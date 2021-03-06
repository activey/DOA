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
package pl.doa.container.impl.neo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.INeoObject;
import pl.doa.NeoEntityDelegator;
import pl.doa.artifact.IArtifact;
import pl.doa.container.IEntitiesContainer;
import pl.doa.container.IEntitiesIterator;
import pl.doa.container.impl.AbstractEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityAttribute;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.entity.event.IEntityEventListener;
import pl.doa.entity.impl.neo.NeoReturnableEvaluator;
import pl.doa.entity.sort.IEntitiesSortComparator;
import pl.doa.neo.utils.EntitiesComparator;
import pl.doa.neo.utils.EntitiesListIterable;
import pl.doa.relation.DOARelationship;
import pl.doa.utils.DynamicInteger;

/**
 * @author activey
 */
public class NeoEntitiesContainer extends AbstractEntitiesContainer implements
        INeoObject, Serializable {

    private final static Logger log = LoggerFactory
            .getLogger(NeoEntitiesContainer.class);

    private NeoEntityDelegator delegator = null;

    public NeoEntitiesContainer(IDOA doa, Node underlyingNode) {
        super(doa);
        this.delegator = new NeoEntityDelegator(doa, underlyingNode);
    }

    public NeoEntitiesContainer(IDOA doa, GraphDatabaseService neo, String name) {
        super(doa);
        this.delegator =
                new NeoEntityDelegator(doa, neo, this.getClass().getName());
        setName(name);
    }

    public NeoEntitiesContainer(IDOA doa, GraphDatabaseService neo) {
        super(doa);
        this.delegator =
                new NeoEntityDelegator(doa, neo, this.getClass().getName());
    }

    @Override
    protected Iterable<? extends IEntity> getEntitiesImpl(final int start,
                                                          final int howMany,
                                                          IEntitiesSortComparator<? extends IEntity> comparator,
                                                          final IEntityEvaluator evaluator, final boolean deep) {
        TraversalDescription travDesc =
                Traversal
                        .description()
                        .evaluator(Evaluators.excludeStartPosition())
                        .relationships(DOARelationship.HAS_ENTITY,
                                Direction.OUTGOING)
                        .depthFirst()
                        .evaluator(
                                (deep) ? Evaluators.fromDepth(1) : Evaluators
                                        .toDepth(1))
                        .evaluator(
                                new NeoReturnableEvaluator(doa, evaluator, deep))
                        .sort(new EntitiesComparator(getDoa(), comparator));
        Traverser nodesTraverser = travDesc.traverse(delegator);
        return new EntitiesListIterable(doa, nodesTraverser.iterator(), start,
                howMany);
    }

    @Override
    protected Iterable<? extends IEntity> getEntitiesImpl(final int start,
                                                          final int howMany,
                                                          IEntitiesSortComparator<? extends IEntity> comparator,
                                                          final IEntityEvaluator evaluator) {
        return getEntitiesImpl(start, howMany, comparator, evaluator, false);
    }

    protected Iterable<? extends IEntity> getEntitiesImpl() {
        return getEntitiesImpl(null);
    }

    @Override
    protected Iterable<? extends IEntity> getEntitiesImpl(
            final IEntityEvaluator evaluator) {
        return getEntitiesImpl(0, 0, null, evaluator);
    }

    protected int countEntitiesImpl() {
        final DynamicInteger dyn = new DynamicInteger();
        Iterable<Relationship> relations =
                delegator.getRelationships(DOARelationship.HAS_ENTITY,
                        Direction.OUTGOING);
        for (Relationship relationship : relations) {
            dyn.modify(1);
        }
        return dyn.getValue();
    }

    @Override
    protected int countEntitiesImpl(final IEntityEvaluator evaluator) {
        return countEntitiesImpl(evaluator, false);
    }

    @Override
    protected int countEntitiesImpl(final IEntityEvaluator evaluator,
                                    final boolean deep) {
        final DynamicInteger dyn = new DynamicInteger();
        TraversalDescription travDesc =
                Traversal
                        .description()
                        .evaluator(Evaluators.excludeStartPosition())
                        .relationships(DOARelationship.HAS_ENTITY,
                                Direction.OUTGOING)
                        .depthFirst()
                        .evaluator(
                                (deep) ? Evaluators.fromDepth(1) : Evaluators
                                        .toDepth(1))
                        .evaluator(
                                new NeoReturnableEvaluator(doa, evaluator, deep));
        Traverser nodesTraverser = travDesc.traverse(delegator);
        for (Path node : nodesTraverser) {
            dyn.modify(1);
        }
        return dyn.getValue();
    }

    @Override
    protected boolean hasEntityImpl(final String entityName) {
        IEntityEvaluator evaluator = new IEntityEvaluator() {

            @Override
            public boolean isReturnableEntity(IEntity entity) {
                return entityName.equals(entity.getName());
            }
        };
        IEntity existingEntity = lookupForEntity(evaluator, false);
        return existingEntity != null;
    }

    protected IEntity addEntityImpl(final IEntity doaEntity)
            throws GeneralDOAException {
        if (!(doaEntity instanceof INeoObject)) {
            throw new GeneralDOAException(
                    "Wrong entity type, must implements INeoObject!");
        }
        final INeoObject neoObject = (INeoObject) doaEntity;
        log.debug("Adding entity: " + doaEntity.getName());
        delegator.createRelationshipTo(neoObject.getNode(),
                DOARelationship.HAS_ENTITY);
        return doaEntity;
    }

    /*
     * TODO
     * @Override protected boolean internalRemove() { if
     * (this.hasRelationship(DOARelationship.HAS_ENTITY, Direction.OUTGOING)) {
     * return false; } return true; }
     */
    public boolean hasEntities() {
        return delegator.hasRelationship(DOARelationship.HAS_ENTITY,
                Direction.OUTGOING);
    }

    @Override
    protected void setAttributeImpl(String attrName, String attrValue) {
        delegator.setAttribute(attrName, attrValue);
    }

    @Override
    protected void setAttributeImpl(IEntityAttribute attributte) {
        delegator.setAttribute(attributte);
    }

    @Override
    protected void removeAttributesImpl() {
        delegator.removeAttributes();
    }

    @Override
    protected boolean removeImpl(boolean forceRemoveContents) {
        if (!forceRemoveContents
                && delegator.hasRelationship(DOARelationship.HAS_ENTITY,
                Direction.OUTGOING)) {
            return false;
        }
        if (forceRemoveContents) {
            purge(IEntityEvaluator.ALL);
        }
        return delegator.remove();
    }

    @Override
    protected boolean isStoredImpl() {
        return delegator.isStored();
    }

    @Override
    protected long getIdImpl() {
        return delegator.getId();
    }

    @Override
    protected String getNameImpl() {
        return delegator.getName();
    }

    @Override
    protected void setNameImpl(String name) {
        delegator.setName(name);
    }

    @Override
    protected IEntitiesContainer getContainerImpl() {
        return delegator.getContainer();
    }

    @Override
    protected String getLocationImpl() {
        return delegator.getLocation();
    }

    @Override
    protected boolean hasAttributesImpl() {
        return delegator.hasAttributes();
    }

    @Override
    protected List<String> getAttributeNamesImpl() {
        return delegator.getAttributeNames();
    }

    @Override
    protected String getAttributeImpl(String attrName) {
        return delegator.getAttribute(attrName);
    }

    @Override
    protected String getAttributeImpl(String attrName, String defaultValue) {
        return delegator.getAttribute(attrName, defaultValue);
    }

    @Override
    protected IEntityAttribute getAttributeObjectImpl(String attrName) {
        return delegator.getAttributeObject(attrName);
    }

    @Override
    protected IEntity storeImpl(String location) throws Throwable {
        return delegator.store(location);
    }

    @Override
    protected void setContainerImpl(IEntitiesContainer container) {
        delegator.setContainer(container);
    }

    @Override
    protected void setAttributesImpl(Map<String, String> attributes) {
        delegator.setAttributes(attributes);
    }

    @Override
    protected boolean hasEventListenersImpl() {
        return delegator.hasEventListeners();
    }

    @Override
    protected List<IEntityEventListener> getEventListenersImpl() {
        return delegator.getEventListeners();
    }

    @Override
    protected boolean isPublicImpl() {
        return delegator.isPublic();
    }

    @Override
    protected IArtifact getArtifactImpl() {
        return delegator.getArtifact();
    }

    @Override
    protected Date getLastModifiedImpl() {
        return delegator.getLastModified();
    }

    @Override
    protected Date getCreatedImpl() {
        return delegator.getCreated();
    }

    @Override
    public void iterateEntities(IEntitiesIterator iterator,
                                IEntityEvaluator evaluator) throws GeneralDOAException {
        Traverser traverser =
                Traversal
                        .description()
                        .evaluator(Evaluators.excludeStartPosition())
                        .breadthFirst()
                        .evaluator(
                                (evaluator != null) ? new NeoReturnableEvaluator(
                                        doa, evaluator, false) : Evaluators
                                        .all())
                        .evaluator(Evaluators.atDepth(1))
                        .relationships(DOARelationship.HAS_ENTITY,
                                Direction.OUTGOING).traverse(delegator);
        Iterable<IEntity> entities =
                new EntitiesListIterable(doa, traverser.iterator());
        for (IEntity entity : entities) {
            iterator.next(entity);
        }
    }

    @Override
    public void iterateEntities(IEntitiesIterator iterator)
            throws GeneralDOAException {
    }

    @Override
    protected IEntity redeployImpl(IEntity newEntity) throws Throwable {
        if (newEntity instanceof INeoObject) {
            delegator = (NeoEntityDelegator) delegator.redeploy(newEntity);
            return this;
        }
        throw new GeneralDOAException("Not INeoObject");
    }

    @Override
    protected IEntity getAncestorImpl() {
        return delegator.getAncestor();
    }

    public NeoEntityDelegator getNode() {
        return delegator;
    }

}
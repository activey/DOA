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
package pl.doa.neo;

import java.util.Date;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

import pl.doa.relation.DOARelationship;

/**
 * @author activey
 */
public class NodeDelegate implements Node {

    public static final String PROP_CLASS_NAME = "_class";

    public static final String PROP_LAST_MODIFIED = "lastModified";

    public static final String PROP_CREATED = "created";

    private final Node delegate;

    private boolean hasDelegate;

    public NodeDelegate() {
        delegate = null;
        this.hasDelegate = false;
    }

    public NodeDelegate(Node node) {
        this.delegate = node;
        this.hasDelegate = (node != null);
    }

    public NodeDelegate(GraphDatabaseService neo, String className) {
        this.delegate = neo.createNode();
        this.hasDelegate = true;
        setCreated();
        setClassName(className);
    }

    public NodeDelegate(GraphDatabaseService neo) {
        this.delegate = neo.createNode();
        this.hasDelegate = true;
        setCreated();
    }

    private void setCreated() {
        if (hasProperty(PROP_CREATED)) {
            return;
        }
        setProperty(PROP_CREATED, System.currentTimeMillis());
    }

    private void setClassName(String className) {
        if (hasProperty(PROP_CLASS_NAME)) {
            return;
        }
        setProperty(PROP_CLASS_NAME, className);
        notifyChanged();
    }

    public final Relationship createRelationshipTo(Node node,
                                                   RelationshipType relation) {
        Relationship rel = delegate.createRelationshipTo(node, relation);
        notifyChanged();
        return rel;
    }

    public void delete() {
        delegate.delete();
    }

    public final long getId() {
        return delegate.getId();
    }

    public Object getProperty(String arg0, Object arg1) {
        return delegate.getProperty(arg0, arg1);
    }

    public Object getProperty(String arg0) {
        if (!hasProperty(arg0)) {
            return null;
        }
        return delegate.getProperty(arg0);
    }

    public Iterable<String> getPropertyKeys() {
        return delegate.getPropertyKeys();
    }

    public Iterable<Object> getPropertyValues() {
        return delegate.getPropertyValues();
    }

    public Iterable<Relationship> getRelationships() {
        return delegate.getRelationships();
    }

    public Iterable<Relationship> getRelationships(Direction arg0) {
        return delegate.getRelationships(arg0);
    }

    public Iterable<Relationship> getRelationships(RelationshipType arg0,
                                                   Direction arg1) {
        return delegate.getRelationships(arg0, arg1);
    }

    public Iterable<Relationship> getRelationships(RelationshipType... arg0) {
        return delegate.getRelationships(arg0);
    }

    public Relationship getSingleRelationship(RelationshipType arg0,
                                              Direction arg1) {
        return delegate.getSingleRelationship(arg0, arg1);
    }

    public boolean hasProperty(String arg0) {
        return delegate.hasProperty(arg0);
    }

    public boolean hasRelationship() {
        return delegate.hasRelationship();
    }

    public boolean hasRelationship(Direction arg0) {
        return delegate.hasRelationship(arg0);
    }

    public boolean hasRelationship(RelationshipType arg0, Direction arg1) {
        return delegate.hasRelationship(arg0, arg1);
    }

    public boolean hasRelationship(RelationshipType... arg0) {
        return delegate.hasRelationship(arg0);
    }

    public Object removeProperty(String arg0) {
        Object removed = delegate.removeProperty(arg0);
        notifyChanged();
        return removed;
    }

    public void removeRelationship(RelationshipType arg0, Direction arg1) {
        for (Relationship relation : delegate.getRelationships(arg0, arg1)) {
            relation.delete();
        }
    }

    public void setProperty(String propertyName, Object propertyValue) {
        if (propertyValue == null) {
            if (hasProperty(propertyName)) {
                removeProperty(propertyName);
            }
            return;
        }
        delegate.setProperty(propertyName, propertyValue);
        notifyChanged();
    }

    public Traverser traverse(Order arg0, StopEvaluator arg1,
                              ReturnableEvaluator arg2, Object... arg3) {
        return delegate.traverse(arg0, arg1, arg2, arg3);
    }

    public Traverser traverse(Order arg0, StopEvaluator arg1,
                              ReturnableEvaluator arg2, RelationshipType arg3, Direction arg4,
                              RelationshipType arg5, Direction arg6) {
        return delegate.traverse(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
    }

    public Traverser traverse(Order arg0, StopEvaluator arg1,
                              ReturnableEvaluator arg2, RelationshipType arg3, Direction arg4) {
        return delegate.traverse(arg0, arg1, arg2, arg3, arg4);
    }

    public Node lookupForNode(RelationshipType relationType,
                              ReturnableEvaluator evaluator) {
        return lookupForNode(relationType, evaluator, StopEvaluator.DEPTH_ONE);
    }

    public Node lookupForNode(RelationshipType relationType,
                              ReturnableEvaluator evaluator, StopEvaluator stopEvaluator) {
        Traverser traverser =
                traverse(Traverser.Order.BREADTH_FIRST, stopEvaluator,
                        evaluator, relationType, Direction.OUTGOING);
        Node result = null;
        for (Node node : traverser) {
            result = node;
            break;
        }
        return result;
    }

    public boolean hasNode(DOARelationship relationType,
                           ReturnableEvaluator evaluator) {
        Traverser traverser =
                traverse(Traverser.Order.BREADTH_FIRST,
                        StopEvaluator.DEPTH_ONE, evaluator, relationType,
                        Direction.OUTGOING);
        return traverser.iterator().hasNext();
    }

    public boolean hasDelegate() {
        return hasDelegate;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NodeDelegate)) {
            return false;
        }
        NodeDelegate delegate = (NodeDelegate) obj;
        return delegate.getId() == getId();
    }

    private void notifyChanged() {
        if (!hasDelegate) {
            return;
        }
        delegate.setProperty(PROP_LAST_MODIFIED, System.currentTimeMillis());
    }

    public final Date getCreated() {
        if (!hasProperty(PROP_CREATED)) {
            return null;
        }
        Long created = (Long) getProperty(PROP_CREATED);
        if (created != null) {
            return new Date(created);
        }
        return new Date();
    }

    public final Date getLastModified() {
        if (!hasProperty(PROP_LAST_MODIFIED)) {
            return null;
        }
        Long lastModified = (Long) getProperty(PROP_LAST_MODIFIED);
        if (lastModified != null) {
            return new Date(lastModified);
        }
        return new Date();
    }

    @Override
    public GraphDatabaseService getGraphDatabase() {
        return delegate.getGraphDatabase();
    }

    @Override
    public Iterable<Relationship> getRelationships(Direction direction,
                                                   RelationshipType... type) {
        return delegate.getRelationships(direction, type);
    }

    @Override
    public boolean hasRelationship(Direction direction,
                                   RelationshipType... type) {
        return delegate.hasRelationship(direction, type);
    }

}

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
package pl.doa.service.impl.neo;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.Traverser.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.INeoObject;
import pl.doa.NeoEntityDelegator;
import pl.doa.artifact.IArtifact;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.impl.neo.NeoDocumentDefinition;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityAttribute;
import pl.doa.entity.event.IEntityEventListener;
import pl.doa.relation.DOARelationship;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.service.impl.AbstractServiceDefinition;

import java.io.Serializable;
import java.util.*;

/**
 * @author activey
 */
public class NeoServiceDefinition extends AbstractServiceDefinition implements
        INeoObject, IServiceDefinition, Serializable {

    private final static Logger log = LoggerFactory
            .getLogger(NeoServiceDefinition.class);
    private static final String PROP_LOGIC_CLASS = "logicClass";
    private NeoEntityDelegator delegator = null;

    public NeoServiceDefinition(IDOA doa, Node underlyingNode) {
        super(doa);
        this.delegator = new NeoEntityDelegator(doa, underlyingNode);
    }

    public NeoServiceDefinition(IDOA doa, GraphDatabaseService neo, String name) {
        super(doa);
        this.delegator =
                new NeoEntityDelegator(doa, neo, this.getClass().getName());
        setName(name);
    }

    public NeoServiceDefinition(IDOA doa, GraphDatabaseService neo,
                                String name, String logicClass) {
        super(doa);
        this.delegator =
                new NeoEntityDelegator(doa, neo, this.getClass().getName());
        setName(name);
        setLogicClass(logicClass);
    }

    public NeoServiceDefinition(IDOA doa, GraphDatabaseService neo,
                                String name, IEntity ancestor) {
        super(doa);
        this.delegator =
                new NeoEntityDelegator(doa, neo, this.getClass().getName(),
                        ancestor);
        setName(name);
    }

    /*
     * (non-Javadoc)
     * @see pl.doa.service.impl.neo.IServiceDefinition#getInputDefinition()
     */
    @Override
    protected NeoDocumentDefinition getInputDefinitionImpl() {
        if (!delegator.getNode().hasRelationship(DOARelationship.HAS_INPUT_DEFINITION,
                Direction.OUTGOING)) {
            return null;
        }
        Node inputNode =
                delegator.getNode().getSingleRelationship(
                        DOARelationship.HAS_INPUT_DEFINITION,
                        Direction.OUTGOING).getEndNode();
        if (inputNode == null) {
            return null;
        }
        return new NeoDocumentDefinition(getDoa(), inputNode);
    }

    /*
     * (non-Javadoc)
     * @see
     * pl.doa.service.impl.neo.IServiceDefinition#setInputDefinition(pl.doa.
     * document.IDocumentDefinition)
     */
    @Override
    protected void setInputDefinitionImpl(IDocumentDefinition inputDefinition) {
        INeoObject neoEntity = (INeoObject) inputDefinition;
        if (delegator.getNode().hasRelationship(DOARelationship.HAS_INPUT_DEFINITION,
                Direction.OUTGOING)) {
            for (Relationship relation : delegator.getNode().getRelationships(
                    DOARelationship.HAS_INPUT_DEFINITION, Direction.OUTGOING)) {
                relation.delete();
            }
            if (inputDefinition == null) {
                return;
            }
        }
        if (inputDefinition == null) {
            return;
        }
        delegator.getNode().createRelationshipTo(neoEntity.getNode(),
                DOARelationship.HAS_INPUT_DEFINITION);
    }

    /*
     * (non-Javadoc)
     * @see pl.doa.service.impl.neo.IServiceDefinition#getPossibleOutputs()
     */
    @Override
    protected List<IDocumentDefinition> getPossibleOutputsImpl() {
        List<IDocumentDefinition> outputs =
                new ArrayList<IDocumentDefinition>();
        Traverser traverser =
                delegator.getNode().traverse(Traverser.Order.BREADTH_FIRST,
                        StopEvaluator.DEPTH_ONE,
                        ReturnableEvaluator.ALL_BUT_START_NODE,
                        DOARelationship.HAS_OUTPUT_DEFINITION,
                        Direction.OUTGOING);
        for (Node node : traverser) {
            outputs.add(new NeoDocumentDefinition(getDoa(), node));
        }
        return outputs;
    }

    /*
     * (non-Javadoc)
     * @see
     * pl.doa.service.impl.neo.IServiceDefinition#getPossibleOutputDefinition
     * (java.lang.String)
     */
    @Override
    protected IDocumentDefinition getPossibleOutputDefinitionImpl(
            final String possibleOutputName) {

        Node possibleOutputNode =
                delegator.lookupForNode(DOARelationship.HAS_OUTPUT_DEFINITION,
                        new ReturnableEvaluator() {

                            @Override
                            public boolean isReturnableNode(
                                    TraversalPosition position) {
                                Node currentNode = position.currentNode();
                                if (currentNode.hasProperty("name")
                                        && possibleOutputName
                                        .equals(currentNode
                                                .getProperty("name"))) {
                                    return true;
                                }
                                return false;
                            }

                        });
        if (possibleOutputNode == null) {
            return null;
        }
        return (IDocumentDefinition) NeoEntityDelegator.createEntityInstance(
                getDoa(), possibleOutputNode);
    }

    /*
     * (non-Javadoc)
     * @see
     * pl.doa.service.impl.neo.IServiceDefinition#addPossibleOutputDefinition
     * (pl.doa.document.impl.neo.NeoDocumentDefinition)
     */
    @Override
    protected void addPossibleOutputDefinitionImpl(
            IDocumentDefinition possibleOutputDefinition) {
        if (possibleOutputDefinition == null) {
            return;
        }
        INeoObject neoEntity = (INeoObject) possibleOutputDefinition;
        delegator.getNode().createRelationshipTo(neoEntity.getNode(),
                DOARelationship.HAS_OUTPUT_DEFINITION);
    }

    /*
     * (non-Javadoc)
     * @see pl.doa.service.impl.neo.IServiceDefinition#getLogicClass()
     */
    @Override
    protected String getLogicClassImpl() {
        String classString = (String) delegator.getNode().getProperty(PROP_LOGIC_CLASS);
        return classString;
    }

    /*
     * (non-Javadoc)
     * @see
     * pl.doa.service.impl.neo.IServiceDefinition#setLogicClass(java.lang.String
     * )
     */
    @Override
    protected void setLogicClassImpl(String logicClass) {
        if (logicClass == null && delegator.getNode().hasProperty(logicClass)) {
            delegator.getNode().removeProperty(PROP_LOGIC_CLASS);
            return;
        }
        delegator.getNode().setProperty(PROP_LOGIC_CLASS, logicClass);
    }

    /*
     * (non-Javadoc)
     * @see pl.doa.service.impl.neo.IServiceDefinition#getRunningServices()
     */
    @Override
    protected List<IRunningService> getRunningServicesImpl() {
        Traverser traverser =
                delegator
                        .getNode().traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE,
                        ReturnableEvaluator.ALL_BUT_START_NODE,
                        DOARelationship.HAS_RUNNING_SERVICE,
                        Direction.OUTGOING);
        Collection<Node> runningNodes = traverser.getAllNodes();
        if (runningNodes.isEmpty()) {
            return null;
        }
        List<IRunningService> runningServices =
                new ArrayList<IRunningService>();
        for (Node node : runningNodes) {
            NeoRunningService runningService = new NeoRunningService(getDoa(), node);
            runningServices.add(runningService);
        }
        return runningServices;
    }

    /*
     * (non-Javadoc)
     * @see
     * pl.doa.service.impl.neo.IServiceDefinition#addRunning(pl.doa.service.
     * impl.neo.NeoRunningService)
     */
    @Override
    protected void addRunningImpl(IRunningService runningService) {
        INeoObject neoEntity = (INeoObject) runningService;
        delegator.getNode().createRelationshipTo(neoEntity.getNode(),
                DOARelationship.HAS_RUNNING_SERVICE);
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
        if (delegator.getNode().hasRelationship(DOARelationship.HAS_RUNNING_SERVICE)) {
            return false;
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
    protected void setContainerImpl(IEntitiesContainer container) throws GeneralDOAException {
        delegator.setContainer(container);
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
    protected Collection<String> getAttributeNamesImpl() {
        return delegator.getAttributeNames();
    }

    @Override
    protected String getAttributeImpl(String attrName) {
        return delegator.getAttribute(attrName);
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
    protected IEntity getAncestorImpl() {
        return delegator.getAncestor();
    }

    @Override
    public Node getNode() {
        return delegator.getNode();
    }

    @Override
    protected void removePossibleOutputDefinitionImpl(
            IDocumentDefinition possibleOutputDefinition) {
        INeoObject neoEntity = (INeoObject) possibleOutputDefinition;
        if (neoEntity == null) {
            return;
        }
        Node node = neoEntity.getNode();
        if (node == null) {
            return;
        }
        for (Relationship relation : delegator
                .getNode().getRelationships(DOARelationship.HAS_OUTPUT_DEFINITION)) {
            if (node.getId() == relation.getEndNode().getId()) {
                relation.delete();
                break;
            }
        }
    }

}
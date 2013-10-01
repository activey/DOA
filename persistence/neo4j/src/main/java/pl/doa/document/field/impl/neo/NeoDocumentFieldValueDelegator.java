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
package pl.doa.document.field.impl.neo;

import java.io.Serializable;
import java.lang.reflect.Constructor;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.IDOA;
import pl.doa.NeoEntityDelegator;
import pl.doa.document.IDocument;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.neo.NodeDelegate;
import pl.doa.relation.DOARelationship;

/**
 * @author activey
 */
public final class NeoDocumentFieldValueDelegator extends NodeDelegate
        implements Serializable {

    private final static Logger log = LoggerFactory
            .getLogger(NeoDocumentFieldValueDelegator.class);

    public static final String PROP_VALUE = "value";
    public static final String PROP_NAME = "name";

    protected IDOA doa;

    protected GraphDatabaseService neo;

    public NeoDocumentFieldValueDelegator(IDOA doa, GraphDatabaseService neo,
                                          String className) {
        super(neo, className);
        this.doa = doa;
        this.neo = neo;
    }

    public NeoDocumentFieldValueDelegator(IDOA doa, Node node) {
        super(node);
        this.doa = doa;
        this.neo = node.getGraphDatabase();
    }

    /*
     * (non-Javadoc)
     * @see pl.doa.document.field.IDocumentFieldValue#getFieldType()
     */
    public IDocumentFieldType getFieldType() {
        // sprawdzanie, czy pole jest podpiete bezposrednio pod dokument
        if (!hasRelationship(DOARelationship.HAS_FIELD, Direction.INCOMING)) {
            return null;
        }
        Relationship relation =
                getSingleRelationship(DOARelationship.HAS_FIELD,
                        Direction.INCOMING);
        Node documentNode = relation.getStartNode();
        if (documentNode != null) {
            IDocument document =
                    (IDocument) NeoEntityDelegator.createEntityInstance(doa,
                            documentNode);
            if (document != null) {
                return document.getDefinition().getFieldType(getFieldName());
            }
        }

        // pobieranie definicji z referencji HAS_DEFINITION
        if (!hasRelationship(DOARelationship.HAS_FIELD_DEFINITION,
                Direction.OUTGOING)) {
            return null;
        }
        relation =
                getSingleRelationship(DOARelationship.HAS_FIELD_DEFINITION,
                        Direction.OUTGOING);
        Node definitionNode = relation.getEndNode();
        if (definitionNode != null) {
            NeoDocumentFieldType fieldType =
                    new NeoDocumentFieldType(doa, definitionNode);
            return fieldType;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see pl.doa.document.field.IDocumentFieldValue#getFieldName()
     */
    public String getFieldName() {
        return (String) getProperty(PROP_NAME);
    }

    /*
     * (non-Javadoc)
     * @see
     * pl.doa.document.field.IDocumentFieldValue#setFieldName(java.lang.String)
     */
    public void setFieldName(String fieldName) {
        setProperty(PROP_NAME, fieldName);
    }

    public static IDocumentFieldValue createFieldValueInstance(IDOA doa,
                                                               Node node) {
        if (!(node.hasProperty(NodeDelegate.PROP_CLASS_NAME))) {
            return null;
        }
        String className =
                (String) node.getProperty(NodeDelegate.PROP_CLASS_NAME);
        if (className == null) {
            return null;
        }
        Class<? extends IDocumentFieldValue> clazz;
        try {
            clazz =
                    (Class<? extends IDocumentFieldValue>) Class
                            .forName(className);
        } catch (Throwable t) {
            log.error("", t);
            return null;
        }
        try {
            Constructor<? extends IDocumentFieldValue> constructor =
                    clazz.getConstructor(IDOA.class, Node.class);
            return constructor.newInstance(doa, node);
        } catch (Throwable e) {
            log.error("", e);
            return null;
        }
    }

}

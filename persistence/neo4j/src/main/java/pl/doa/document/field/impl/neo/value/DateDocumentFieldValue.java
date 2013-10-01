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
package pl.doa.document.field.impl.neo.value;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.INeoObject;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.impl.AbstractDateDocumentFieldValue;
import pl.doa.document.field.impl.neo.NeoDocumentFieldValueDelegator;

/**
 * @author activey
 */
public class DateDocumentFieldValue extends AbstractDateDocumentFieldValue
        implements INeoObject {

    private final static Logger log = LoggerFactory
            .getLogger(DateDocumentFieldValue.class);
    private NeoDocumentFieldValueDelegator delegator;

    public DateDocumentFieldValue(IDOA doa, GraphDatabaseService neo) {
        this.delegator =
                new NeoDocumentFieldValueDelegator(doa, neo,
                        DateDocumentFieldValue.class.getName());
    }

    public DateDocumentFieldValue(IDOA doa, Node node) {
        this.delegator = new NeoDocumentFieldValueDelegator(doa, node);
    }

    @Override
    public Date getFieldValueImpl() {
        if (!delegator.hasProperty(NeoDocumentFieldValueDelegator.PROP_NAME)) {
            return null;
        }
        Long timestamp =
                (Long) delegator
                        .getProperty(NeoDocumentFieldValueDelegator.PROP_VALUE);
        if (timestamp == null) {
            return null;
        }
        return new Date(timestamp);
    }

    public void setFieldValueImpl(Date fieldValue) throws GeneralDOAException {
        if (fieldValue == null) {
            delegator.removeProperty(NeoDocumentFieldValueDelegator.PROP_VALUE);
            return;
        }
        delegator.setProperty(NeoDocumentFieldValueDelegator.PROP_VALUE,
                fieldValue.getTime());
    }

    @Override
    public int compareToImpl(Date fieldValue) {
        return ((Date) delegator
                .getProperty(NeoDocumentFieldValueDelegator.PROP_VALUE))
                .compareTo(fieldValue);
    }

    @Override
    public void copyFromImpl(IDocumentFieldValue otherField) {
        IDocumentFieldValue dateField = (IDocumentFieldValue) otherField;
        Date date = (Date) dateField.getFieldValue();
        if (date == null) {
            return;
        }
        delegator.setProperty(NeoDocumentFieldValueDelegator.PROP_VALUE,
                date.getTime());
    }

    public IDocumentFieldType getFieldTypeImpl() {
        return delegator.getFieldType();
    }

    public String getFieldNameImpl() {
        return delegator.getFieldName();
    }

    public void setFieldNameImpl(String fieldName) {
        delegator.setFieldName(fieldName);
    }

    @Override
    public Node getNode() {
        return delegator;
    }

    @Override
    public boolean isEmpty() {
        return delegator.hasProperty(NeoDocumentFieldValueDelegator.PROP_VALUE);
    }

    @Override
    public void remove() {
        delegator.delete();
    }

}

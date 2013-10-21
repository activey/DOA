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
package pl.doa;

import org.apache.commons.configuration.Configuration;
import pl.doa.artifact.impl.StandaloneArtifactManager;
import pl.doa.artifact.impl.StandaloneArtifactManagerOld;
import pl.doa.entity.IEntityAttribute;
import pl.doa.entity.startable.impl.StandaloneStartableEntityManager;
import pl.doa.impl.AbstractBootstrapDOA;
import pl.doa.resource.impl.SimpleStaticResourceStorage;
import pl.doa.service.impl.StandaloneServicesManager;
import pl.doa.thread.impl.SimpleThreadManager;

import java.util.*;

/**
 * @author activey
 */
public class StandaloneDOA extends AbstractBootstrapDOA {

    public static final String DOA_LOGIC = "doa.logic";
    private String logicClass;
    private Map<String, String> attributes = new HashMap<String, String>();
    private String name;


    public StandaloneDOA(Configuration configuration) {
        setServicesManager(new StandaloneServicesManager(this));
        setArtifactManager(new StandaloneArtifactManager(this, configuration));
        setResourceStorage(new SimpleStaticResourceStorage(configuration));
        setStartableManager(new StandaloneStartableEntityManager(this));
        setThreadManager(new SimpleThreadManager(configuration));

        setLogicClass(configuration.getString(DOA_LOGIC));
        Iterator<String> keys = configuration.getKeys(DOA_LOGIC);
        while (keys.hasNext()) {
            String key = keys.next();
            if (key.length() == DOA_LOGIC.length()) {
                continue;
            }
            String logicAttrName = key.substring(DOA_LOGIC.length() + 1);
            setAttribute(logicAttrName, configuration.getString(key));
        }
    }

    @Override
    protected final String getNameImpl() {
        return this.name;
    }

    @Override
    protected final void setNameImpl(String name) {
        this.name = name;
    }

    @Override
    protected String getAttributeImpl(String attrName) {
        IEntityAttribute attr = getAttributeObjectImpl(attrName);
        if (attr == null) {
            return null;
        }
        return attr.getValueAsString();
    }

    @Override
    protected final boolean hasAttributesImpl() {
        return attributes != null && !attributes.isEmpty();
    }

    @Override
    protected Collection<String> getAttributeNamesImpl() {
        return new ArrayList<String>(attributes.keySet());
    }

    @Override
    protected final IEntityAttribute getAttributeObjectImpl(final String attrName) {
        final String attrValue = attributes.get(attrName);
        if (attrValue == null) {
            return null;
        }
        IEntityAttribute attr = new IEntityAttribute() {

            @Override
            public boolean remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getValueAsString() {
                Object value = getValue();
                if (value == null) {
                    return "";
                }
                return value.toString();
            }

            @Override
            public Object getValue() {
                return attrValue;
            }

            @Override
            public void setValue(Object value) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getName() {
                return attrName;
            }

            @Override
            public void setName(String name) {
                throw new UnsupportedOperationException();
            }
        };
        return attr;
    }

    @Override
    protected final void setAttributeImpl(String attrName, String attrValue) {
        attributes.put(attrName, attrValue);
    }

    protected final void setAttributeImpl(IEntityAttribute attributte) {
        attributes.put(attributte.getName(), attributte.getValue().toString());
    }

    @Override
    protected final void removeAttributesImpl() {
        attributes.clear();
    }

    @Override
    protected final void setAttributesImpl(Map<String, String> attributes) {
        this.attributes.putAll(attributes);
    }

    @Override
    protected final String getLogicClassImpl() {
        return this.logicClass;
    }

    @Override
    protected void setLogicClassImpl(String logicClass) {
        this.logicClass = logicClass;
    }
}

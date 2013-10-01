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
package pl.doa.artifact.tag;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import nu.xom.Nodes;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.templates.tags.AttributesSupportTag;
import pl.doa.templates.tags.Tag;

public class FieldTypeTag extends Tag implements AttributesSupportTag {

    private final static Logger log = LoggerFactory
            .getLogger(FieldTypeTag.class);

    private String name;
    private String dataType;
    private String var;
    private boolean required = false;
    private boolean authorizable = false;

    private IDocumentFieldType newField;

    private Map<String, Object> attributes = new HashMap<String, Object>();

    @Override
    public void processTagStart() throws Exception {
        Tag parent = getParent();
        if (parent != null && parent instanceof DocumentDefinitionTag) {
            DocumentDefinitionTag definition = (DocumentDefinitionTag) parent;
            this.newField =
                    definition.add(name,
                            DocumentFieldDataType.valueOf(dataType), required,
                            authorizable);

            for (Map.Entry<String, Object> attr : attributes.entrySet()) {
                String attrName = attr.getKey();
                Object attrValue = attr.getValue();
                try {
                    BeanUtils.setProperty(newField, attrName, attrValue);
                } catch (Exception e) {
                    log.error(MessageFormat.format(
                            "Error while setting attribute value: [{0}]",
                            attrName), e);
                    continue;
                }
            }
        }
        if (getVar() != null) {
            context.setVariable(getVar(), this.newField);
        }
    }

    @Override
    public Nodes processTagEnd() throws Exception {
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isAuthorizable() {
        return authorizable;
    }

    public void setAuthorizable(boolean authorizable) {
        this.authorizable = authorizable;
    }

    @Override
    public void setAttribute(String attrName, String attrValue) {
        attributes.put(attrName, attrValue);
    }

}

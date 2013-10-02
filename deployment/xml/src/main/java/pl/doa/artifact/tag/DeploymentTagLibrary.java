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

import org.apache.commons.beanutils.ConvertUtils;
import pl.doa.artifact.deploy.IDeploymentProcessor;
import pl.doa.artifact.tag.convert.*;
import pl.doa.artifact.tag.field.impl.*;
import pl.doa.artifact.tag.processor.DeployProcessorTag;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.entity.IEntity;
import pl.doa.entity.event.EntityEventType;
import pl.doa.templates.tags.TagLibrary;

import java.io.InputStream;

public class DeploymentTagLibrary extends TagLibrary {

    @Override
    protected void initializeTagLibrary() throws Exception {
        ConvertUtils.register(new EntityConverter(templateContext),
                IDocumentDefinition.class);
        ConvertUtils.register(new EntityConverter(templateContext),
                IEntitiesContainer.class);
        ConvertUtils.register(new EntityConverter(templateContext),
                IEntity.class);
        ConvertUtils.register(new FileConverter(templateContext),
                InputStream.class);
        ConvertUtils.register(new FieldDataTypeConverter(templateContext),
                DocumentFieldDataType.class);
        ConvertUtils.register(new EntityEventTypeConverter(templateContext),
                EntityEventType.class);
        ConvertUtils.register(new TypeConverter(templateContext),
                IDeploymentProcessor.class);

        setNamespace("http://doaplatform.org/deploy");

        registerTag("deploy", DeployTag.class);
        registerTag("agent", AgentTag.class);
        registerTag("attr", AttributeTag.class);
        registerTag("channel", ChannelTag.class);
        registerTag("eventListener", EventListenerTag.class);
        registerTag("container", EntitiesContainerTag.class);
        registerTag("service", ServiceDefinitionTag.class);
        registerTag("possibleOutput", ServiceDefinitionPossibleOutputTag.class);
        registerTag("resource", StaticResourceTag.class);
        registerTag("definition", DocumentDefinitionTag.class);
        registerTag("document", DocumentTag.class);
        registerTag("documentAligner", DocumentAlignerTag.class);
        registerTag("renderer", RendererTag.class);
        registerTag("reference", ReferenceTag.class);
        registerTag("doa", DOATag.class);
        registerTag("lookup", LookupTag.class);
        registerTag("load", LoadTag.class);

        registerTag("fieldType", FieldTypeTag.class);
        registerTag("fieldValue", FieldValueTag.class);
        registerTag("listValue", FieldValueTag.class);

        registerTag("stringField", StringFieldTypeTag.class);
        registerTag("booleanField", BooleanFieldTypeTag.class);
        registerTag("doubleField", DoubleFieldTypeTag.class);
        registerTag("integerField", IntegerFieldTypeTag.class);
        registerTag("longField", LongFieldTypeTag.class);
        registerTag("bigdecimalField", BigdecimalFieldTypeTag.class);
        registerTag("dateField", DateFieldTypeTag.class);
        registerTag("passwordField", PasswordFieldTypeTag.class);
        registerTag("referenceField", ReferenceFieldTypeTag.class);
        registerTag("listField", ListFieldTypeTag.class);

        registerTag("deploy-processor", DeployProcessorTag.class);
    }

}

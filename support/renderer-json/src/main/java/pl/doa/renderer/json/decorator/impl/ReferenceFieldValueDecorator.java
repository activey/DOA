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
package pl.doa.renderer.json.decorator.impl;

import org.json.JSONException;
import org.json.JSONObject;

import pl.doa.GeneralDOAException;
import pl.doa.agent.IAgent;
import pl.doa.artifact.IArtifact;
import pl.doa.channel.IChannel;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.alignment.IDocumentAligner;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityReference;
import pl.doa.entity.event.IEntityEventListener;
import pl.doa.renderer.IRenderer;
import pl.doa.renderer.json.decorator.SimpleFieldValueDecorator;
import pl.doa.resource.IStaticResource;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;

/**
 * @author activey
 * 
 */
public class ReferenceFieldValueDecorator extends SimpleFieldValueDecorator {

	@Override
	public JSONObject decorateField(IDocumentFieldValue simpleField)
			throws GeneralDOAException {
		JSONObject fieldObject = new JSONObject();
		try {
			IEntity entity = (IEntity) simpleField.getFieldValue();
			fieldObject.put("type", simpleField.getFieldType()
					.getFieldDataType().toString());
			fieldObject.put("value", entity.getLocation());
			fieldObject.put("referenceType", buildReferenceType(entity));

			if (entity instanceof IEntitiesContainer) {
				IEntitiesContainer container = (IEntitiesContainer) entity;
				fieldObject.put("entitiesCount", container.countEntities());
			}
		} catch (JSONException e) {
			throw new GeneralDOAException(e);
		}
		return fieldObject;
	}

	private String buildReferenceType(IEntity entity) {
		if (entity instanceof IAgent) {
			return IAgent.class.getName();
		} else if (entity instanceof IArtifact) {
			return IArtifact.class.getName();
		} else if (entity instanceof IChannel) {
			return IChannel.class.getName();
		} else if (entity instanceof IEntitiesContainer) {
			return IEntitiesContainer.class.getName();
		} else if (entity instanceof IDocument) {
			return IDocument.class.getName();
		} else if (entity instanceof IDocumentDefinition) {
			return IDocumentDefinition.class.getName();
		} else if (entity instanceof IDocumentAligner) {
			return IDocumentAligner.class.getName();
		} else if (entity instanceof IRenderer) {
			return IRenderer.class.getName();
		} else if (entity instanceof IEntityReference) {
			return IEntityReference.class.getName();
		} else if (entity instanceof IStaticResource) {
			return IStaticResource.class.getName();
		} else if (entity instanceof IServiceDefinition) {
			return IServiceDefinition.class.getName();
		} else if (entity instanceof IRunningService) {
			return IRunningService.class.getName();
		} else if (entity instanceof IEntityEventListener) {
			return IEntityEventListener.class.getName();
		}
		return null;
	}
}

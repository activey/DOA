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
package pl.doa.renderer.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.agent.IAgent;
import pl.doa.artifact.IArtifact;
import pl.doa.channel.IChannel;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.alignment.IDocumentAligner;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityReference;
import pl.doa.entity.event.IEntityEventListener;
import pl.doa.entity.event.IEntityEventReceiver;
import pl.doa.renderer.AbstractTemplateRendererLogic;
import pl.doa.renderer.IRenderer;
import pl.doa.renderer.IRenderingContext;
import pl.doa.renderer.json.decorator.JSONFieldValueDecoratorFactory;
import pl.doa.resource.IStaticResource;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.utils.IteratorIterable;

/**
 * @author activey
 * 
 */
public class JSONRendererLogic extends AbstractTemplateRendererLogic implements
		JSONConstants {

	private final static Logger log = LoggerFactory
			.getLogger(JSONRendererLogic.class);

	public void startup() throws pl.doa.GeneralDOAException {
	}

	public void shutdown() throws pl.doa.GeneralDOAException {

	}

	public boolean isStartedUp() {
		return false;
	}

	@Override
	protected long renderEntityImpl(IEntity entity, OutputStream output,
			IStaticResource template, IRenderingContext renderingContext)
			throws GeneralDOAException {
		JSONObjectWrapper renderedObject = new JSONObjectWrapper();
		if (template == null || skipTemplates(renderingContext)) {
			renderEntityGeneric(renderedObject, entity);
		} else {
			Context context = ContextFactory.getGlobal().enterContext();
			Scriptable scope = context.initStandardObjects();
			scope.put("entity", scope, entity);
			scope.put("json", scope, renderedObject);
			InputStream templateStream = template.getContentStream();
			Reader reader = new InputStreamReader(templateStream);
			try {
				context.evaluateReader(scope, reader, "script", 1, null);
			} catch (IOException e) {
				throw new GeneralDOAException(e);
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					throw new GeneralDOAException(e);
				}
			}
		}
		String jsonString = renderedObject.toString();
		PrintStream stream = new PrintStream(output);
		try {
			stream.print(jsonString);
		} catch (Exception e) {
			throw new GeneralDOAException(e);
		}
		return jsonString.getBytes().length;
	}

	private void renderEntityGeneric(JSONObject renderedObject, IEntity entity)
			throws GeneralDOAException {
		try {
			if (entity instanceof IAgent) {
				renderAgent((IAgent) entity, renderedObject);
			} else if (entity instanceof IArtifact) {
				renderArtifact((IArtifact) entity, renderedObject);
			} else if (entity instanceof IChannel) {
				renderChannel((IChannel) entity, renderedObject);
			} else if (entity instanceof IEntitiesContainer) {
				renderEntitiesContainer((IEntitiesContainer) entity,
						renderedObject);
			} else if (entity instanceof IRenderer) {
				renderRenderer((IRenderer) entity, renderedObject);
			} else if (entity instanceof IRunningService) {
				renderRunningService((IRunningService) entity, renderedObject);
			} else if (entity instanceof IServiceDefinition) {
				renderServiceDefinition((IServiceDefinition) entity,
						renderedObject);
			} else if (entity instanceof IStaticResource) {
				renderStaticResource((IStaticResource) entity, renderedObject);
			} else if (entity instanceof IDocumentAligner) {
				renderDocumentAligner((IDocumentAligner) entity, renderedObject);
			} else if (entity instanceof IDocument) {
				renderDocument((IDocument) entity, renderedObject);
			} else if (entity instanceof IDocumentDefinition) {
				renderDocumentDefinition((IDocumentDefinition) entity,
						renderedObject);
			} else if (entity instanceof IEntityReference) {
				renderEntityReference((IEntityReference) entity, renderedObject);
			} else if (entity instanceof IEntityEventListener) {
				renderEntityListener((IEntityEventListener) entity,
						renderedObject);
			}

			renderedObject.put("id", entity.getId());
			renderedObject.put("name", entity.getName());
			renderedObject.put("created", entity.getCreated().getTime());
			renderedObject.put("lastModified", entity.getLastModified()
					.getTime());
			IEntity ancestor = entity.getAncestor();
			if (ancestor != null) {
				renderedObject.put("ancestor", ancestor.getId());
			}
			IEntitiesContainer container = entity.getContainer();
			if (ancestor != null) {
				renderedObject.put("container", container.getId());
			}
			renderedObject.put("location", entity.getLocation());
			JSONObject attributes = new JSONObject();
			Collection<String> attrNames = entity.getAttributeNames();
			for (String attrName : attrNames) {
				Object attrValue = entity.getAttribute(attrName);
				attributes.put(attrName, attrValue);
			}
			renderedObject.put("attrs", attributes);
		} catch (JSONException e) {
			throw new GeneralDOAException(e);
		}
	}

	private void renderDocument(IDocument entity, JSONObject renderedObject)
			throws JSONException {
		JSONFieldValueDecoratorFactory factory = new JSONFieldValueDecoratorFactory();
		renderedObject.put("type", IDocument.class.getName());
		renderedObject.put("definition", entity.getDefinition().getLocation());
		renderedObject.put("definitionId", entity.getDefinition().getId());
		JSONObject fields = new JSONObject();
		Iterator<String> fieldNames = entity.getFieldsNames();
		while (fieldNames.hasNext()) {
			String fieldName = fieldNames.next();
			IDocumentFieldValue fieldValue = entity.getField(fieldName);
			try {

				JSONObject object = factory.decorate(fieldValue);
				fields.put(fieldName, object);
			} catch (Exception e) {
				log.error("", e);
				continue;
			}
		}
		renderedObject.put("fields", fields);

	}

	private void renderEntityReference(IEntityReference reference,
			JSONObject renderedObject) throws JSONException {
		renderedObject.put("type", IEntityReference.class.getName());
		IEntity referenced = reference.getEntity();
		renderedObject.put("entity", referenced.getId());
		renderedObject.put("entityLocation", referenced.getLocation());
	}

	private void renderDocumentDefinition(IDocumentDefinition reference,
			JSONObject renderedObject) throws JSONException {
		renderedObject.put("type", IDocumentDefinition.class.getName());
		JSONObject fields = new JSONObject();
		Iterable<String> fieldNames = new IteratorIterable<String>(
				reference.getFieldNames());
		for (String fieldName : fieldNames) {
			IDocumentFieldType fieldType = reference.getFieldType(fieldName);
			try {
				JSONObject fieldObject = new JSONObject();
				fieldObject.put("name", fieldType.getName());
				fieldObject.put("type", fieldType.getFieldDataType().name());
				fieldObject.put("required", fieldType.isRequired());
				fieldObject.put("authorizable", fieldType.isAuthorizable());

				List<String> attrNames = fieldType.getAttributeNames();
				for (String attrName : attrNames) {
					Object attrValue = fieldType.getAttribute(attrName);
					fieldObject.put(attrName, attrValue);
				}

				fields.put(fieldName, fieldObject);
			} catch (Exception e) {
				log.error("", e);
				continue;
			}
		}
		renderedObject.put("fields", fields);
	}

	private void renderDocumentAligner(IDocumentAligner reference,
			JSONObject renderedObject) throws JSONException {
		renderedObject.put("type", IDocumentAligner.class.getName());

		IDocumentDefinition fromDefinition = reference.getFromDefinition();
		IDocumentDefinition toDefinition = reference.getToDefinition();
		renderedObject.put("fromDefinition", fromDefinition.getId());
		renderedObject.put("fromDefinitionLocation",
				fromDefinition.getLocation());
		renderedObject.put("toDefinition", toDefinition.getId());
		renderedObject.put("toDefinitionLocation", toDefinition.getLocation());
		renderedObject.put("logicClass", reference.getLogicClass());

	}

	private void renderStaticResource(IStaticResource reference,
			JSONObject renderedObject) throws JSONException {
		renderedObject.put("type", IStaticResource.class.getName());
		renderedObject.put("mimeType", reference.getMimetype());
		renderedObject.put("contentSize", reference.getContentSize());
	}

	private void renderServiceDefinition(IServiceDefinition reference,
			JSONObject renderedObject) throws JSONException {
		renderedObject.put("type", IServiceDefinition.class.getName());
		IDocumentDefinition inputDefinition = reference.getInputDefinition();
		if (inputDefinition != null) {
			renderedObject.put("inputDefinition", inputDefinition.getId());
			renderedObject.put("inputDefinitionLocation",
					inputDefinition.getLocation());
		}
		List<IDocumentDefinition> possibleOutputs = reference
				.getPossibleOutputs();
		if (possibleOutputs != null && possibleOutputs.size() > 0) {
			JSONObject outputs = new JSONObject();
			for (IDocumentDefinition output : possibleOutputs) {
				JSONObject jsonOutput = new JSONObject();
				jsonOutput.put("definition", output.getId());
				jsonOutput.put("definitionLocation", output.getLocation());
				outputs.put(output.getName(), jsonOutput);
			}
			renderedObject.put("possibleOutputs", outputs);
		}
		renderedObject.put("logicClass", reference.getLogicClass());
	}

	private void renderRunningService(IRunningService reference,
			JSONObject renderedObject) throws JSONException {
		renderedObject.put("type", IRunningService.class.getName());

		IServiceDefinition definition = reference.getServiceDefinition();
		renderedObject.put("definition", definition.getId());
		renderedObject.put("definitionLocation", definition.getLocation());

		IDocument input = reference.getInput();
		if (input != null) {
			renderedObject.put("input", input.getId());
		}
		IDocument output = reference.getOutput();
		if (output != null) {
			renderedObject.put("output", output.getId());
		}
	}

	private void renderRenderer(IRenderer reference, JSONObject renderedObject)
			throws JSONException {
		renderedObject.put("type", IRenderer.class.getName());
		renderedObject.put("mimeType", reference.getMimetype());
	}

	private void renderEntitiesContainer(IEntitiesContainer reference,
			JSONObject renderedObject) throws JSONException {
		renderedObject.put("type", IEntitiesContainer.class.getName());
		renderedObject.put("entitiesCount", reference.countEntities());
	}

	private void renderChannel(IChannel reference, JSONObject renderedObject)
			throws JSONException {
		renderedObject.put("type", IChannel.class.getName());
		renderedObject.put("started", reference.isStartedUp());
	}

	private void renderArtifact(IArtifact reference, JSONObject renderedObject)
			throws JSONException {
		renderedObject.put("type", IArtifact.class.getName());
		renderedObject.put("groupId", reference.getGroupId());
		renderedObject.put("artifactId", reference.getArtifactId());
		renderedObject.put("version", reference.getVersion());
		renderedObject.put("description", reference.getDescription());
		IEntitiesContainer baseContainer = reference.getBaseContainer();
		if (baseContainer != null) {
			renderedObject.put("baseContainer", baseContainer.getId());
			renderedObject.put("baseContainerLocation",
					baseContainer.getLocation());
		}
	}

	private void renderAgent(IAgent reference, JSONObject renderedObject)
			throws JSONException {
		renderedObject.put("type", IAgent.class.getName());
	}

	private void renderEntityListener(IEntityEventListener listener,
			JSONObject renderedObject) throws JSONException {
		renderedObject.put("type", IEntityEventListener.class.getName());
		IEntityEventReceiver receiver = listener.getEventReceiver();
		if (receiver instanceof IEntity) {
			IEntity receiverEntity = (IEntity) receiver;
			renderedObject.put("eventReceiver", receiverEntity.getId());
			renderedObject.put("eventReceiverLocation",
					receiverEntity.getLocation());
		}
		renderedObject.put("eventType", listener.getEventType().name());
		IEntity sourceEntity = listener.getSourceEntity();
		renderedObject.put("sourceEntity", sourceEntity.getId());
		renderedObject.put("sourceEntityLocation", sourceEntity.getLocation());
	}

	@Override
	public String getTemplateFinderSuffix() {
		return ".js";
	}

	private boolean skipTemplates(IRenderingContext renderingContext) {
		String skipTemplatesVar = (String) renderingContext
				.getVariable(SKIP_TEMPLATES);
		if (skipTemplatesVar == null) {
			return false;
		}
		return Boolean.parseBoolean(skipTemplatesVar);
	}

	@Override
	public IStaticResource renderEntity(IEntity entity)
			throws GeneralDOAException {
		// TODO Auto-generated method stub
		return null;
	}

}

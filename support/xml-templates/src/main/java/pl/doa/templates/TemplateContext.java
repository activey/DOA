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
/*
 * This file is part of "xml-templates" project.
 */
package pl.doa.templates;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.WellformednessException;
import pl.doa.templates.tags.TagLibrary;
import pl.doa.templates.xml.DOANodeFactory;
import pl.doa.templates.xml.element.BaseElement;

/**
 * 
 * @author activey
 */
public class TemplateContext {

	private static final String ERROR_REMOVE_ROOT = "Factory attempted to remove the root element";

	private List<TagLibrary> taglibs = new ArrayList<TagLibrary>();

	private Map<String, Object> variables = new HashMap<String, Object>();

	private Boolean raw = null;

	private boolean skipBody;

	private boolean skipComments;

	private final TemplateContext parentContext;

	private DOANodeFactory nodeFactory;

	public TemplateContext() {
		this(null);
	}

	public TemplateContext(TemplateContext parentContext) {
		this.parentContext = parentContext;
		this.nodeFactory = new DOANodeFactory(this);
		this.raw = false;
	}

	/**
	 * TODO
	 * 
	 * @param name
	 * @param tagLibrary
	 * @throws GeneralDOAException
	 */
	public void registerTagLibrary(TagLibrary tagLibrary) throws Exception {
		if (isTaglibRegistered(tagLibrary))
			throw new Exception("this taglib is already registered!");
		tagLibrary.initialize(this);
		taglibs.add(tagLibrary);
	}

	public boolean isTaglibRegistered(TagLibrary tagLibrary) {
		for (TagLibrary registeredTagLibrary : taglibs) {
			if (registeredTagLibrary.equals(tagLibrary))
				return true;
		}
		if (parentContext != null) {
			return parentContext.isTaglibRegistered(tagLibrary);
		}
		return false;
	}

	public void setVariable(String varName, Object varValue) {
		this.variables.put(varName, varValue);
	}

	public Object getVariable(String varName) {
		return getVariable(varName, false);
	}

	public Object getVariable(String varName, boolean remove) {
		Object varValue = this.variables.get(varName);
		if (varValue != null) {
			if (remove) {
				this.variables.remove(varName);
			}
			return varValue;
		}
		if (parentContext != null) {
			Object parentVar = parentContext.getVariable(varName, remove);
			return parentVar;
		}
		return null;
	}

	public String execute(InputStream templateContent, boolean preserveNamespace)
			throws Exception {
		Builder builder = new Builder(new DOANodeFactory(this));
		Document document = builder.build(templateContent);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		Serializer serializer = new TemplateSerializer(output,
				preserveNamespace);
		serializer.setIndent(4);
		serializer.setPreserveBaseURI(false);
		serializer.write(document);
		serializer.flush();
		return output.toString();
	}

	public String execute(InputStream templateContent) throws Exception {
		return execute(templateContent, false);
	}

	public String execute(String templateContent) throws Exception {
		return execute(templateContent, false);
	}

	public String execute(String templateContent, boolean preserveNamespace)
			throws Exception {
		Builder builder = new Builder(nodeFactory);
		Document document = builder.build(new StringReader(templateContent));
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		Serializer serializer = new TemplateSerializer(output,
				preserveNamespace);
		serializer.setIndent(4);
		serializer.setPreserveBaseURI(false);
		serializer.write(document);
		serializer.flush();
		return output.toString();
	}

	public String execute(byte[] templateContent) throws Exception {
		return execute(templateContent, false);
	}

	public String execute(byte[] templateContent, boolean preserveNamespace)
			throws Exception {
		Builder builder = new Builder(nodeFactory);
		Document document = builder.build(new ByteArrayInputStream(
				templateContent));
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		Serializer serializer = new TemplateSerializer(output,
				preserveNamespace);
		serializer.setIndent(4);
		serializer.setPreserveBaseURI(false);
		serializer.write(document);
		serializer.flush();
		return output.toString();
	}

	public Node executeAsNode(String templateContent) throws Exception {
		if (templateContent == null || templateContent.trim().length() == 0) {
			return null;
		}
		Builder builder = new Builder(nodeFactory);
		Document document = builder.build(new StringReader(templateContent));
		Element rootElement = document.getRootElement();
		if (rootElement instanceof BaseElement) {
			BaseElement baseRoot = (BaseElement) rootElement;
			return baseRoot.copy();
		}
		return null;
	}

	public TagLibrary lookupTagLibrary(String namespace) {
		for (TagLibrary tagLibrary : taglibs) {
			String ns = tagLibrary.getNamespace();
			if (namespace == null) {
				continue;
			}
			if (ns.equals(namespace))
				return tagLibrary;
		}
		if (parentContext != null) {
			return parentContext.lookupTagLibrary(namespace);
		}
		return null;
	}

	public boolean isRaw() {
		if (raw != null) {
			return raw;
		}
		if (parentContext != null) {
			return parentContext.isRaw();
		}
		return false;
	}

	public void setRaw(boolean raw) {
		this.raw = raw;
	}

	public void setSkipBody(boolean skipBody) {
		this.skipBody = skipBody;
	}

	public boolean isSkipBody() {
		if (skipBody) {
			return skipBody;
		}
		if (parentContext != null) {
			return parentContext.isSkipBody();
		}
		return skipBody;
	}

	public void execute(InputStream templateContent, OutputStream output,
			boolean preserveNamespace) throws Exception {
		Builder builder = new Builder(new DOANodeFactory(this));
		Document document = null;
		try {
			document = builder.build(templateContent);
		} catch (ParsingException parsingException) {
			Throwable cause = parsingException.getCause();
			if (cause != null && (cause instanceof WellformednessException)) {
				String message = cause.getMessage();
				if (!ERROR_REMOVE_ROOT.equals(message)) {
					throw parsingException;
				}
				return;
			}
		}
		Serializer serializer = new TemplateSerializer(output,
				preserveNamespace);
		serializer.setIndent(4);
		serializer.setPreserveBaseURI(false);
		serializer.write(document);
		serializer.flush();
	}

	public void execute(InputStream templateContent, OutputStream output)
			throws Exception {
		execute(templateContent, output, false);
	}

	public boolean isSkipComments() {
		return skipComments;
	}

	public void setSkipComments(boolean skipComments) {
		this.skipComments = skipComments;
	}

}

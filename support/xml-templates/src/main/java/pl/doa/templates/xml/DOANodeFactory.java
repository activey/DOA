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
package pl.doa.templates.xml;

import nu.xom.Attribute;
import nu.xom.Attribute.Type;
import nu.xom.Comment;
import nu.xom.Element;
import nu.xom.NodeFactory;
import nu.xom.Nodes;

import org.apache.commons.beanutils.ConvertingWrapDynaBean;
import org.apache.commons.beanutils.DynaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.templates.TemplateContext;
import pl.doa.templates.tags.AttributesSupportTag;
import pl.doa.templates.tags.Tag;
import pl.doa.templates.tags.TagLibrary;
import pl.doa.templates.xml.element.BaseElement;

public class DOANodeFactory extends NodeFactory {

	private final static Logger log = LoggerFactory
			.getLogger(DOANodeFactory.class);
	private final TemplateContext templateContext;

	private Tag currentTag;

	public DOANodeFactory(TemplateContext templateContext) {
		this.templateContext = templateContext;
	}

	@Override
	public Element makeRootElement(String name, String namespace) {
		Element element = startMakingElement(name, namespace);
		if ("html".equals(name)) {
			templateContext.setVariable("ROOT_ELEMENT", element);
		}
		return element;
	}

	@Override
	public Nodes makeComment(String data) {
		if (templateContext.isSkipComments()) {
			return new Nodes();
		}
		return super.makeComment(data);
	}

	@Override
	public Nodes makeDocType(String rootElementName, String publicID,
			String systemID) {
		if (templateContext.isSkipComments()) {
			return new Nodes();
		}
		return super.makeDocType(rootElementName, publicID, systemID);
	}

	@Override
	public Nodes makeAttribute(String name, String namespace, String value,
			Type type) {
		if (templateContext.isRaw()) {
			return super.makeAttribute(name, namespace, value, type);
		}
		// przepisywanie parametrow
		if (currentTag != null) {
			DynaBean wrapper = new ConvertingWrapDynaBean(currentTag);
			if ("class".equals(name)) {
				wrapper.set("clazz", value);
			} else if (value.startsWith("#")) {
				String contextVar = value.substring(1);
				Object contextObj = templateContext.getVariable(contextVar);
				wrapper.set(name, (contextObj == null) ? value : contextObj);
			} else {
				wrapper.set(name, value);
			}
			if (currentTag instanceof AttributesSupportTag) {
				AttributesSupportTag attrsTag = (AttributesSupportTag) currentTag;
				attrsTag.setAttribute(name, value);
			}
		}
		return new Nodes(new Attribute(name, namespace, value));
	}

	private void processPrevious() throws Exception {
		if (currentTag != null) {
			try {
				currentTag.tagStart();
			} catch (Exception e) {
				log.error("", e);
				currentTag.setException(e);
				templateContext.setSkipBody(true);
			}
			this.currentTag = null;
		}
	}

	@Override
	public Element startMakingElement(String name, String namespace) {
		BaseElement element = new BaseElement(name, namespace);
		// wyszukiwanie biblioteki znacznikow na podstawie namespace i name
		TagLibrary tagLibrary = templateContext.lookupTagLibrary(namespace);
		if (tagLibrary == null) {
			try {
				processPrevious();
			} catch (Exception e) {
				log.error("", e);
			}
			return element;
		}
		Tag tag = tagLibrary.createTagInstance(element.getLocalName(),
				templateContext);
		if (tag == null) {
			try {
				processPrevious();
			} catch (Exception e) {
				log.error("", e);
			}
			return element;
		}
		try {
			processPrevious();
		} catch (Exception e) {
			log.error("", e);
		}
		if (templateContext.isRaw()) {
			element.setRaw(true);
			return element;
		}
		// sprawdzanie, czy nalezy przetwarzac element
		boolean isSkip = templateContext.isSkipBody();
		if (isSkip) {
			return null;
		}
		TagElement tagElement = new TagElement(element.getQualifiedName(),
				namespace);
		tag.setElement(tagElement);
		tagElement.setTag(tag);
		this.currentTag = tag;
		return tagElement;
	}

	@Override
	public Nodes finishMakingElement(Element elem) {
		BaseElement element = (BaseElement) elem;
		if (element.isRaw()) {
			return super.finishMakingElement(element);
		}
		if (!(element instanceof TagElement)) {
			Boolean isSkip = templateContext.isSkipBody();
			if (isSkip != null && isSkip) {
				return new Nodes();
			}
			return super.finishMakingElement(element);
		}
		TagElement tagElement = (TagElement) element;
		Tag tag = tagElement.getTag();
		try {
			if (tag.hasException()) {
				// usuwanie z kontekstu informacji o bledzie
				templateContext.setSkipBody(false);
				throw tag.getException();
			}
		} catch (Exception e) {
			log.error("", e);
			Comment error = new Comment(e.getMessage());
			return new Nodes(error);
		}
		Boolean isSkip = templateContext.isSkipBody();
		if (isSkip != null && isSkip) {
			return new Nodes();
		}
		this.currentTag = null;
		// przepisywanie atrybutow
		Nodes nodes;
		try {
			if (element.getChildCount() == 0) {
				tag.tagStart();
			}
			nodes = tag.tagEnd();
		} catch (Exception e) {
			log.error("", e);
			Comment error = new Comment(e.getMessage());
			return new Nodes(error);
		}
		/*
		 * if (nodes != null) { boolean hasChildren = (element.getChildCount() >
		 * 0); if (hasChildren) { // przepisywanie dzieci if (nodes instanceof
		 * Element) { Element appendedElement = (Element) nodes; while
		 * (hasChildren) { Node child = element.getChild(0); child.detach();
		 * appendedElement.appendChild(child); hasChildren =
		 * (element.getChildCount() > 0); } return new Nodes(appendedElement); }
		 * else if (nodes instanceof Text) { Nodes nodes = new Nodes(); while
		 * (hasChildren) { Node child = element.getChild(0); child.detach();
		 * nodes.append(child); hasChildren = (element.getChildCount() > 0); }
		 * return nodes; } } else { return new Nodes(nodes); } } return new
		 * Nodes();
		 */
		if (nodes == null) {
			nodes = new Nodes();
		}
		return nodes;
	}

}

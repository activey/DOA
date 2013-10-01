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
package pl.doa.templates.xml.element;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import pl.doa.templates.xml.traverse.ElementTraverser;

/**
 * @author activey
 * 
 */
public class BaseElement extends Element {

	private boolean raw;

	public BaseElement(String name) {
		super(name);
	}

	public BaseElement(String name, String namespace) {
		super(name, namespace);
	}

	public BaseElement traverseParent(ElementTraverser parentTraverser) {
		ParentNode parentElement = getParent();
		if (parentElement == null) {
			return null;
		}
		if (parentElement instanceof BaseElement) {
			BaseElement element = (BaseElement) parentElement;
			if (parentTraverser.elementMatch(element)) {
				return element;
			}
			return element.traverseParent(parentTraverser);
		}
		return null;
	}

	public final Attribute addAttribute(String attrName, String attrValue) {
		return addAttribute(attrName, attrValue, false);
	}

	public final Attribute addAttribute(String attrName, String attrValue,
			boolean createEmpty) {
		Attribute attribute = null;
		if (attrValue == null) {
			if (!createEmpty) {
				return null;
			}
			attribute = new Attribute(attrName, "");
		} else {
			attribute = new Attribute(attrName, attrValue);
		}		
		addAttribute(attribute);
		return attribute;
	}

	public BaseElement traverseChildren(ElementTraverser childrenTraverser) {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			Node node = getChild(i);
			if (!(node instanceof BaseElement)) {
				continue;
			}
			BaseElement element = (BaseElement) node;
			if (childrenTraverser.elementMatch(element)) {
				return element;
			}
			BaseElement fromChildren =
					element.traverseChildren(childrenTraverser);
			if (fromChildren != null) {
				return fromChildren;
			}
		}
		return null;
	}

	public boolean isRaw() {
		return raw;
	}

	public void setRaw(boolean raw) {
		this.raw = raw;
	}

	public Element copy() {
		Element copy = shallowCopy();
		Elements elements = getChildElements();
		for (int i = 0; i < elements.size(); i++) {
			Node childNode = elements.get(i);
			childNode.detach();
			copy.appendChild(childNode);
		}
		return copy;
	}

	public Nodes getChildNodes() {
		Nodes nodes = new Nodes();
		int childCount = getChildCount();
		for (int i = childCount - 1; i >= 0; i--) {
			Node node = getChild(i);
			node.detach();
			nodes.insert(node, 0);
		}
		return nodes;
	}

	public void appendChildren(Nodes childNodes) {
		int size = childNodes.size();
		for (int i = 0; i < size; i++) {
			appendChild(childNodes.get(i));
		}

	}

}

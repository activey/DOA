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
package pl.doa.templates.tags;

import nu.xom.Comment;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import pl.doa.templates.TemplateContext;
import pl.doa.templates.xml.TagElement;
import pl.doa.templates.xml.element.BaseElement;
import pl.doa.templates.xml.traverse.ElementTraverser;
import pl.doa.templates.xml.traverse.TagTraverser;

public abstract class Tag {

    protected TemplateContext context;
    protected TagElement element;
    private Exception exception;
    private boolean skip = false;

    public final void tagStart() throws Exception {
        processTagStart();
    }

    public abstract void processTagStart() throws Exception;

    public final Nodes tagEnd() throws Exception {
        if (skip) {
            return new Nodes();
        }
        return processTagEnd();
    }

    public abstract Nodes processTagEnd() throws Exception;

    public final void setTemplateContext(TemplateContext context) {
        this.context = context;
    }

    public final Document getDocument() {
        Element rootElement = (Element) context.getVariable("ROOT_ELEMENT");
        return rootElement.getDocument();
    }

    public BaseElement createElement(String elementName) {
        BaseElement element = new BaseElement(elementName);
        return element;
    }

	/*
     * public void replaceElement(Element newElement) { ParentNode parent =
	 * element.getParent(); parent.replaceChild(element, newElement); }
	 */

    public BaseElement createElement(String elementName, String namespace) {
        String prefix = element.getNamespacePrefix();
        String fullName = elementName;
        if (prefix != null && prefix.trim().length() > 0) {
            fullName = prefix + ":" + fullName;
        }
        BaseElement element = new BaseElement(fullName, namespace);
        return element;
    }

    public Comment createComment(String comment) {
        return new Comment(comment);
    }

    public TagElement getElement() {
        return this.element;
    }

    public void setElement(TagElement element) {
        this.element = element;
    }

    public boolean hasException() {
        return exception != null;
    }

    public Exception getException() {
        return this.exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public Tag traverseParent(final TagTraverser traverser) {
        TagElement parentElement =
                (TagElement) element.traverseParent(new ElementTraverser() {

                    public boolean elementMatch(Element element) {
                        if (element instanceof TagElement) {
                            TagElement tagElement = (TagElement) element;
                            return traverser.elementMatch(tagElement);
                        }
                        return false;
                    }
                });
        if (parentElement == null) {
            return null;
        }
        return parentElement.getTag();
    }

    public Tag getParent() {
        return traverseParent(new TagTraverser() {

            @Override
            public boolean tagMatch(Tag tag) {
                return true;
            }
        });
    }

    public String getBodyText() {
        return element.getValue();
    }

}

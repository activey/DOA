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
package pl.doa.templates.tags;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.templates.TemplateContext;

/**
 * 
 * @author activey
 */
public abstract class TagLibrary {

	private Map<String, Class<? extends Tag>> tags =
			new HashMap<String, Class<? extends Tag>>();

	private String namespace;

	private final static Logger log = LoggerFactory.getLogger(TagLibrary.class);

	private static final String RESTRICTED_CLASS = "class";

	private UnrecognizedTag unrecognizedTag;

	protected TemplateContext templateContext;

	/**
	 * Metoda rejestruje instancje znacznika w bibliotece znacznikow.
	 * 
	 * @param tagName
	 * @param tagInstance
	 */
	protected final void registerTag(String tagName,
			Class<? extends Tag> tagClass) throws Exception {
		if (tags.containsKey(tagName))
			throw new Exception("this tag is already registered!");
		tags.put(tagName, tagClass);
	}

	public final void initialize(TemplateContext templateContext) throws Exception {
		this.templateContext = templateContext;
		initializeTagLibrary();
	}

	protected abstract void initializeTagLibrary() throws Exception;

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public boolean equals(TagLibrary otherTaglibrary) {
		if (otherTaglibrary == null)
			return false;
		if (otherTaglibrary.getNamespace().equals(this.getNamespace()))
			return true;
		return false;
	}

	public Tag createTagInstance(String tagName, TemplateContext context) {
		Class<? extends Tag> tagClass = tags.get(tagName);
		Tag tagInstance = null;
		try {
			if (tagClass != null) {
				tagInstance = tagClass.newInstance();
			}
		} catch (Exception e) {
			log.error("", e);
		}
		if (tagInstance == null) {
			UnrecognizedTag unr = getUnrecognizedTag();
			if (unr != null) {
				unr.setTagName(tagName);
				tagInstance = unr;
			}
			if (tagInstance == null) {
				return null;
			}
		}
		tagInstance.setTemplateContext(context);
		return tagInstance;
	}

	public UnrecognizedTag getUnrecognizedTag() {
		return unrecognizedTag;
	}

	public void setUnrecognizedTag(UnrecognizedTag unrecognizedTag) {
		this.unrecognizedTag = unrecognizedTag;
	}

}

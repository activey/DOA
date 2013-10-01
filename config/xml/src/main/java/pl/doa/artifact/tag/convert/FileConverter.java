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
package pl.doa.artifact.tag.convert;

import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;

import org.apache.commons.beanutils.Converter;

import pl.doa.templates.TemplateContext;

public class FileConverter implements Converter {

	private final TemplateContext context;

	public FileConverter(TemplateContext context) {
		this.context = context;
	}

	@Override
	public Object convert(Class type, Object value) {
		if (value == null) {
			return null;
		}
		if (type.isAssignableFrom(value.getClass())) {
			return value;
		} else if (value instanceof String) {
			return findFile((String) value);
		}
		return null;
	}

	protected InputStream findFile(String lookupPath) {
		String artifactFileLocation =
				(String) context.getVariable("artifactJarFile");
		if (artifactFileLocation != null) {
			String jarEntryUrl =
					MessageFormat.format("jar:file:{0}!/{1}",
							artifactFileLocation, lookupPath);
			try {
				URL fileUrl = new URL(jarEntryUrl);
				return fileUrl.openStream();
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

}

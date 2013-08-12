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
package pl.doa.temp.http.service.auth.aligner;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.alignment.impl.AbstractDocumentAlignerLogic;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.IListDocumentFieldValue;
import pl.doa.utils.Base64Coder;

/**
 * @author activey
 * 
 */
public class BasicAuthenticationAligner extends AbstractDocumentAlignerLogic {

	/* (non-Javadoc)
	 * @see pl.doa.document.alignment.DocumentAlignerLogic#align(pl.doa.document.IDocument, pl.doa.document.IDocumentDefinition)
	 */
	@Override
	public IDocument align(IDocument input, IDocumentDefinition toDefinition)
			throws GeneralDOAException {
		IDocument output = toDefinition.createDocumentInstance();
		output.copyFieldFrom(input, "applicationDocument");
		IListDocumentFieldValue headers =
				(IListDocumentFieldValue) input.getField("headers");
		Iterable<IDocumentFieldValue> headersIterator = headers.iterateFields();
		for (IDocumentFieldValue header : headersIterator) {
			if (header.getFieldName().equalsIgnoreCase("authorization")) {
				String headerValue = header.getFieldValueAsString();
				if (headerValue != null) {
					String[] headerValueParts = headerValue.split(" ");
					String authType = null;
					String authString = null;
					if (headerValueParts.length > 0) {
						authType = headerValueParts[0];
					}
					if (authType == null
							|| !authType.toLowerCase().equals("basic")) {
						throw new GeneralDOAException(
								"Wrong authentication string!");
					}
					if (headerValueParts.length > 1) {
						authString = headerValueParts[1];
					}
					if (authString == null) {
						throw new GeneralDOAException(
								"Wrong authentication string!");
					}
					String decodedValue = Base64Coder.decodeString(authString);
					String[] authParts = decodedValue.split(":");
					if (authParts.length > 0) {
						output.setFieldValue("login", authParts[0]);
					}
					if (authParts.length > 1) {
						output.setFieldValue("password", authParts[1]);
					}
				}
				return output;
			}
		}
		return output;
	}

}

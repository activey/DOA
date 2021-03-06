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
package pl.doa;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import pl.doa.document.IDocument;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.IListDocumentFieldValue;

public class GeneralDOAException extends Exception {

	public GeneralDOAException() {
		super();
	}

	public GeneralDOAException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public GeneralDOAException(String message) {
		super(message);
	}

	public GeneralDOAException(String pattern, Object... arguments) {
		super(MessageFormat.format(pattern, arguments));
	}

	public GeneralDOAException(Throwable throwable) {
		super(throwable);
	}

	public void buildExceptionDocument(IDocument documentInstance)
			throws Exception {
		System.out.println("dsfgdfgdf");


        documentInstance.setFieldValue("message", getMessage());
		List<IDocumentFieldValue> stackTrace =
				new ArrayList<IDocumentFieldValue>();
		StackTraceElement[] stackElements = getStackTrace();
		IListDocumentFieldValue stackTraceField = null;
		if (stackElements != null && stackElements.length > 0) {
			stackTraceField =
					(IListDocumentFieldValue) documentInstance.getField(
							"stackTrace", true);
		}
		for (int i = 0; i < stackElements.length; i++) {
			StackTraceElement stackTraceElement = stackElements[i];
			stackTraceField.addStringField("stackElement" + i,
					stackTraceElement.toString());
		}
		documentInstance.setFieldValue("stackTrace", stackTrace);
	}

	public String getDefinitionLocation() {
		return "/documents/system/exception";
	}

}

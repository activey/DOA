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
package pl.doa.document;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import pl.doa.GeneralDOAException;
import pl.doa.document.field.IListDocumentFieldValue;

/**
 * @author activey
 * 
 */
public class DocumentValidationException extends GeneralDOAException {

	private List<FieldError> fieldsErrors = new ArrayList<FieldError>();

	public List<FieldError> getFieldsErrors() {
		return fieldsErrors;
	}

	public DocumentValidationException() {

	}

	public void addFieldException(String fieldName, String fieldError) {
		this.fieldsErrors.add(new FieldError(fieldName, fieldError));
	}

	@Override
	public String getMessage() {
		StringBuffer errorBuffer = new StringBuffer("Errors in fields: ");
		for (FieldError fieldError : fieldsErrors) {
			errorBuffer.append(MessageFormat.format("[{0}: {1}]",
					fieldError.getFieldName(), fieldError.getFieldError()));
		}
		return errorBuffer.toString();
	}

	private class FieldError {
		private String fieldName;
		private String fieldError;

		public FieldError(String fieldName, String fieldError) {
			this.fieldName = fieldName;
			this.fieldError = fieldError;
		}

		public String getFieldName() {
			return fieldName;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getFieldError() {
			return fieldError;
		}

		public void setFieldError(String fieldError) {
			this.fieldError = fieldError;
		}

	}

	@Override
	public void buildExceptionDocument(IDocument documentInstance)
			throws Exception {
		IListDocumentFieldValue errors =
				(IListDocumentFieldValue) documentInstance.getField("errors",
						true);
		for (FieldError error : fieldsErrors) {
			errors.addStringField(error.getFieldName(), error.getFieldError());
		}
	}

	@Override
	public String getDefinitionLocation() {
		return "/documents/system/validation_errors";
	}

}

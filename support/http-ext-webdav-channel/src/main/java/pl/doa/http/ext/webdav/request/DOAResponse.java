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
package pl.doa.http.ext.webdav.request;

import io.milton.http.AbstractResponse;
import io.milton.http.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.IListDocumentFieldValue;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author activey
 * 
 */
public class DOAResponse extends AbstractResponse {

	private final static Logger log = LoggerFactory
			.getLogger(DOAResponse.class);

	private final IDocument httpResponse;

	private final OutputStream outputStream;

	public DOAResponse(IDocument httpResponse, OutputStream outputStream) {
		this.httpResponse = httpResponse;
		this.outputStream = outputStream;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Response#getStatus()
	 */
	@Override
	public Status getStatus() {
		Integer httpCode = (Integer) httpResponse.getFieldValue("httpCode");
		if (httpCode == null) {
			return Status.SC_OK;
		}
		Status[] allStatuses = Status.values();
		for (Status status : allStatuses) {
			if (status.code == httpCode) {
				return status;
			}
		}
		return Status.SC_OK;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Response#getHeaders()
	 */
	@Override
	public Map<String, String> getHeaders() {
		Map<String, String> headers = new HashMap<String, String>();
		Iterable<IDocumentFieldValue> headersFields =
				(Iterable<IDocumentFieldValue>) httpResponse
						.getFieldValue("headers");
		for (IDocumentFieldValue headerField : headersFields) {
			headers.put(headerField.getFieldName(),
					headerField.getFieldValueAsString());
		}
		return headers;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Response#setAuthenticateHeader(java.util.List)
	 */
	@Override
	public void setAuthenticateHeader(List<String> challenges) {

	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Response#setStatus(com.bradmcevoy.http.Response.Status)
	 */
	@Override
	public void setStatus(Status status) {
		try {
			httpResponse.setFieldValue("httpCode", status.code);
		} catch (GeneralDOAException e) {
			log.error("", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Response#setNonStandardHeader(java.lang.String, java.lang.String)
	 */
	@Override
	public void setNonStandardHeader(String code, String value) {		
		try {
			IListDocumentFieldValue headers =
				(IListDocumentFieldValue) httpResponse
						.getField("headers", true);
			headers.addStringField(code, value);
		} catch (GeneralDOAException e) {
			log.error("", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Response#getNonStandardHeader(java.lang.String)
	 */
	@Override
	public String getNonStandardHeader(String code) {
		return getHeaders().get(code);
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Response#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() {
		return this.outputStream;
	}

    @Override
    public void close() {
    }

    @Override
    public void sendError(Status status, String s) {
    }

    /* (non-Javadoc)
     * @see com.bradmcevoy.http.Response#setCookie(com.bradmcevoy.http.Cookie)
     */
	@Override
	public Cookie setCookie(Cookie cookie) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Response#setCookie(java.lang.String, java.lang.String)
	 */
	@Override
	public Cookie setCookie(String name, String value) {
		return null;
	}

}

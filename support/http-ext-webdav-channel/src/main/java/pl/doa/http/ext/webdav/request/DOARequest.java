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

import io.milton.http.*;
import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.IListDocumentFieldValue;
import pl.doa.resource.IStaticResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author activey
 */
public class DOARequest extends AbstractRequest {

    private final IDocument httpRequest;

    public DOARequest(IDocument httpRequest) {
        this.httpRequest = httpRequest;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bradmcevoy.http.Request#getHeaders()
     */
    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        Iterable<IDocumentFieldValue> headersFields = (Iterable<IDocumentFieldValue>) httpRequest
                .getFieldValue("headers");
        for (IDocumentFieldValue headerField : headersFields) {
            headers.put(headerField.getFieldName(),
                    headerField.getFieldValueAsString());
        }
        return headers;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bradmcevoy.http.Request#getFromAddress()
     */
    @Override
    public String getFromAddress() {
        return httpRequest.getFieldValueAsString("remoteAddress");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bradmcevoy.http.Request#getMethod()
     */
    @Override
    public Method getMethod() {
        return Method.valueOf(httpRequest.getFieldValueAsString("method"));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bradmcevoy.http.Request#getAuthorization()
     */
    @Override
    public Auth getAuthorization() {
        return null;
    }

    @Override
    public void setAuthorization(Auth auth) {
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bradmcevoy.http.Request#getAbsoluteUrl()
     */
    @Override
    public String getAbsoluteUrl() {
        return httpRequest.getFieldValueAsString("url");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bradmcevoy.http.Request#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        IStaticResource bodyResource = (IStaticResource) httpRequest
                .getFieldValue("body");
        try {
            return bodyResource.getContentStream();
        } catch (GeneralDOAException e) {
            throw new IOException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bradmcevoy.http.Request#parseRequestParameters(java.util.Map,
     * java.util.Map)
     */
    @Override
    public void parseRequestParameters(Map<String, String> params,
                                       Map<String, FileItem> files) throws RequestParseException {

    }

    /*
     * (non-Javadoc)
     *
     * @see com.bradmcevoy.http.Request#getCookie(java.lang.String)
     */
    @Override
    public Cookie getCookie(String name) {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bradmcevoy.http.Request#getCookies()
     */
    @Override
    public List<Cookie> getCookies() {
        List<Cookie> cookies = new ArrayList<Cookie>();
        Iterable<IDocumentFieldValue> headersFields = (Iterable<IDocumentFieldValue>) httpRequest
                .getFieldValue("cookies");
        for (IDocumentFieldValue cookiesField : headersFields) {

        }
        return cookies;
    }

    @Override
    public String getRemoteAddr() {
        return httpRequest.getFieldValueAsString("remoteAddress");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.bradmcevoy.http.AbstractRequest#getRequestHeader(com.bradmcevoy.http
     * .Request.Header)
     */
    @Override
    public String getRequestHeader(Header header) {
        IListDocumentFieldValue listField = (IListDocumentFieldValue) httpRequest
                .getField("headers");
        IDocumentFieldValue fieldValue = listField.getListField(header.code
                .toLowerCase());
        if (fieldValue == null) {
            return null;
        }
        return fieldValue.getFieldValueAsString();
    }

}

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
package pl.doa.http.ext.webdav.resource.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.document.IDocument;
import pl.doa.http.ext.webdav.resource.builder.ResourceBuilderFactory;
import pl.doa.renderer.IRenderer;
import pl.doa.resource.IStaticResource;

import com.bradmcevoy.http.FileItem;
import com.bradmcevoy.http.FileResource;
import com.bradmcevoy.http.Range;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;

/**
 * @author activey
 */
public class DocumentResource extends DOAEntityResource<IDocument> implements FileResource {

    private static final String CONTENT_TYPE = "doa/document";

    private static final String RENDERER_JSON = "/renderers/renderer_json/json";

    private static final Logger log = LoggerFactory
            .getLogger(DocumentResource.class);

    private byte[] content;

    public DocumentResource(ResourceBuilderFactory factory, IDocument document) {
        super(factory, document);

        // renderowanie dokumentu w json
        IDOA doa = factory.getDoa();
        IRenderer renderer = (IRenderer) doa
                .lookupEntityByLocation(RENDERER_JSON);
        if (renderer == null) {
            return;
        }
        IStaticResource rendered;
        try {
            rendered = renderer.renderEntity((IDocument) entity);
            if (rendered == null) {
                return;
            }
            this.content = rendered.getContent();
        } catch (GeneralDOAException e) {
            log.error("", e);
            return;
        }
    }

    @Override
    public String processForm(Map<String, String> parameters,
                              Map<String, FileItem> files) throws BadRequestException,
            NotAuthorizedException, ConflictException {
        return null;
    }


    @Override
    public String getContentType(String accepts) {
        return CONTENT_TYPE;
    }

    @Override
    public Long getContentLength() {
        if (content == null) {
            return null;
        }
        return (long) content.length;
    }

    @Override
    public void sendContent(OutputStream out, Range range,
                            Map<String, String> params, String contentType) throws IOException,
            NotAuthorizedException, BadRequestException {
        out.write(content);
        out.flush();
        out.close();
    }

}

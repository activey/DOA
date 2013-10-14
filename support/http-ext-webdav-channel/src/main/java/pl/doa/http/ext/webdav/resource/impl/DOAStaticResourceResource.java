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

import io.milton.http.FileItem;
import io.milton.http.Range;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.resource.FileResource;
import io.milton.resource.ReplaceableResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.http.ext.webdav.resource.builder.ResourceBuilderFactory;
import pl.doa.resource.IStaticResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author activey
 */
public class DOAStaticResourceResource extends
        DOAEntityResource<IStaticResource> implements FileResource,
        ReplaceableResource {

    private static final Logger log = LoggerFactory
            .getLogger(DOAStaticResourceResource.class);

    public DOAStaticResourceResource(ResourceBuilderFactory factory,
                                     IStaticResource resource) {
        super(factory, resource);
    }

    @Override
    public String processForm(Map<String, String> parameters,
                              Map<String, FileItem> files) throws BadRequestException,
            NotAuthorizedException, ConflictException {
        return null;
    }

    @Override
    public String getContentType(String accepts) {
        return entity.getMimetype();
    }

    @Override
    public Long getContentLength() {
        return new Long(entity.getContentSize());
    }

    @Override
    public void sendContent(OutputStream out, Range range,
                            Map<String, String> params, String contentType) throws IOException,
            NotAuthorizedException, BadRequestException {
        try {
            out.write(entity.getContent());
        } catch (GeneralDOAException e) {
            throw new IOException(e);
        }
        out.flush();
        out.close();
    }

    public void replaceContent(InputStream in, Long length) {
        if (length == null) {
            return;
        }
        try {
            entity.setContentFromStream(in, length);
        } catch (GeneralDOAException e) {
            log.error("", e);
        }
    }

}

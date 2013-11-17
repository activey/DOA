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
package pl.doa.resource.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.entity.impl.AbstractEntity;
import pl.doa.resource.IStaticResource;
import pl.doa.utils.DataUtils;

/**
 * @author activey
 */
public abstract class AbstractStaticResource extends AbstractEntity implements
        IStaticResource {

    public AbstractStaticResource(IDOA doa) {
        super(doa);
    }

    protected abstract void setMimetypeImpl(String mimetype);

    public final void setMimetype(String mimetype) {
        setMimetypeImpl(mimetype);
    }

    protected abstract String getMimetypeImpl();

    public final String getMimetype() {
        return getMimetypeImpl();
    }

    protected abstract long getContentSizeImpl();

    public final long getContentSize() {
        return getContentSizeImpl();
    }

    public final void setContentFromStream(InputStream contentStream)
            throws GeneralDOAException {
        try {
            long contentSize = getDoa().storeOrUpdate(this, contentStream);
            setContentSizeImpl(contentSize);
        } catch (Exception e) {
            throw new GeneralDOAException(e);
        }
    }

    public final boolean removeStreamContent() throws Exception {
        return getDoa().removeFileStream(this);
    }

    public final void setContentFromBytes(byte[] bytes)
            throws GeneralDOAException {
        try {
            long contentSize =
                    getDoa().storeOrUpdate(this, new ByteArrayInputStream(bytes));
            setContentSizeImpl(contentSize);
        } catch (Exception e) {
            throw new GeneralDOAException(e);
        }
    }

    protected abstract void setContentSizeImpl(long contentSize);

    public final void setContentFromStream(InputStream contentStream,
                                           Long contentSize) throws GeneralDOAException {
        try {
            long storedSize = getDoa().storeOrUpdate(this, contentStream);
            if (contentSize == null || contentSize != storedSize) {
                contentSize = storedSize;
            }
            setContentSizeImpl(contentSize);
        } catch (Exception e) {
            throw new GeneralDOAException(e);
        }
    }

    public final InputStream getContentStream() throws GeneralDOAException {
        try {
            return getDoa().retrieve(this);
        } catch (Exception e) {
            throw new GeneralDOAException(e);
        }
    }

    public final byte[] getContent() throws GeneralDOAException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            InputStream data = getDoa().retrieve(this);
            DataUtils.copyStream(data, out, 1024);
        } catch (Exception e) {
            throw new GeneralDOAException(e);
        }
        return out.toByteArray();
    }

}

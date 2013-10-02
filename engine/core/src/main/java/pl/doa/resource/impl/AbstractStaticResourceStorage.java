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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.resource.IStaticResource;
import pl.doa.resource.IStaticResourceStorage;

/**
 * @author activey
 */
public abstract class AbstractStaticResourceStorage implements IStaticResourceStorage {

    private final static Logger log = LoggerFactory
            .getLogger(AbstractStaticResourceStorage.class);

    private static final String ATTR_STORED_FILE_LOCATION =
            "stored_file_location";


    protected abstract File getFileDB();

    /* (non-Javadoc)
     * @see pl.doa.resource.IStaticResourceStorage#storeOrUpdate(pl.doa.resource.IStaticResource, java.io.InputStream)
     */
    @Override
    public final long storeOrUpdate(IStaticResource resource, InputStream dataStream)
            throws Exception {
        String storedFile = null;
        try {
            storedFile = resource.getAttribute(ATTR_STORED_FILE_LOCATION);
        } catch (Exception ex) {
            log.info("Could not find resource file location, skipping...");
        }
        File file = null;
        if (storedFile != null) {
            file = new File(storedFile);
        } else {
            if (getFileDB() != null) {
                file = new File(getFileDB(), "/resource_" + resource.getId());
            } else {
                file = File.createTempFile("resource_", resource.getId() + "");
            }
            resource.setAttribute(ATTR_STORED_FILE_LOCATION,
                    file.getAbsolutePath());
        }
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = dataStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } finally {
            /*if (dataStream != null) {
				dataStream.close();
			}*/
            if (out != null) {
                out.close();
            }
        }
        return file.length();
    }

    /* (non-Javadoc)
     * @see pl.doa.resource.IStaticResourceStorage#retrieve(pl.doa.resource.IStaticResource)
     */
    @Override
    public final InputStream retrieve(IStaticResource resource) throws Exception {
        String storedFile = resource.getAttribute(ATTR_STORED_FILE_LOCATION);
        if (storedFile == null) {
            throw new GeneralDOAException(
                    "Unable to find resource file for: [{0}]",
                    resource.getLocation());
        }
        return new FileInputStream(storedFile);
    }

    @Override
    public final boolean remove(IStaticResource resource) throws Exception {
        String storedFile = resource.getAttribute(ATTR_STORED_FILE_LOCATION);
        if (storedFile == null) {
            log.debug("Unable to find resource file for: [{0}]",
                    resource.getLocation());
            return true;
        }
        return new File(storedFile).delete();
    }
}

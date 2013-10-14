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

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.MessageFormat;

/**
 * @author activey
 */
public class SimpleStaticResourceStorage extends AbstractStaticResourceStorage {

    public static final String CONFIGURATION_STORAGE_DIRECTORY = "doa.storage.directory";
    private final static Logger log = LoggerFactory
            .getLogger(AbstractStaticResourceStorage.class);
    private final static String FILE_STORAGE_DIR = "./var/files";
    private File fileDB;

    public SimpleStaticResourceStorage(Configuration configuration) {
        this.fileDB = new File(configuration.getString(CONFIGURATION_STORAGE_DIRECTORY, FILE_STORAGE_DIR));
        if (!fileDB.exists()) {
            log.debug("Storage directory [" + fileDB.getAbsolutePath() + "] does not exist yet, creating ...");
            fileDB.mkdirs();
        }
    }

    @Override
    protected File getFileDB() {
        return this.fileDB;
    }

    public void setFileDB(String fileDB) {
        File dbFolder = new File(fileDB);
        if (!dbFolder.exists()) {
            log.debug(MessageFormat.format(
                    "Unable to find folder {0}, recreating structure ...",
                    fileDB));
            dbFolder.mkdirs();
        }
        this.fileDB = dbFolder;
    }
}

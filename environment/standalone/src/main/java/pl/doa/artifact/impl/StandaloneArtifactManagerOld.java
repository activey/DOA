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
package pl.doa.artifact.impl;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.artifact.IDirectoryListener;
import pl.doa.artifact.DirectoryMonitor;
import pl.doa.artifact.IArtifact;
import pl.doa.entity.ITransactionCallback;
import pl.doa.jvm.DOAURLHandlerFactory;
import pl.doa.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;

public class StandaloneArtifactManagerOld  {

    private final static Logger LOG = LoggerFactory
            .getLogger(StandaloneArtifactManagerOld.class);

    private final static String DEFAULT_DEPLOY_DIRECTORY = "./var/deploy";
    private final static long DEFAULT_MONITOR_INTERVAL = 1000;
    private final static String DEFAULT_CACHE_DIRECTORY = "./var/ivy";

    public static final String CONFIGURATION_DEPLOY_DIRECTORY = "doa.deploy.directory";
    public static final String CONFIGURATION_DEPLOY_MONITOR_INTERVAL = "doa.deploy.monitor.interval";
    public static final String CONFIGURATION_DEPLOY_CACHE_DIRECTORY = "doa.deploy.cache.directory";

    private final Configuration configuration;

    public StandaloneArtifactManagerOld(IDOA doa, Configuration configuration) {
        this.configuration = configuration;
        try {
            LOG.debug("Initializing Artifact Manager ...");
            DOAURLHandlerFactory.attachFactory(doa);

            // uruchomianie sluchacza katalogu deploymentu
            LOG.debug("Starting up artifacts deployment directory listener: ["
                    + getDeployDirectory() + "]");
            DirectoryMonitor directoryMonitor =
                    new DirectoryMonitor(getMonitorInterval());
            File deployDirectory = new File(getDeployDirectory());
            if (!deployDirectory.exists()) {
                LOG.debug("Deploy directory does not exist, attempting to create it ...");
                boolean created = deployDirectory.mkdirs();
                if (!created) {
                    LOG.debug(MessageFormat
                            .format("Unable to create directory: [{0}], hot deployment disabled",
                                    getDeployDirectory()));
                    return;
                }
            }
            directoryMonitor.setDirectory(deployDirectory);
            //directoryMonitor.addListener(this);
            directoryMonitor.start();

            //initializeRepository(null);
        } catch (Exception e) {
            LOG.error("", e);
        }
    }

    public String getDeployDirectory() {
        return configuration.getString(CONFIGURATION_DEPLOY_DIRECTORY, DEFAULT_DEPLOY_DIRECTORY);
    }

    public long getMonitorInterval() {
        return configuration.getLong(CONFIGURATION_DEPLOY_MONITOR_INTERVAL, DEFAULT_MONITOR_INTERVAL);
    }

    //@Override
    public String getCacheDirectory() {
        return configuration.getString(CONFIGURATION_DEPLOY_CACHE_DIRECTORY, DEFAULT_CACHE_DIRECTORY);
    }


    public IArtifact deployArtifact(File artifactFile) throws GeneralDOAException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
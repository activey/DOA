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
package pl.doa.artifact.tag;

import nu.xom.Nodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.artifact.deploy.DeploymentContext;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.impl.EntityLocationIterator;
import pl.doa.resource.IStaticResource;
import pl.doa.utils.ContentTypeUtils;
import pl.doa.utils.PathIterator;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class LoadTag extends DeploymentProcessorSupportTag {

    private final static Logger log = LoggerFactory.getLogger(LoadTag.class);
    private String directory;
    private String location;
    private IDOA doa;

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public void processTagStart() throws Exception {
        if (directory == null) {
            return;
        }


        File artifactFile =
                (File) context.getVariable(DeploymentContext.VAR_DEPLOYED);
        JarFile jarFile;
        try {
            jarFile = new JarFile(artifactFile);
        } catch (IOException e) {
            throw new GeneralDOAException(e);
        }
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) jarEntries.nextElement();
            if (jarEntry.getName().startsWith(directory)) {
                String entityLocation = jarEntry.getName();
                entityLocation =
                        entityLocation.substring(directory.length(),
                                entityLocation.length());
                if ("".equals(entityLocation)) {
                    continue;
                }

                PathIterator<String> entityLocationPath = new EntityLocationIterator(entityLocation, true);
                if (entityLocationPath.getLength() == 0) {
                    continue;
                }
                String entityName = entityLocationPath.previous();

                IEntity entity =
                        getParentContainer().lookupEntityByLocation(entityLocation);
                if (entity == null) {
                    IEntitiesContainer parentContainer = (IEntitiesContainer) getParentContainer()
                            .lookupEntityByLocation(entityLocationPath.getRemainingPath());
                    if (jarEntry.isDirectory()) {
                        entity = createEntitiesContainer(entityName, parentContainer);
                    } else {

                        IStaticResource resource =
                                createStaticResource(
                                        entityName,
                                        ContentTypeUtils
                                                .findContentTypes(entityName), parentContainer);
                        URL entryUrl =
                                getJarEntryURL(artifactFile, jarEntry);
                        try {
                            resource.setContentFromStream(
                                    entryUrl.openStream(), jarEntry.getSize());
                            entity = resource;
                        } catch (IOException e) {
                            throw new GeneralDOAException(e);
                        }
                    }
                    String destLocation =
                            entityLocation.substring(0, entityLocation.length()
                                    - entityName.length() - 1);
                    if (destLocation == null || destLocation.length() == 0) {
                        getParentContainer().addEntity(entity);
                    } else {
                        /*TODO think about it
                        entity.store(MessageFormat.format("{0}{1}",
                                destContainer.getLocation(), destLocation));   */
                    }
                }
                continue;
            }
        }
    }

    @Override
    public Nodes processTagEnd() throws Exception {
        return null;
    }

    private URL getJarEntryURL(File jarFile, JarEntry jarEntry) {
        String entryLocation = jarEntry.getName();
        try {
            String url =
                    MessageFormat.format("jar:file:{0}!/{1}", jarFile.getAbsolutePath(),
                            entryLocation);
            return new URL(url);
        } catch (MalformedURLException e) {
            log.error("", e);
            return null;
        }
    }

}

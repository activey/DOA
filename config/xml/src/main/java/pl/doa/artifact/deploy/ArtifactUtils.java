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
package pl.doa.artifact.deploy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;

import org.apache.ivy.core.module.descriptor.Artifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.artifact.IArtifact;
import pl.doa.artifact.tag.DeploymentTagLibrary;
import pl.doa.entity.startable.IStartableEntity;

public class ArtifactUtils {

	private final static Logger log = LoggerFactory
			.getLogger(ArtifactUtils.class);

	public static String getArtifactName(Artifact artifact) {
		return MessageFormat.format("{0}.{1}.{2}", artifact
				.getModuleRevisionId().getOrganisation(), artifact.getId()
				.getArtifactId().getName(), artifact.getModuleRevisionId()
				.getRevision());
	}

	public static void executeDeploymentScript(IArtifact artifact,
			File artifactJarFile, byte[] scriptContent,
			List<IStartableEntity> autostartEntities)
			throws GeneralDOAException {
		ArtifactUtils.executeDeploymentScript(artifact, artifactJarFile,
				new ByteArrayInputStream(scriptContent), autostartEntities);
	}

	public static void executeDeploymentScript(IArtifact artifact,
			File artifactJarFile, InputStream scriptContent,
			List<IStartableEntity> autostartEntities)
			throws GeneralDOAException {
		IDOA doa = artifact.getDoa();

		DeploymentContext context = new DeploymentContext();
		try {
			context.registerTagLibrary(new DeploymentTagLibrary());
		} catch (Exception e1) {
			throw new GeneralDOAException(e1);
		}
		context.setArtifact(artifact);
		context.setDoa(doa);
		context.setAutostart(autostartEntities);
		if (artifactJarFile != null) {
			context.setArtifactJar(artifactJarFile);
		}
		log.debug("executing deployment script ...");

		String renderedContent;
		try {
			renderedContent = context.execute(scriptContent);
		} catch (Exception e) {
			throw new GeneralDOAException(e);
		}
	}
}

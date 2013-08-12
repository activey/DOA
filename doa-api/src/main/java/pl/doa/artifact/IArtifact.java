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
package pl.doa.artifact;

import java.io.InputStream;
import java.util.List;

import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.resource.IStaticResource;

public interface IArtifact extends IEntity {

	public static enum Type {
		XML,

		JAR
	}

	public void setGroupId(String groupId);

	public String getGroupId();

	public void setArtifactId(String artifactId);

	public String getArtifactId();

	public String getVersion();

	public void setVersion(String version);

	public void setArtifactFileName(String fileName);

	public String getArtifactFileName();

	public void setType(IArtifact.Type type);

	public IArtifact.Type getType();

	public String getDescription();

	public void setDescription(String description);

	public IStaticResource getArtifactResource();

	/**
	 * Create connection between Artifact and Entity.
	 * 
	 * @param entity
	 */
	public void registerEntity(IEntity entity) throws GeneralDOAException;

	/**
	 * Removes connection between Artifact and Entity.
	 * 
	 * @param entity
	 */
	public void unregisterEntity(IEntity entity);

	/**
	 * Informs if at least one Artifact is dependent on this.
	 * 
	 * @return
	 */
	public boolean isParentDependent();

	/**
	 * Disconnect this with dependent Artifact.
	 */
	public void removeDependency(IArtifact dependecy);

	public void setArtifactResource(IStaticResource resource);

	public void setArtifactResourceStream(InputStream fileStream,
			long contentSize) throws GeneralDOAException;

	public void setArtifactResourceBytes(byte[] fileContent)
			throws GeneralDOAException;

	public InputStream getArtifactFileStream() throws GeneralDOAException;

	public void setDependencies(List<IArtifact> dependendArtifacts);

	public List<IArtifact> getDependencies();

	public void setBaseContainer(IEntitiesContainer container);

	public IEntitiesContainer getBaseContainer();

	public List<IEntity> getRegisteredEntities();

}
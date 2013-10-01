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
package pl.doa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;

import pl.doa.artifact.IArtifactManager;
import pl.doa.entity.IEntityAttribute;
import pl.doa.entity.startable.IStartableEntityManager;
import pl.doa.resource.IStaticResourceStorage;
import pl.doa.service.IServicesManager;
import pl.doa.thread.IThreadManager;

/**
 * @author activey
 * 
 */
public class StandaloneDOA extends AbstractBootstrapDOA implements
		DisposableBean {

	private String logicClass;

	private Map<String, String> attributes = new HashMap<String, String>();

	private String name;

	@Autowired
	private IServicesManager servicesManager;

	@Autowired
	private IArtifactManager artifactManager;

	@Autowired
	private IStaticResourceStorage resourceStorage;

	@Autowired
	private IStartableEntityManager startableManager;

	@Autowired
	private IThreadManager threadManager;

	@Override
	protected final String getNameImpl() {
		return this.name;
	}

	@Override
	protected final void setNameImpl(String name) {
		this.name = name;
	}

	@Override
	protected final boolean hasAttributesImpl() {
		return attributes != null && !attributes.isEmpty();
	}

	@Override
	protected List<String> getAttributeNamesImpl() {
		return new ArrayList<String>(attributes.keySet());
	}

	@Override
	protected String getAttributeImpl(String attrName, String defaultValue) {
		String attrValue = attributes.get(attrName);
		if (attrValue == null) {
			attrValue = defaultValue;
		}
		return attrValue;
	}

	@Override
	protected final IEntityAttribute getAttributeObjectImpl(final String attrName) {
		final String attrValue = attributes.get(attrName);
		if (attrValue == null) {
			return null;
		}
		IEntityAttribute attr = new IEntityAttribute() {

			@Override
			public void setValue(Object value) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void setName(String name) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean remove() {
				throw new UnsupportedOperationException();
			}

			@Override
			public Object getValue() {
				return attrValue;
			}

			@Override
			public String getName() {
				return attrName;
			}
		};
		return attr;
	}

	@Override
	protected final void setAttributeImpl(String attrName, String attrValue) {
		attributes.put(attrName, attrValue);
	}

	protected final void setAttributeImpl(IEntityAttribute attributte) {
		attributes.put(attributte.getName(), attributte.getValue().toString());
	}

	@Override
	protected final void removeAttributesImpl() {
		attributes.clear();
	}

	@Override
	protected final void setAttributesImpl(Map<String, String> attributes) {
		this.attributes.putAll(attributes);
	}

	@Override
	protected final String getLogicClassImpl() {
		return this.logicClass;
	}

	@Override
	protected void setLogicClassImpl(String logicClass) {
		this.logicClass = logicClass;
	}

	@Override
	public void destroy() throws Exception {
		shutdown();
	}

	public void setServicesManager(IServicesManager servicesManager) {
		this.servicesManager = servicesManager;
	}

	public void setArtifactManager(IArtifactManager artifactManager) {
		this.artifactManager = artifactManager;
	}

	public void setResourceStorage(IStaticResourceStorage resourceStorage) {
		this.resourceStorage = resourceStorage;
	}

	public void setStartableManager(IStartableEntityManager startableManager) {
		this.startableManager = startableManager;
	}

	public void setThreadManager(IThreadManager threadManager) {
		this.threadManager = threadManager;
	}

	public IServicesManager getServicesManager() {
		return servicesManager;
	}

	public IArtifactManager getArtifactManager() {
		return artifactManager;
	}

	public IStaticResourceStorage getResourceStorage() {
		return resourceStorage;
	}

	public IStartableEntityManager getStartableManager() {
		return startableManager;
	}

	public IThreadManager getThreadManager() {
		return threadManager;
	}

}

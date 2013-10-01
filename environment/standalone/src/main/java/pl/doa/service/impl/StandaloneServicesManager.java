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
package pl.doa.service.impl;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import pl.doa.IDOA;
import pl.doa.service.AbstractServiceDefinitionLogic;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinitionLogic;
import pl.doa.service.IServicesManager;

/**
 * @author activey
 * 
 */
public class StandaloneServicesManager extends AbstractServicesManager implements
		ApplicationContextAware, IServicesManager {

	private static final Logger log = LoggerFactory
			.getLogger(StandaloneServicesManager.class);

	@Autowired
	private IDOA doa;

	private ApplicationContext springContext;


	private ThreadGroup threads;

	static {
		// make sure that the task class is loaded by the classloader that
		// loads the task runner, otherwise it might be loaded several times
		// and result in linkage error exceptions
		@SuppressWarnings("unused")
		Class t = AbstractServiceDefinitionLogic.class;
	}

	@Override
	public void setApplicationContext(ApplicationContext springContext)
			throws BeansException {
		this.springContext = springContext;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * pl.doa.service.IServicesManager#getRunning(pl.doa.service.IRunningService
	 * )
	 */
	@Override
	public IServiceDefinitionLogic getRunning(IRunningService runningService) {
		long entityUuid = runningService.getId();
		log.debug(MessageFormat.format(
				"checking if service is running for id = {0}", entityUuid));
		IServiceDefinitionLogic running = getRunning(entityUuid + "");
		if (running != null) {
			log.debug(MessageFormat.format("found running instance: {0}",
					runningService.getId()));
		}
		return running;
	}

	/*
	 * (non-Javadoc)
	 * @see pl.doa.service.IServicesManager#getRunning(java.lang.String)
	 */
	@Override
	public IServiceDefinitionLogic getRunning(String runningServiceUUID) {
		DefaultListableBeanFactory factory =
				(DefaultListableBeanFactory) springContext
						.getAutowireCapableBeanFactory();
		if (!factory.containsSingleton(runningServiceUUID)) {
			return null;
		}
		return (IServiceDefinitionLogic) factory
				.getSingleton(runningServiceUUID);
	}


	public IDOA getDoa() {
		return doa;
	}

	public void setDoa(IDOA doa) {
		this.doa = doa;
	}
}

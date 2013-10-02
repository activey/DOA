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
package pl.doa.entity.startable.impl;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.entity.impl.AbstractStartableEntityManager;
import pl.doa.entity.startable.IStartableEntity;
import pl.doa.entity.startable.IStartableEntityLogic;
import pl.doa.utils.SpringUtils;

/**
 * @author activey
 * 
 */
public class StandaloneStartableEntityManager extends
        AbstractStartableEntityManager implements ApplicationContextAware {

	private ApplicationContext springContext;

	@Autowired
	private IDOA doa;

	@Override
	public void setApplicationContext(ApplicationContext springContext)
			throws BeansException {
		this.springContext = springContext;
	}

	public IDOA getDoa() {
		return doa;
	}

	public void setDoa(IDOA doa) {
		this.doa = doa;
	}

	public IStartableEntityLogic getRunning(IStartableEntity startableEntity) {
		long entityId = startableEntity.getId();
		if (entityId == -1) {
			return null;
		}
		/*
		 * wyszukiwanie bean-a w rejestrze springa
		 */
		DefaultListableBeanFactory factory =
				(DefaultListableBeanFactory) springContext
						.getAutowireCapableBeanFactory();
		if (!factory.containsSingleton(entityId + "")) {
			return null;
		}
		IStartableEntityLogic entityLogic =
				(IStartableEntityLogic) factory.getSingleton(entityId + "");
		return entityLogic;
	}

	@Override
	protected void registerRunning(long entityId, IStartableEntityLogic instance)
			throws GeneralDOAException {
		SpringUtils.registerBean(springContext, entityId + "", instance);
	}

	@Override
	protected void unregisterRunning(long entityId) throws GeneralDOAException {
		SpringUtils.removeBean(springContext, entityId + "");
	}

}
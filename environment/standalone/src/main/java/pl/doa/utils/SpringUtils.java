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
package pl.doa.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

import pl.doa.GeneralDOAException;

/**
 * @author activey
 * 
 */
public class SpringUtils {

	private final static Logger log =
			LoggerFactory.getLogger(SpringUtils.class);

	public static void registerBean(ApplicationContext springContext,
			String entityUuid, Object object) throws GeneralDOAException {
		log.debug("registering bean: [" + entityUuid + ", "
				+ object.getClass().getName() + "]");
		DefaultListableBeanFactory factory =
				(DefaultListableBeanFactory) springContext
						.getAutowireCapableBeanFactory();
		factory.registerSingleton(entityUuid, object);
	}

	public static void removeBean(ApplicationContext springContext,
			String entityUuid) throws GeneralDOAException {
		if (!springContext.containsBean(entityUuid)) {
			throw new GeneralDOAException("bean with id = [" + entityUuid
					+ "] isn't registered!");
		}
		log.debug("removing bean: [" + entityUuid + "]");
		DefaultListableBeanFactory factory =
				(DefaultListableBeanFactory) springContext
						.getAutowireCapableBeanFactory();
		factory.destroySingleton(entityUuid);
	}
}

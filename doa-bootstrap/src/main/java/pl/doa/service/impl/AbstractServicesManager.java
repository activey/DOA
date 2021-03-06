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

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.agent.IAgent;
import pl.doa.document.IDocument;
import pl.doa.entity.ITransactionCallback;
import pl.doa.service.AbstractServiceDefinitionLogic;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.service.IServicesManager;
import pl.doa.service.ServiceRunnable;

/**
 * @author activey
 * 
 */
public abstract class AbstractServicesManager implements IServicesManager {

	private static final Logger log = LoggerFactory
			.getLogger(AbstractServicesManager.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.doa.service.IServicesManager#executeService(pl.doa.service.IRunningService
	 * , boolean)
	 */
	public final void executeService(IRunningService runningService,
			final boolean asynchronous) throws GeneralDOAException {
		IServiceDefinition definition = null;
		definition = runningService.getServiceDefinition();
		IAgent agent = runningService.getAgent();
		IDocument input = runningService.getInput();

		Runnable runnable =
				new ServiceRunnable(runningService, input, agent, definition) {

					@Override
					public void run() {
						try {
							final AbstractServiceDefinitionLogic logicInstance =
									(AbstractServiceDefinitionLogic) getDoa()
											.instantiateObject(
													definition.getLogicClass(),
													true, asynchronous);
							if (logicInstance == null) {
								throw new GeneralDOAException(
										MessageFormat
												.format("Unable to instantiate service class: [{0}]",
														definition
																.getLogicClass()));
							}
							logicInstance.setRunningService(runningService);
							logicInstance.setDoa(getDoa());
							try {
								logicInstance.align();
							} catch (Throwable t) {
								throw new GeneralDOAException(t);
							}
						} catch (Throwable e) {
							log.error("", e);
						}
					}

				};

		/*
		 * uruchamianie uslugi w odpowiednim trybie
		 */
		if (asynchronous) {

			// TODO zaimplementowac grupe watkow startowych
			new Thread(runnable).start();

			return;
		}

		runnable.run();
	}

	public abstract IDOA getDoa();

	public abstract void setDoa(IDOA doa);

}

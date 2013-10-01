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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author activey
 * 
 */
public class DOAStarter {

	private static final int SLEEP_DELAY = 3000;
	private final static Logger log = LoggerFactory.getLogger(DOAStarter.class);
	private final static Logger heartBeatLog = LoggerFactory
			.getLogger("heart-beat");

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		
		final ClassPathXmlApplicationContext context =
				new ClassPathXmlApplicationContext(new String[] { "doa.xml" });

		// sprawdzanie, czy doa jest juz uruchomione, szukanie pliku doa.pid w
		// aktualnym katalogu
		File pidFile = new File("doa.pid");
		if (pidFile.exists()) {
			log.error("doa is already started!");
			return;
		}
		Runnable spring = new Runnable() {
			public void run() {

				/*
				 * final ApplicationContext applicationContext = new
				 * FileSystemXmlApplicationContext( "var/spring-context.xml");
				 */
				Runtime.getRuntime().addShutdownHook(new Thread() {
					public void run() {
						try {
							IDOA doa =
									(IDOA) BeanFactoryUtils.beanOfType(context,
											IDOA.class);
							log.debug("shutting down DOA ...");
							// usuwanie plik doa.pid
							File pidFile = new File("doa.pid");
							if (pidFile.exists()) {
								pidFile.delete();
							}
							doa.shutdown();
						} catch (GeneralDOAException e) {
							log.error("", e);
						}
					}
				});
				boolean isDOARunning = true;
				// tworzenie pliku doa.pid
				createPidFile();
				IDOA doa;
				try {
							doa =
									(IDOA) BeanFactoryUtils.beanOfType(context,
											IDOA.class);
					doa.startup();
				} catch (GeneralDOAException e1) {
					log.error("", e1);
					return;
				}
				/*
				 * try { doa.startup(); } catch (GeneralDOAException e1) {
				 * log.error("", e1); return; }
				 */
				
				while (isDOARunning) {
					try {
						Thread.currentThread().sleep(SLEEP_DELAY);
						//heartBeatLog.debug("sleeping ...");
						doa =
								(IDOA) BeanFactoryUtils.beanOfType(context,
										IDOA.class);
						isDOARunning = doa.isStartedUp();
					} catch (Exception e) {
						log.error("", e);
					}
				}
			}

			private void createPidFile() {
				String pid = System.getProperty("app.pid");
				if (pid == null) {
					return;
				}
				File newPidFile = new File("doa.pid");
				try {
					boolean pidCreated = newPidFile.createNewFile();
					if (!pidCreated) {
						return;
					}
					log.debug(MessageFormat.format(
                            "creating file for PID: {0}", pid));
					FileWriter writer = new FileWriter(newPidFile);
					writer.write(pid);
					writer.flush();
				} catch (IOException e) {
					log.error("", e);
				}

			}
		};
		Thread springThread = new Thread(spring);
		springThread.start();

	}
}

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
package pl.doa.temp.http.service.auth;

import java.text.MessageFormat;

import pl.doa.GeneralDOAException;
import pl.doa.agent.IAgent;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.field.IListDocumentFieldValue;
import pl.doa.entity.event.impl.ServiceExecutedEvent;
import pl.doa.entity.event.source.impl.ServiceOutputProducedSource;
import pl.doa.service.AbstractServiceDefinitionLogic;
import pl.doa.service.IServiceDefinition;

public class BasicAuthenticationService extends AbstractServiceDefinitionLogic {

	@Override
	public void align() throws GeneralDOAException {
		IDocument input = getInput();
		// pobieranie dokumentu aplikacji
		IDocument applicationDocument =
				(IDocument) input.getFieldValue("applicationDocument");

		// pobieranie referencji do uslugi autoryzacyjnej
		IServiceDefinition authorizationService =
				(IServiceDefinition) applicationDocument
						.getFieldValue("authenticationService");

		// autoryzowanie agenta poprzez uruchomienie uslugi autoryzacyjnej
		ServiceExecutedEvent authEvent =
				waitForEvent(new ServiceOutputProducedSource(
						authorizationService, input));
		IAgent authenticatedAgent = authEvent.getAgent();
		IDocumentDefinition httpResponseDefinition =
				getPossibleOutputDefinition("http_response_definition");

		if (authenticatedAgent == null) {
			IDocument response =
					httpResponseDefinition.createDocumentInstance();
			// dodawanie headerow do autoryzacji BASIC
			IListDocumentFieldValue headers =
					(IListDocumentFieldValue) response
							.getField("headers", true);
			headers.addStringField("WWW-Authenticate", MessageFormat.format(
					"Basic realm=\"{0}\"", applicationDocument
							.getFieldValueAsString("applicationName")));
			response.setFieldValue("httpCode", 401);
			setOutput(response);
			return;
		}
		IDocumentDefinition authOutputDefinition =
				getPossibleOutputDefinition("auth_output");
		IDocument authOutput = authOutputDefinition.createDocumentInstance();
		authOutput.setFieldValue("authenticated", authenticatedAgent);
		setOutput(authOutput);
	}

}

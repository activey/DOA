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
package pl.doa.temp.http.service.auth;

import pl.doa.GeneralDOAException;
import pl.doa.agent.IAgent;
import pl.doa.container.IEntitiesContainer;
import pl.doa.container.IEntitiesIterator;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.IDocumentFieldEvaluator;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.IListDocumentFieldValue;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.entity.event.impl.ServiceExecutedEvent;
import pl.doa.entity.event.source.impl.ServiceOutputProducedSource;
import pl.doa.service.AbstractServiceDefinitionLogic;
import pl.doa.service.IServiceDefinition;

/**
 * @author activey
 */
public class AuthenticationFacadeService extends AbstractServiceDefinitionLogic {


    /*
     * (non-Javadoc)
     *
     * @see pl.doa.temp.service.DOAServiceDefinitionLogic#align()
     */
    @Override
    public void align() throws GeneralDOAException {
        IDocument input = getInput();
        // pobieranie dokumentu aplikacji
        IDocument applicationDocument = (IDocument) input
                .getFieldValue("applicationDocument");
        // sprawdzanie, czy zostalo ustawione pole mechanizmu autentykacji
        final String authentication = applicationDocument
                .getFieldValueAsString("authentication");
        IAgent authenticated = getAgent();
        if (authenticated != null) {

        }

        // szukanie uslugi autentykacyjnej
        IEntitiesContainer authContainer = getServiceDefinition()
                .getContainer();
        IServiceDefinition authenticationService = (IServiceDefinition) authContainer
                .lookupForEntity(new IEntityEvaluator() {
                    @Override
                    public boolean isReturnableEntity(IEntity service) {
                        if (!(service instanceof IServiceDefinition)) {
                            return false;
                        }
                        return authentication.equals(service.getName());
                    }
                }, false);
        if (authenticationService == null) {
            setOutput(createExceptionDocument(
                    "Authentication mechanism not supported: [{0}]",
                    authentication));
            return;
        }
        // uruchamianie uslugi autentykacyjnej
        ServiceExecutedEvent event = waitForEvent(new ServiceOutputProducedSource(
                authenticationService, input));
        IDocument output = event.getServiceOutput();
        if (output.isDefinedBy("/channels/http/auth/auth_output")) {
            authenticated = (IAgent) output.getFieldValue("authenticated");
            // tworzenie dokumentu dla agenta
            IDocument agentDocument = input
                    .createCopy(new IDocumentFieldEvaluator() {

                        @Override
                        public boolean evaluate(IDocumentFieldValue fieldValue) {
                            return fieldValue.getFieldType().isAuthorizable();
                        }
                    });

            IEntitiesContainer fingerprints = authenticated
                    .getFingerprintsContainer(true);

            // usuwanie poprzednich fingerprintow
            fingerprints.iterateEntities(new IEntitiesIterator() {

                private IDocumentDefinition definition;

                @Override
                public void next(IEntity entity) throws GeneralDOAException {
                    IDocument fingerprint = (IDocument) entity;
                    if (!fingerprint.isDefinedBy(definition)) {
                        return;
                    }
                    fingerprint.remove();
                }

                public IEntitiesIterator setDefinition(
                        IDocumentDefinition definition) {
                    this.definition = definition;
                    return this;
                }
            }.setDefinition(agentDocument.getDefinition()),
                    IEntityEvaluator.TYPE_DOCUMENT);

            // dodawanie aktualnego fingerprintu
            fingerprints.addEntity(agentDocument);
        } else if (output.isDefinedBy("/documents/system/exception")) {
            IListDocumentFieldValue stackTrace = (IListDocumentFieldValue) output
                    .getField("stackTrace");
            if (stackTrace != null) {
                Iterable<IDocumentFieldValue> elements = stackTrace
                        .iterateFields();
                for (IDocumentFieldValue element : elements) {
                    System.out.println(">>> " + element.getFieldValue());
                }
            }
        }
        setOutput(output);
    }

}

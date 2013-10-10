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
package pl.doa.service;

import pl.doa.GeneralDOAException;
import pl.doa.agent.IAgent;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.entity.IEntity;

import java.util.List;

public interface IServiceDefinition extends IEntity {

    /**
     * Metoda powinna zachowywac sie roznie, w zaleznosci od ustalonego modelu uruchamiania uslug. Nalezy zadecydowac czy
     * wykonanie metody powoduje stworzenie zupelnie nowej instancji uruchomionej uslugi, czy tez ma korzystac z juz
     * uruchomionej instancji.
     *
     * @param input
     * @return
     */
    public abstract IRunningService executeService(IDocument serviceInput,
                                                   IAgent runAs, final boolean asynchronous)
            throws GeneralDOAException;

    public abstract IRunningService executeService(IDocument serviceInput,
                                                   final boolean asynchronous) throws GeneralDOAException;

    public abstract IDocumentDefinition getInputDefinition();

    public abstract void setInputDefinition(IDocumentDefinition inputDefinition);

    public abstract List<IDocumentDefinition> getPossibleOutputs();

    public abstract IDocumentDefinition getPossibleOutputDefinition(
            final String possibleOutputName);

    public abstract void addPossibleOutputDefinition(
            IDocumentDefinition possibleOutputDefinition);

    public abstract void removePossibleOutputDefinition(
            IDocumentDefinition possibleOutputDefinition);

    public abstract String getLogicClass();

    public abstract void setLogicClass(String logicClass);

    public abstract List<IRunningService> getRunningServices();

    public abstract void addRunning(IRunningService runningService);

}
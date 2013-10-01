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
package pl.doa.service;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.agent.IAgent;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.impl.DetachedDocument;
import pl.doa.entity.IEntityProxy;
import pl.doa.entity.ITransactionCallback;
import pl.doa.entity.event.DetachedEvent;
import pl.doa.entity.event.impl.ServiceExecutedByEvent;
import pl.doa.entity.event.impl.ServiceExecutedEvent;
import pl.doa.entity.event.source.SynchronousEventSource;

/**
 * @author activey
 */
public abstract class AbstractServiceDefinitionLogic implements
        IServiceDefinitionLogic, Serializable {

    private static final long serialVersionUID = 1L;

    private final static Logger log = LoggerFactory
            .getLogger(AbstractServiceDefinitionLogic.class);

    protected transient IDOA doa;

    protected IRunningService runningService;

    /*
     * (non-Javadoc)
     *
     * @see pl.doa.service.IServiceDefinitionLogic#align()
     */
    public abstract void align() throws GeneralDOAException;

    public void setDoa(IDOA doa) {
        this.doa = doa;
    }

    public final <T extends DetachedEvent> T waitForEvent(
            SynchronousEventSource<T> origin) throws GeneralDOAException {
        return origin.waitForEvent(doa);
    }

    /*
     * (non-Javadoc)
     *
     * @see pl.doa.service.IServiceDefinitionLogic#getInput()
     */
    @Override
    public final IDocument getInput() {
        IDocument input = runningService.getInput();
        if (input != null) {
            if (input instanceof IEntityProxy) {
                return input;
            }
            return new DetachedDocument(doa, input);
        } else {
            return null;
        }
    }

    public final IAgent getAgent() {
        IAgent agent = runningService.getAgent();
        return agent;
    }

    public void setOutput(final IDocument output) {
        doa.doInTransaction(new ITransactionCallback<Object>() {

            @Override
            public Object performOperation() throws Exception {
                runningService.setOutput(output);
                return null;
            }
        });

        doa.publishEvent(new ServiceExecutedEvent(runningService));
        doa.publishEvent(new ServiceExecutedByEvent(runningService));
    }

    public final void setAttribute(String attrName, String attrValue) {
        runningService.setAttribute(attrName, attrValue);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /*
     * (non-Javadoc)
     *
     * @see pl.doa.service.IServiceDefinitionLogic#getServiceDefinition()
     */
    @Override
    public IServiceDefinition getServiceDefinition() {
        return getRunningService().getServiceDefinition();
    }

    public void setRunningService(IRunningService runningService) {
        this.runningService = runningService;
    }

    public IRunningService getRunningService() {
        return runningService;
    }

    public IDocument createExceptionDocument(String message, Throwable throwable) {
        return doa.createExceptionDocument(message, throwable);
    }

    public IDocument createExceptionDocument(String message) {
        return doa.createExceptionDocument(message);
    }

    public IDocument createExceptionDocument(Throwable throwable) {
        return doa.createExceptionDocument(throwable);
    }

    public IDocument createExceptionDocument(String template, Object... params) {
        return doa.createExceptionDocument(template, params);
    }

    @Override
    public List<IDocumentDefinition> getPossibleOutputs() {
        return getServiceDefinition().getPossibleOutputs();
    }

    @Override
    public IDocumentDefinition getPossibleOutputDefinition(
            String possibleOutputName) {
        return getServiceDefinition().getPossibleOutputDefinition(
                possibleOutputName);
    }

    @Override
    public IDocumentDefinition getInputDefinition() {
        return getServiceDefinition().getInputDefinition();
    }

    @Override
    public final IDocument getOutput() {
        return runningService.getOutput();
    }

    protected IDocument createOutputDocument(String possibleOutputName)
            throws GeneralDOAException {
        IDocumentDefinition outputDefinition = getPossibleOutputDefinition(possibleOutputName);
        if (outputDefinition == null) {
            return null;
        }
        return outputDefinition.createDocumentInstance();
    }

}
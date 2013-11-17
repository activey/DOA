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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.agent.IAgent;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.entity.IEntity;
import pl.doa.entity.impl.AbstractEntity;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.utils.profile.PerformanceProfiler;
import pl.doa.utils.profile.impl.ExecuteServiceAction;

/**
 * @author activey
 */
public abstract class AbstractServiceDefinition extends AbstractEntity
        implements IServiceDefinition {

    private final static Logger log = LoggerFactory
            .getLogger(AbstractServiceDefinition.class);

    public AbstractServiceDefinition(IDOA doa) {
        super(doa);
    }

    public final IRunningService executeService(IDocument input, IAgent runAs,
            boolean asynchronous) throws GeneralDOAException {
        return PerformanceProfiler.runProfiled(new ExecuteServiceAction(getDoa(),
                this, input, runAs, asynchronous));
    }

    public final IRunningService executeService(IDocument input,
            boolean asynchronous) throws GeneralDOAException {
        return executeService(input, null, asynchronous);
    }

    protected abstract IDocumentDefinition getInputDefinitionImpl();

    public final IDocumentDefinition getInputDefinition() {
        IDocumentDefinition definition = getInputDefinitionImpl();
        if (definition == null) {
            IEntity ancestor = getAncestor();
            if (ancestor != null) {
                return ((IServiceDefinition) ancestor).getInputDefinition();
            }
        }
        return definition;
    }

    protected abstract void setAttributeImpl(String attrName, String attrValue);

    protected abstract List<IDocumentDefinition> getPossibleOutputsImpl();

    public final List<IDocumentDefinition> getPossibleOutputs() {
        List<IDocumentDefinition> allPossible =
                new ArrayList<IDocumentDefinition>();
        IEntity ancestor = getAncestor();
        if (ancestor != null) {
            allPossible.addAll(((IServiceDefinition) ancestor)
                    .getPossibleOutputs());
        }
        allPossible.addAll(getPossibleOutputsImpl());
        return allPossible;
    }

    protected abstract IDocumentDefinition getPossibleOutputDefinitionImpl(
            String possibleOutputName);

    public final IDocumentDefinition getPossibleOutputDefinition(
            String possibleOutputName) {
        IDocumentDefinition outputDef =
                getPossibleOutputDefinitionImpl(possibleOutputName);
        if (outputDef == null) {
            IEntity ancestor = getAncestor();
            if (ancestor != null) {
                return ((IServiceDefinition) ancestor)
                        .getPossibleOutputDefinition(possibleOutputName);
            }
        }
        return outputDef;
    }

    protected abstract void addPossibleOutputDefinitionImpl(
            IDocumentDefinition possibleOutputDefinition);

    protected abstract void removePossibleOutputDefinitionImpl(
            IDocumentDefinition possibleOutputDefinition);

    public final void addPossibleOutputDefinition(
            IDocumentDefinition possibleOutputDefinition) {
        addPossibleOutputDefinitionImpl(possibleOutputDefinition);
    }

    public final void removePossibleOutputDefinition(
            IDocumentDefinition possibleOutputDefinition) {
        removePossibleOutputDefinitionImpl(possibleOutputDefinition);
    }

    protected abstract void setInputDefinitionImpl(
            IDocumentDefinition inputDefinition);

    public final void setInputDefinition(IDocumentDefinition inputDefinition) {
        setInputDefinitionImpl(inputDefinition);
    }

    protected abstract String getLogicClassImpl();

    public final String getLogicClass() {
        String logicClass = getLogicClassImpl();
        if (logicClass == null) {
            IEntity ancestor = getAncestor();
            if (ancestor != null) {
                return ((IServiceDefinition) ancestor).getLogicClass();
            }
        }
        return logicClass;
    }

    protected abstract void setLogicClassImpl(String logicClass);

    public final void setLogicClass(String logicClass) {
        setLogicClassImpl(logicClass);
    }

    protected abstract List<IRunningService> getRunningServicesImpl();

    public final List<IRunningService> getRunningServices() {
        return getRunningServicesImpl();
    }

    protected abstract void addRunningImpl(IRunningService runningService);

    public final void addRunning(IRunningService runningService) {
        if (runningService instanceof DetachedRunningService) {
            DetachedRunningService detached =
                    (DetachedRunningService) runningService;
            try {
                IRunningService storedInstance =
                        (IRunningService) detached.store("/tmp");
                addRunningImpl(storedInstance);
                return;
            } catch (GeneralDOAException e) {
                log.error("", e);
                return;
            }
        }
        addRunningImpl(runningService);
    }

}

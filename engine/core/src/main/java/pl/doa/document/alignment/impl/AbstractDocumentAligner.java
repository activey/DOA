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
package pl.doa.document.alignment.impl;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.alignment.IDocumentAligner;
import pl.doa.document.alignment.IDocumentAlignerLogic;
import pl.doa.entity.impl.AbstractEntity;
import pl.doa.jvm.factory.EntityArtifactDependenciesEvaluator;
import pl.doa.jvm.factory.ObjectFactory;

import static pl.doa.jvm.factory.ObjectFactory.instantiateObject;

/**
 * @author activey
 */
public abstract class AbstractDocumentAligner extends AbstractEntity implements
        IDocumentAligner {

    private final static Logger log = LoggerFactory
            .getLogger(AbstractDocumentAligner.class);

    public AbstractDocumentAligner(IDOA doa) {
        super(doa);
    }

    protected abstract String getLogicClassImpl();

    public final String getLogicClass() {
        return getLogicClassImpl();
    }

    protected abstract void setLogicClassImpl(String logicClass);

    public final void setLogicClass(String logicClass) {
        setLogicClassImpl(logicClass);
    }

    protected abstract IDocumentDefinition getFromDefinitionImpl();

    public final IDocumentDefinition getFromDefinition() {
        return getFromDefinitionImpl();
    }

    protected abstract IDocumentDefinition getToDefinitionImpl();

    public final IDocumentDefinition getToDefinition() {
        return getToDefinitionImpl();
    }

    protected abstract void setToDefinitionImpl(IDocumentDefinition toDefinition);

    public final void setToDefinition(IDocumentDefinition toDefinition) {
        setToDefinitionImpl(toDefinition);
    }


    protected abstract void setFromDefinitionImpl(IDocumentDefinition fromDefinition);

    @Override
    public final void setFromDefinition(IDocumentDefinition fromDefinition) {
        setFromDefinitionImpl(fromDefinition);
    }

    @Override
    public final IDocument align(IDocument input) throws GeneralDOAException {
        IDocumentDefinition toDefinition = getToDefinition();
        log.debug(MessageFormat.format("Aligning from {0} to {1}", input
                .getDefinition().getLocation(), toDefinition.getLocation()));
        IDocumentAlignerLogic logicInstance = createLogicInstance();
        if (logicInstance == null) {
            log.error("unable to create aligner instance!");
            return null;
        }
        return logicInstance.align(input, toDefinition);
    }

    private IDocumentAlignerLogic createLogicInstance()
            throws GeneralDOAException {
        String logicClass = getLogicClass();
        if (logicClass == null) {
            throw new GeneralDOAException("logic class name can't be null!");
        }
        try {
            IDocumentAlignerLogic logicInstance =
                    instantiateObject(getDoa(), logicClass, new EntityArtifactDependenciesEvaluator(this));
            logicInstance.setAligner(this);
            logicInstance.setDoa(getDoa());
            return logicInstance;
        } catch (Exception e) {
            throw new GeneralDOAException(e);
        }

    }

}

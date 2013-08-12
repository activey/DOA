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
package pl.doa.renderer.impl;

import java.io.OutputStream;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.entity.IEntity;
import pl.doa.entity.startable.impl.AbstractStartableEntity;
import pl.doa.renderer.IRenderer;
import pl.doa.renderer.IRendererLogic;
import pl.doa.renderer.IRenderingContext;
import pl.doa.renderer.ITemplateFinder;
import pl.doa.renderer.ITemplateRendererLogic;
import pl.doa.renderer.RenderingContextImpl;
import pl.doa.resource.IStaticResource;

/**
 * @author activey
 */
public abstract class AbstractRenderer extends AbstractStartableEntity
        implements IRenderer {

    private final static Logger log = LoggerFactory
            .getLogger(AbstractRenderer.class);

    public AbstractRenderer(IDOA doa) {
        super(doa);
    }

    protected abstract void setMimetypeImpl(String mimetype);

    public final void setMimetype(String mimetype) {
        setMimetypeImpl(mimetype);
    }

    protected abstract String getMimetypeImpl();

    public final String getMimetype() {
        return getMimetypeImpl();
    }

    @Override
    public final IStaticResource renderEntity(IEntity document,
                                              IStaticResource template) throws GeneralDOAException {
        if (template == null) {
            return renderEntity(document);
        }
        IRendererLogic runningInstance = (IRendererLogic) doa.getRunning(this);
        if (runningInstance == null) {
            log.error(MessageFormat
                    .format("Unable to find running instance of [{0}], creating a new one ...",
                            getLocation()));
            return null;
        }
        if (runningInstance instanceof ITemplateRendererLogic) {
            ITemplateRendererLogic templateRenderer = (ITemplateRendererLogic) runningInstance;
            return templateRenderer.renderEntity(document, template);
        }

		/* Thread.currentThread().setContextClassLoader(previousClassloader); */
        return runningInstance.renderEntity(document);
    }

    @Override
    public final long renderEntity(IEntity document, OutputStream stream,
                                   IStaticResource template) throws GeneralDOAException {
        if (template == null) {
            return renderEntity(document, stream);
        }
        IRendererLogic runningInstance = (IRendererLogic) doa.getRunning(this);
        if (runningInstance == null) {
            log.error(MessageFormat
                    .format("Unable to find running instance of [{0}], creating a new one ...",
                            getLocation()));
        }
        if (runningInstance instanceof ITemplateRendererLogic) {
            ITemplateRendererLogic templateRenderer = (ITemplateRendererLogic) runningInstance;
            return templateRenderer.renderEntity(document, stream, template);
        }
        /* Thread.currentThread().setContextClassLoader(previousClassloader); */
        return runningInstance.renderEntity(document, stream);
    }

    @Override
    public final IStaticResource renderEntity(IEntity document,
                                              ITemplateFinder templateFinder) throws GeneralDOAException {
        if (templateFinder == null) {
            return renderEntity(document);
        }
        IRendererLogic runningInstance = (IRendererLogic) doa.getRunning(this);
        if (runningInstance == null) {
            log.error(MessageFormat
                    .format("Unable to find running instance of [{0}], creating a new one ...",
                            getLocation()));
            return null;
        }
        if (runningInstance instanceof ITemplateRendererLogic) {
            ITemplateRendererLogic templateRenderer = (ITemplateRendererLogic) runningInstance;
            return templateRenderer.renderEntity(document, templateFinder);
        }
		/* Thread.currentThread().setContextClassLoader(previousClassloader); */
        return runningInstance.renderEntity(document);
    }

    @Override
    public final long renderEntity(IEntity document, OutputStream output,
                                   ITemplateFinder templateFinder) throws GeneralDOAException {
        return renderEntity(document, output, templateFinder, null);
    }

    @Override
    public final long renderEntity(IEntity document, OutputStream output,
                                   ITemplateFinder templateFinder, IRenderingContext renderingContext)
            throws GeneralDOAException {
        if (templateFinder == null) {
            return renderEntity(document, output);
        }
        IRendererLogic runningInstance = (IRendererLogic) doa.getRunning(this);
        if (runningInstance == null) {
            log.error(MessageFormat
                    .format("Unable to find running instance of [{0}], creating a new one ...",
                            getLocation()));
        }
        if (runningInstance instanceof ITemplateRendererLogic) {
            ITemplateRendererLogic templateRenderer = (ITemplateRendererLogic) runningInstance;
            return templateRenderer.renderEntity(document, output,
                    templateFinder, renderingContext);
        }
		/* Thread.currentThread().setContextClassLoader(previousClassloader); */
        return runningInstance.renderEntity(document, output, renderingContext);
    }

    @Override
    public final long renderEntity(IEntity document, OutputStream output,
                                   IStaticResource template, IRenderingContext renderingContext)
            throws GeneralDOAException {
        if (template == null) {
            return renderEntity(document, output);
        }
        IRendererLogic runningInstance = (IRendererLogic) doa.getRunning(this);
        if (runningInstance == null) {
            log.error(MessageFormat
                    .format("Unable to find running instance of [{0}], creating a new one ...",
                            getLocation()));
        }
        if (runningInstance instanceof ITemplateRendererLogic) {
            ITemplateRendererLogic templateRenderer = (ITemplateRendererLogic) runningInstance;
            return templateRenderer.renderEntity(document, output, template,
                    renderingContext);
        }
        return runningInstance.renderEntity(document, output, renderingContext);
    }

    @Override
    public final IStaticResource renderEntity(IEntity document)
            throws GeneralDOAException {
        IRendererLogic runningInstance = (IRendererLogic) doa.getRunning(this);
        if (runningInstance == null) {
            log.error(MessageFormat
                    .format("Unable to find running instance of [{0}], creating a new one ...",
                            getLocation()));
            return null;
        }

		/*
		 * TODO ClassLoader previousClassloader =
		 * Thread.currentThread().getContextClassLoader();
		 * Thread.currentThread().setContextClassLoader(
		 * doa.getRepositoryClassLoader());
		 */
        IStaticResource renderedDocument = runningInstance
                .renderEntity(document);
		/* Thread.currentThread().setContextClassLoader(previousClassloader); */
        return renderedDocument;
    }

    @Override
    public final long renderEntity(IEntity document, OutputStream stream)
            throws GeneralDOAException {
        IRendererLogic runningInstance = (IRendererLogic) doa.getRunning(this);
        if (runningInstance == null) {
            log.error(MessageFormat
                    .format("Unable to find running instance of [{0}], creating a new one ...",
                            getLocation()));
        }

		/*
		 * TODO ClassLoader previousClassloader =
		 * Thread.currentThread().getContextClassLoader();
		 * Thread.currentThread().setContextClassLoader(
		 * doa.getRepositoryClassLoader());
		 */
        return runningInstance.renderEntity(document, stream);
		/* Thread.currentThread().setContextClassLoader(previousClassloader); */
    }

    @Override
    public final IRenderingContext createContext() {
        return new RenderingContextImpl();
    }

}

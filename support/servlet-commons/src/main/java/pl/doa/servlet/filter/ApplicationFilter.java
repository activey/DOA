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
package pl.doa.servlet.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.channel.IIncomingChannel;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.entity.IEntity;
import pl.doa.servlet.filter.processor.IRequestProcessor;
import pl.doa.servlet.filter.processor.rest.processor.BasicRestProcessor;
import pl.doa.servlet.profile.HandleRequestAction;
import pl.doa.servlet.profile.ParseHttpRequestAction;
import pl.doa.servlet.profile.RenderResponseAction;
import pl.doa.utils.profile.PerformanceProfiler;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author activey
 */
public class ApplicationFilter implements Filter {

    private static final String DOA_CHANNEL_LOCATION = "/channels/http/http_channel";

    private final static Logger log = LoggerFactory
            .getLogger(ApplicationFilter.class);

    private ServletContext servletContext;

    // all filter mappings defined
    private Collection<String> filterMappings;

    private Collection<IRequestProcessor> processors;

    @Override
    public final void init(FilterConfig filterConfig) throws ServletException {
        this.servletContext = filterConfig.getServletContext();
        this.filterMappings = readFilterMappings(filterConfig);
        this.processors = new ArrayList<IRequestProcessor>();

        initFilter();
    }

    protected void initFilter() {

    }

    private Collection<String> readFilterMappings(FilterConfig filterConfig) {
        String filterName = filterConfig.getFilterName();
        Collection<String> mappings = new ArrayList<String>();
        Collection<String> rawMappings = servletContext.getFilterRegistration(
                filterName).getUrlPatternMappings();
        for (String rawMapping : rawMappings) {
            if (rawMapping.equals("/*")) {
                mappings.add(rawMapping.substring(0, rawMapping.length() - 2));
                continue;
            }
            mappings.add(rawMapping);
        }
        return mappings;
    }

    @Override
    public final void doFilter(ServletRequest request,
                               ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;

        log.debug("Processing HTTP request ...");

        final IDOA doa = (IDOA) servletContext.getAttribute("DOA");
        try {
            // parsowanie requestu
            IDocument requestDocument = PerformanceProfiler
                    .runProfiled(new ParseHttpRequestAction(doa, httpRequest,
                            getChannelReference(doa).getContainer(),
                            filterMappings));

            // uruchamianie procesu
            IDocument responseDocument = PerformanceProfiler
                    .runProfiled(new HandleRequestAction(doa, requestDocument));
            if (responseDocument.isDefinedBy("/documents/system/exception")) {
                log.error("An error has occurred! Continuing filter chain ...");
                chain.doFilter(httpRequest, httpResponse);
                return;
            }

            if (responseDocument
                    .isDefinedBy("/channels/http/http_response_definition")) {
                int httpCode = (Integer) responseDocument.getFieldValue(
                        "httpCode", 200);
                if (httpCode == 200) {
                    // przetwarzanie zlecenia
                    processRequest(requestDocument, responseDocument, doa);
                } else if (httpCode == 404) {
                    chain.doFilter(httpRequest, httpResponse);
                    return;
                }
            }

            // renderowanie wyniku
            IDocument appDocument = (IDocument) requestDocument
                    .getFieldValue("applicationDocument");
            RenderResponseAction renderAction = new RenderResponseAction(doa,
                    httpResponse, responseDocument, appDocument);
            Boolean rendered = PerformanceProfiler.runProfiled(renderAction);
            if (!rendered) {
                chain.doFilter(httpRequest, httpResponse);
                return;
            }
        } catch (GeneralDOAException exception) {
            log.error("", exception);
            chain.doFilter(httpRequest, httpResponse);
        }

    }

    private void processRequest(IDocument requestDocument,
                                IDocument responseDocument, IDOA doa) throws GeneralDOAException {
        String applicationUri = requestDocument
                .getFieldValueAsString("applicationUri");
        IDocument applicationDocument = (IDocument) requestDocument
                .getFieldValue("applicationDocument");
        if (isRootUri(applicationUri)) {
            String defaultUri = applicationDocument
                    .getFieldValueAsString("defaultUri");
            if (defaultUri != null && defaultUri.trim().length() > 0) {
                applicationUri = defaultUri;
            } else {
                applicationUri = "/";
            }
        }

        IEntity entity = null;
        if (processors == null || processors.size() == 0) {
            IEntitiesContainer applicationContainer = applicationDocument
                    .getContainer();
            entity = applicationContainer
                    .lookupEntityByLocation(applicationUri);
            if (entity != null) {
                responseDocument.setFieldValue("response", entity);
                responseDocument.setFieldValue("httpCode", 200);
            } else {
                responseDocument.setFieldValue("httpCode", 404);
            }
        } else {
            for (IRequestProcessor processor : processors) {
                if (!processor.matches(applicationUri)) {
                    continue;
                }
                try {
                    processor.processRequest(doa, requestDocument,
                            responseDocument);
                    break;
                } catch (Exception e) {
                    throw new GeneralDOAException(e);
                }
            }
        }

    }

    @Override
    public final void destroy() {
    }

    private final IIncomingChannel getChannelReference(IDOA doa) {
        IIncomingChannel channel = (IIncomingChannel) doa
                .lookupEntityByLocation(DOA_CHANNEL_LOCATION);
        return channel;
    }


    protected final void registerMapping(String uriPattern, String modifyPattern) {
        processors.add(new BasicRestProcessor(uriPattern, modifyPattern));
    }

    protected final void registerProcessor(IRequestProcessor processor) {
        processors.add(processor);
    }

    private boolean isRootUri(String uri) {
        if (uri == null || uri.length() == 0 || "/".equals(uri)) {
            return true;
        }
        return false;
    }
}

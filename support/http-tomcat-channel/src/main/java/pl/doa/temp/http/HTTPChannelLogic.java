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
package pl.doa.temp.http;

import java.text.MessageFormat;

import javax.servlet.ServletContext;

import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Constants;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.Tomcat.FixContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.artifact.IArtifact;
import pl.doa.channel.impl.AbstractIncomingChannelLogic;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.IListDocumentFieldValue;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.entity.IEntityReference;
import pl.doa.entity.event.IEntityEventDescription;
import pl.doa.jvm.DOAClassLoader;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.temp.http.resource.StaticResourceContext;

/**
 * @author activey
 */
public class HTTPChannelLogic extends AbstractIncomingChannelLogic implements
        HttpChannelConstants {

    private static final String DEFINITION_EXTENSION = "/channels/http/http_filter_definition";
    private static final String SERVICE_HANDLE_SESSION = "/channels/http/handle_session";
    private final static Logger log = LoggerFactory
            .getLogger(HTTPChannelLogic.class);

    private Tomcat webServer;

    private boolean startedUp;

    /*
     * (non-Javadoc)
     *
     * @see pl.doa.temp.channel.DOAChannelLogic#handleIncoming(java.lang.Object)
     */
    public IRunningService handleIncoming(IDocument document)
            throws GeneralDOAException {
        // uruchamianie uslugi, ktora zajmie sie obsluga zadania
        final IServiceDefinition handleSessionService = (IServiceDefinition) doa
                .lookupEntityByLocation(SERVICE_HANDLE_SESSION);
        if (handleSessionService == null) {
            throw new GeneralDOAException("Unable to find service [{0}]",
                    SERVICE_HANDLE_SESSION);
        }
        return handleSessionService.executeService(document, null, false);
    }

    /*
     * (non-Javadoc)
     *
     * @see pl.doa.temp.channel.DOAChannelLogic#isStartedUp()
     */
    public boolean isStartedUp() {
        if (webServer == null) {
            return false;
        }
        return startedUp;
    }

    /*
     * (non-Javadoc)
     *
     * @see pl.doa.temp.channel.DOAChannelLogic#shutdown()
     */
    public void shutdown() throws GeneralDOAException {
        try {
            log.debug("Shutting down HTTP server ...");
            webServer.stop();
            log.debug("HTTP shutted down ...");
            this.webServer = null;
            this.startedUp = false;
        } catch (Exception e) {
            throw new GeneralDOAException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @seepl.doa.temp.channel.DOAChannelLogic#startup(pl.doa.temp.entity.
     * DOAStartableEntity)
     */
    public void startup() throws GeneralDOAException {
        this.webServer = new Tomcat();
        webServer.setPort(Integer.parseInt(channel.getAttribute("port")));

        Iterable<? extends IEntity> children = doa.lookupEntitiesFromLocation("/applications", new IEntityEvaluator() {
            @Override
            public boolean isReturnableEntity(IEntity currentEntity) {
                if (!(currentEntity instanceof IDocument)) {
                    return false;
                }
                IDocument doc = (IDocument) currentEntity;
                return (doc
                        .isDefinedBy("/channels/http/application_context_definition"));
            }
        }, true);

        for (IEntity child : children) {
            try {
                IDocument document = (IDocument) child;
                deployApplication(document.getName(), document);
            } catch (Exception e) {
                log.error("", e);
                continue;
            }
        }
        try {
            log.debug("Starting up HTTP server ...");
            webServer.start();
            log.debug("HTTP server started ...");

            this.startedUp = true;
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private void deployApplication(String applicationName,
                                   IDocument applicationDocument) throws Exception {
        String appName = applicationDocument
                .getFieldValueAsString("applicationName");
        log.debug(MessageFormat.format("Initializing web application: [{0}]",
                appName));

        // kontekst aplikacji
        String contextPath = "/" + applicationName;

        // kontener aplikacji
        IEntitiesContainer applicationContainer = applicationDocument
                .getContainer();
        // tworzenie nowego kontekstu
        Context context = new StandardContext();

        // ustawianie custom classloadera
        DOAClassLoader applicationLoader = new DOAClassLoader(doa, getClass()
                .getClassLoader(), new IEntityEvaluator() {

            @Override
            public boolean isReturnableEntity(IEntity currentEntity) {
                if (!(currentEntity instanceof IArtifact)) {
                    return false;
                }
                IArtifact artifact = (IArtifact) currentEntity;
                String artifactId = artifact.getArtifactId();
                String groupId = artifact.getGroupId();
                if ((artifactId != null && artifactId.contains("servlet-api"))
                        || (groupId != null && groupId
                        .startsWith("org.apache.tomcat"))) {
                    log.debug(MessageFormat.format(
                            "Ignoring [{0}] artifact ...", artifactId));
                    return false;
                }
                return true;
            }
        });
        WebappLoader loader = new WebappLoader(applicationLoader);
        loader.setSearchExternalFirst(true);
        loader.setDelegate(true);
        loader.setReloadable(true);
        context.setLoader(loader);

        context.addWelcomeFile("index.html");
        context.setResources(new StaticResourceContext(doa, applicationDocument));
        context.setName(applicationName);
        context.setPath(contextPath);
        context.setDocBase(".");
        context.addLifecycleListener(new FixContextListener());

        ContextConfig ctxCfg = new ContextConfig();
        ctxCfg.setDefaultWebXml(Constants.NoDefaultWebXml);
        context.addLifecycleListener(ctxCfg);
        Wrapper servlet = webServer.addServlet(context, "default",
                "org.apache.catalina.servlets.DefaultServlet");
        servlet.setLoadOnStartup(1);
        context.addServletMapping("/", "default");
        context.setSessionTimeout(30);

        webServer.getHost().addChild(context);

        // dodawanie zmiennych do kontekstu
        ServletContext servletContext = context.getServletContext();
        servletContext.setAttribute("DOA", doa);
        servletContext.setAttribute("DOA.application.container",
                applicationContainer.getLocation());

        // rejestrowanie filtrow, ktore sa opsane jako dokumenty w kontenerze
        // "filters"
        initApplicationExtensions(context, applicationContainer);

        log.debug(MessageFormat.format("Web application [{0}] initialized ..",
                appName));
    }

    private void initApplicationExtensions(Context context,
                                           IEntitiesContainer applicationContainer) throws GeneralDOAException {
        IDocumentDefinition extensionDefinition = (IDocumentDefinition) doa
                .lookupEntityByLocation(DEFINITION_EXTENSION);
        if (extensionDefinition == null) {
            log.debug(MessageFormat
                    .format("Unable to find definition: [{0}], skipping extensions initialization ...",
                            DEFINITION_EXTENSION));
            return;
        }

        IEntitiesContainer filtersContainer = (IEntitiesContainer) applicationContainer
                .getEntityByName("filters");
        if (filtersContainer != null) {
            Iterable<IDocument> filters = (Iterable<IDocument>) filtersContainer
                    .getEntities(new IEntityEvaluator() {

                        @Override
                        public boolean isReturnableEntity(IEntity currentEntity) {
                            return currentEntity instanceof IDocument;
                        }
                    });
            for (IDocument filter : filters) {
                IDocument extensionFilter = null;

                if (!filter.isDefinedBy(extensionDefinition)) {
                    log.debug("Got different type of filter, trying to align ...");
                    extensionFilter = filter.align(extensionDefinition);
                    if (extensionFilter == null) {
                        log.warn(MessageFormat.format(
                                "Unable to initialize extension: {0}",
                                filter.getName()));
                        continue;
                    }
                } else {
                    extensionFilter = filter;
                }

                log.debug(MessageFormat.format(
                        "Initializing http filter: [{0}]",
                        extensionFilter.getName()));

                String filterClass = extensionFilter
                        .getFieldValueAsString("filterClass");
                String filterMapping = extensionFilter
                        .getFieldValueAsString("filterMapping");

                // tworzenie instancji filtra
                try {
                    FilterDef filterDef = new FilterDef();
                    filterDef.setFilterClass(filterClass);
                    filterDef.setFilterName(extensionFilter.getName());
                    context.addFilterDef(filterDef);

                    IListDocumentFieldValue initParams = (IListDocumentFieldValue) extensionFilter
                            .getField("initParams");
                    if (initParams != null) {
                        Iterable<IDocumentFieldValue> paramsList = initParams
                                .iterateFields();
                        for (IDocumentFieldValue paramField : paramsList) {
                            filterDef.addInitParameter(
                                    paramField.getFieldName(),
                                    paramField.getFieldValueAsString());
                        }
                    }

                    FilterMap map = new FilterMap();
                    map.addURLPattern(filterMapping);
                    map.setFilterName(extensionFilter.getName());
                    context.addFilterMap(map);
                } catch (Exception e) {
                    log.error(
                            MessageFormat
                                    .format("Unable to initialize application extension: [{0}]",
                                            filter.getName()), e);
                }
            }
        }

    }

    @Override
    public void handleEvent(IEntityEventDescription eventDescription)
            throws Exception {
        IEntity newEntity = eventDescription
                .getReferenceProperty("createdEntity");
        if (newEntity == null) {
            return;
        }
        if (!(newEntity instanceof IEntityReference)) {
            return;
        }
        IEntityReference ref = (IEntityReference) newEntity;
        IEntity refEntity = ref.getEntity();
        if (!(refEntity instanceof IDocument)) {
            return;
        }
        IDocument document = (IDocument) refEntity;
        if (!(document
                .isDefinedBy("/channels/http/application_context_definition"))) {
            return;
        }
        deployApplication(ref.getName(), document);
    }

}

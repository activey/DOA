/**
 *
 */
package pl.doa.wicket;

import javax.servlet.ServletContext;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.agent.IAgent;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.entity.IEntity;
import pl.doa.wicket.auth.DOAAuthenticatedWebSession;
import pl.doa.wicket.auth.IAuthConfig;
import pl.doa.wicket.decorator.IDecoratorLocator;
import pl.doa.wicket.decorator.IEntityDecorator;
import pl.doa.wicket.decorator.impl.DefaultDecoratorLocator;
import pl.doa.wicket.model.EntityModel;
import pl.doa.wicket.mount.container.AgentEntitiesContainerMapper;
import pl.doa.wicket.mount.container.EntitiesContainerMapper;
import pl.doa.wicket.mount.container.ResourcesContainerMapper;
import pl.doa.wicket.ui.page.EntityPage;

/**
 * @author activey
 */
public abstract class WicketDOAApplication extends AuthenticatedWebApplication {

    private final static Logger log = LoggerFactory
            .getLogger(WicketDOAApplication.class);

    protected transient IDecoratorLocator decoratorLocator;

    /* (non-Javadoc)
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public final Class<? extends Page> getHomePage() {
        return getStartingPage();
    }

    protected abstract Class<? extends EntityPage<? extends IEntity>> getStartingPage();

    public IAuthConfig getAuthConfig() {
        return null;
    }

    protected void initDOAApplication() throws Exception {

    }

    protected IDecoratorLocator getDecoratorLocator() {
        return new DefaultDecoratorLocator();
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return null;
    }

    @Override
    protected Class<? extends DOAAuthenticatedWebSession> getWebSessionClass() {
        return DOAAuthenticatedWebSession.class;
    }

    @Override
    protected final void init() {
        super.init();
        getMarkupSettings().setStripWicketTags(true);

        // ustalanie lokalizatora dekotarorow
        this.decoratorLocator = getDecoratorLocator();

        // szukanie kontenera aplikacji
        IEntitiesContainer applicationContainer = getApplicationContainer();
        if (applicationContainer == null) {
            throw new IllegalStateException(
                    "Unable to find application container");
        }
        log.debug("Using application container: ["
                + applicationContainer.getLocation() + "]");
        try {
            initDOAApplication();
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public final void mountContainer(String mountPath,
                                     Class<? extends EntityPage<? extends IEntity>> pageClass,
                                     String containerLocation) {
        IRequestMapper mapper =
                new EntitiesContainerMapper(mountPath, pageClass,
                        containerLocation);
        mount(mapper);
    }

    public final void mountAgentContainer(String mountPath,
                                          Class<? extends EntityPage<? extends IEntity>> pageClass,
                                          String containerLocation) {
        IRequestMapper mapper =
                new AgentEntitiesContainerMapper(mountPath, pageClass,
                        containerLocation);
        mount(mapper);
    }

    public final <T extends IEntity> void mountAgentContainer(String mountPath,
                                                              Class<? extends EntityPage<T>> pageClass, String containerLocation,
                                                              Class<T> entityType) {
        IRequestMapper mapper =
                new AgentEntitiesContainerMapper(mountPath, pageClass,
                        containerLocation);
        mount(mapper);
    }

    public final void mountResourcesContainer(String mountPath,
                                              String containerLocation) {
        mount(new ResourcesContainerMapper(mountPath, containerLocation));
    }

    public final <T extends IEntityDecorator<? extends IEntity>> void registerDecorator(
            String relativeEntityLocation, final Class<T> decoratorClass) {
        IEntitiesContainer appContainer = getApplicationContainer();
        IEntity entity =
                appContainer.lookupEntityByLocation(relativeEntityLocation);

        decoratorLocator.registerDecorator(entity.getId(), decoratorClass);
    }

    public final IDOA getDoa() {
        ServletContext context = WebApplication.get().getServletContext();
        return (IDOA) context.getAttribute("DOA");
    }

    public final IEntitiesContainer getApplicationContainer() {
        IDOA doa = getDoa();
        if (doa == null) {
            return null;
        }
        String containerLocation = getApplicationContainerLocation();
        if (containerLocation == null) {
            return null;
        }
        return (IEntitiesContainer) doa
                .lookupEntityByLocation(containerLocation);
    }

    public final String getApplicationContainerLocation() {
        ServletContext context = getServletContext();
        String containerLocation =
                (String) context.getAttribute("DOA.application.container");
        return containerLocation;
    }

    public <T extends IEntity> Component decorateEntity(T entity,
                                                        String componentId) throws Exception {
        IModel<T> entityModel = new EntityModel<T>(entity);
        if (decoratorLocator == null) {
            return null;
        }
        IEntityDecorator<T> decorator =
                decoratorLocator.locateDecorator(entityModel.getObject());
        if (decorator == null) {
            return null;
        }
        return decorator.decorate(entityModel, componentId);
    }

    public <T extends IEntity> Component decorateEntity(IModel<T> entityModel,
                                                        String componentId) throws Exception {
        if (decoratorLocator == null) {
            return null;
        }
        IEntityDecorator<T> decorator =
                decoratorLocator.locateDecorator(entityModel.getObject());
        if (decorator == null) {
            return null;
        }
        return decorator.decorate(entityModel, componentId);
    }

    public static IAgent getAgent() {
        return DOAAuthenticatedWebSession.get().getAgent();
    }

    public static IAgent getAgent(IDocument fingerprint)
            throws GeneralDOAException {
        return DOAAuthenticatedWebSession.get().getAgent(fingerprint);
    }

    public static WicketDOAApplication get() {
        WebApplication application = WebApplication.get();

        if (application instanceof WicketDOAApplication == false) {
            throw new WicketRuntimeException(
                    "The application attached to the current thread is not a "
                            + WicketDOAApplication.class.getSimpleName());
        }

        return (WicketDOAApplication) application;
    }

    @Override
    public Session newSession(Request request, Response response) {
        return new DOAAuthenticatedWebSession(request);
    }

}
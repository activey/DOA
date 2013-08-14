/**
 *
 */
package pl.doa.servlet.listener;

import java.text.MessageFormat;
import java.util.EnumSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.IDOA;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.IListDocumentFieldValue;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.servlet.filter.ApplicationFilter;

/**
 * @author activey
 */
public class ApplicationContextListener implements ServletContextListener {

    private final static Logger log = LoggerFactory
            .getLogger(ApplicationContextListener.class);


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        IDOA doa;
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            doa = (IDOA) envCtx.lookup("DOA");
        } catch (Exception e) {
            log.error("", e);
            return;
        }
        if (doa == null) {
            log.error("Unable to find DOA reference in JNDI context!");
            return;
        }
        log.debug("Found DOA Reference: " + doa);

        ServletContext context = sce.getServletContext();
        String contextPath = context.getContextPath();
        final String applicationName = contextPath.substring(1);
        // sprawdzanie, czy istnieje aplikacja dla tego kontekstu
        IEntitiesContainer applicationContainer =
                (IEntitiesContainer) doa.lookup("/applications",
                        new IEntityEvaluator() {

                            @Override
                            public boolean isReturnableEntity(
                                    IEntity currentEntity) {
                                if (!(currentEntity instanceof IEntitiesContainer)) {
                                    return false;
                                }
                                return currentEntity.getName().equals(
                                        applicationName);
                            }
                        });
        if (applicationContainer == null) {
            log.error("Unable to find application for context " + contextPath);
            return;
        }

        // rejestrowanie filtrow, ktore sa opsane jako dokumenty w kontenerze "filters"
        IEntitiesContainer filtersContainer =
                (IEntitiesContainer) applicationContainer
                        .getEntityByName("filters");
        if (filtersContainer != null) {
            Iterable<IDocument> filters =
                    (Iterable<IDocument>) filtersContainer
                            .getEntities(new IEntityEvaluator() {

                                @Override
                                public boolean isReturnableEntity(
                                        IEntity currentEntity) {
                                    return false;
                                }
                            });
            for (IDocument filter : filters) {
                log.debug(MessageFormat.format(
                        "Initializing http filter: [{0}]", filter.getName()));

                String filterClass =
                        filter.getFieldValueAsString("filterClass");
                String filterMapping =
                        filter.getFieldValueAsString("filterMapping");
                Dynamic filterInstance =
                        context.addFilter(filter.getName(), filterClass);

                IListDocumentFieldValue initParams =
                        (IListDocumentFieldValue) filter.getField("initParams");
                if (initParams != null) {
                    Iterable<IDocumentFieldValue> paramsList =
                            initParams.iterateFields();
                    for (IDocumentFieldValue paramField : paramsList) {
                        filterInstance.setInitParameter(
                                paramField.getFieldName(),
                                paramField.getFieldValueAsString());
                    }
                }

                filterInstance
                        .addMappingForUrlPatterns(
                                EnumSet.of(DispatcherType.REQUEST), true,
                                filterMapping);
            }
        }

		/* rejestrowanie filtra dla aplikacji, 
           ktory bedzie przekierowywal requesty do kanalu komunikacyjnego */
        log.debug("Initializing Application Filter for context: ["
                + context.getContextPath() + "] ...");
        Dynamic createdFilter =
                context.addFilter(context.getContextPath(), ApplicationFilter.class.getName());
        createdFilter.addMappingForUrlPatterns(
                EnumSet.of(DispatcherType.REQUEST), true, "/*");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }


}
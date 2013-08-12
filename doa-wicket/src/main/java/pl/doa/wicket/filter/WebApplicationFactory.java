package pl.doa.wicket.filter;

import java.text.MessageFormat;

import javax.servlet.ServletContext;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.protocol.http.IWebApplicationFactory;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.IDOA;
import pl.doa.artifact.IArtifact;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.jvm.DOAClassLoader;

public class WebApplicationFactory implements IWebApplicationFactory {

    private final static Logger log = LoggerFactory
            .getLogger(WebApplicationFactory.class);

    @Override
    public WebApplication createApplication(WicketFilter filter) {
        ServletContext servletContext = filter.getFilterConfig()
                .getServletContext();

        String applicationClassName = filter.getFilterConfig()
                .getInitParameter("applicationClassName");
        IDOA doa = (IDOA) servletContext.getAttribute("DOA");
        try {
            // ustawianie custom classloadera
            DOAClassLoader applicationLoader = new DOAClassLoader(doa,
                    WebApplication.class.getClassLoader(),
                    new IEntityEvaluator() {

                        @Override
                        public boolean isReturnableEntity(IEntity currentEntity) {
                            if (!(currentEntity instanceof IArtifact)) {
                                return false;
                            }
                            IArtifact artifact = (IArtifact) currentEntity;
                            String artifactId = artifact.getArtifactId();
                            String groupId = artifact.getGroupId();
                            if ((artifactId != null && artifactId
                                    .contains("servlet-api"))
                                    || (groupId != null && groupId
                                    .startsWith("org.apache.tomcat"))) {
                                log.debug(MessageFormat.format(
                                        "Ignoring [{0}] artifact ...",
                                        artifactId));
                                return false;
                            }
                            return true;
                        }
                    });

            Class<?> applicationClass = Class.forName(applicationClassName,
                    false, applicationLoader);
            if (WebApplication.class.isAssignableFrom(applicationClass)) {
                return (WebApplication) applicationClass.newInstance();
            } else {
                throw new WicketRuntimeException("Application class "
                        + applicationClassName
                        + " must be a subclass of WebApplication");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void destroy(WicketFilter filter) {

    }
}

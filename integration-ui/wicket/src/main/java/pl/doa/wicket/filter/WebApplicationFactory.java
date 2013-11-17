package pl.doa.wicket.filter;

import org.apache.wicket.protocol.http.IWebApplicationFactory;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketFilter;
import pl.doa.IDOA;
import pl.doa.servlet.classloader.HttpExcludedEvaluator;

import javax.servlet.ServletContext;

import static pl.doa.jvm.factory.ObjectFactory.instantiateObject;

public class WebApplicationFactory implements IWebApplicationFactory {

    @Override
    public WebApplication createApplication(WicketFilter filter) {
        ServletContext servletContext = filter.getFilterConfig()
                .getServletContext();

        String applicationClassName = filter.getFilterConfig()
                .getInitParameter("applicationClassName");
        IDOA doa = (IDOA) servletContext.getAttribute("DOA");
        return instantiateObject(doa, applicationClassName, WebApplication.class.getClassLoader(),
                new HttpExcludedEvaluator());
    }

    @Override
    public void destroy(WicketFilter filter) {

    }
}

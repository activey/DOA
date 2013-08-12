/**
 *
 */
package pl.doa.wicket.filter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.apache.wicket.protocol.http.IWebApplicationFactory;

/**
 * @author activey
 */
public class WicketFilter extends org.apache.wicket.protocol.http.WicketFilter {

    @Override
    public void init(boolean isServlet, FilterConfig filterConfig)
            throws ServletException {
        super.init(isServlet, filterConfig);
    }

    @Override
    protected ClassLoader getClassLoader() {
        return this.getClass().getClassLoader();
    }

    @Override
    protected IWebApplicationFactory getApplicationFactory() {
        return new WebApplicationFactory();
    }

    @Override
    protected String getFilterPathFromAnnotation(boolean isServlet) {
        return "";
    }
}
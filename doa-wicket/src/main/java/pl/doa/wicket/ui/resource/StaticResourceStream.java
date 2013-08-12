/**
 *
 */
package pl.doa.wicket.ui.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.time.Time;

import pl.doa.GeneralDOAException;
import pl.doa.resource.IStaticResource;
import pl.doa.wicket.model.EntityModel;

/**
 * @author activey
 */
public class StaticResourceStream implements IResourceStream {

    private IModel<IStaticResource> resourceModel;
    private Locale locale = Locale.getDefault();
    private String style;
    private String variation;
    private InputStream stream;

    public StaticResourceStream(IModel<IStaticResource> resourceModel) {
        this.resourceModel = resourceModel;
    }

    public StaticResourceStream(IStaticResource resource) {
        this(new EntityModel<IStaticResource>(resource));
    }

    @Override
    public Time lastModifiedTime() {
        return Time.valueOf(resourceModel.getObject().getLastModified());
    }

    /* (non-Javadoc)
     * @see org.apache.wicket.util.resource.IResourceStream#getContentType()
     */
    @Override
    public String getContentType() {
        return resourceModel.getObject().getMimetype();
    }

    /* (non-Javadoc)
     * @see org.apache.wicket.util.resource.IResourceStream#length()
     */
    @Override
    public Bytes length() {
        return Bytes.bytes(resourceModel.getObject().getContentSize());
    }

    /* (non-Javadoc)
     * @see org.apache.wicket.util.resource.IResourceStream#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws ResourceStreamNotFoundException {
        if (this.stream == null) {
            try {
                this.stream = resourceModel.getObject().getContentStream();
            } catch (GeneralDOAException e) {
                throw new ResourceStreamNotFoundException(e);
            }
        }
        return this.stream;

    }

    /* (non-Javadoc)
     * @see org.apache.wicket.util.resource.IResourceStream#close()
     */
    @Override
    public void close() throws IOException {
        if (this.stream == null) {
            return;
        }
        this.stream.close();
    }

    /* (non-Javadoc)
     * @see org.apache.wicket.util.resource.IResourceStream#getLocale()
     */
    @Override
    public Locale getLocale() {
        return this.locale;
    }

    /* (non-Javadoc)
     * @see org.apache.wicket.util.resource.IResourceStream#setLocale(java.util.Locale)
     */
    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /* (non-Javadoc)
     * @see org.apache.wicket.util.resource.IResourceStream#getStyle()
     */
    @Override
    public String getStyle() {
        return this.style;
    }

    /* (non-Javadoc)
     * @see org.apache.wicket.util.resource.IResourceStream#setStyle(java.lang.String)
     */
    @Override
    public void setStyle(String style) {
        this.style = style;
    }

    /* (non-Javadoc)
     * @see org.apache.wicket.util.resource.IResourceStream#getVariation()
     */
    @Override
    public String getVariation() {
        return this.variation;
    }

    /* (non-Javadoc)
     * @see org.apache.wicket.util.resource.IResourceStream#setVariation(java.lang.String)
     */
    @Override
    public void setVariation(String variation) {
        this.variation = variation;
    }

}

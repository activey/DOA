/**
 *
 */
package pl.doa.wicket.ui.resource;

import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.resource.IStaticResource;
import pl.doa.wicket.model.EntityModel;

/**
 * @author activey
 */
public class StaticResourceReference extends ResourceReference {

    private final static Logger log = LoggerFactory
            .getLogger(StaticResourceReference.class);

    private IModel<IStaticResource> resourceModel;

    public StaticResourceReference(IModel<IStaticResource> resourceModel) {
        this((resourceModel == null) ? null : resourceModel.getObject());
    }

    public StaticResourceReference(IStaticResource resource) {
        super(ResourceReference.class, "static-resource-"
                + ((resource == null) ? "null" : resource.getId())
                + ((resource == null) ? "null" : resource.getName()));
        this.resourceModel = new EntityModel<IStaticResource>(resource);
    }

    /* (non-Javadoc)
     * @see org.apache.wicket.request.resource.ResourceReference#getResource()
     */
    @Override
    public IResource getResource() {
        return new StaticResource(resourceModel);
    }

    private class StaticResource extends DynamicImageResource {

        private IModel<IStaticResource> resourceModel;

        public StaticResource(IModel<IStaticResource> resourceModel) {
            this.resourceModel = resourceModel;
        }

        public StaticResource(IStaticResource resource) {
            this(new EntityModel<IStaticResource>(resource));
        }

        @Override
        protected byte[] getImageData(Attributes attributes) {
            IStaticResource res = resourceModel.getObject();
            if (res == null) {
                return null;
            }
            try {
                return res.getContent();
            } catch (GeneralDOAException e) {
                log.error("", e);
            }
            return null;
        }

        @Override
        protected void configureResponse(ResourceResponse response,
                                         Attributes attributes) {
            IStaticResource res = resourceModel.getObject();
            if (res == null) {
                return;
            }
            response.setContentType(res.getMimetype());
            response.setContentLength(res.getContentSize());
            response.setFileName(res.getName());
        }
    }

    public IModel<IStaticResource> getResourceModel() {
        return resourceModel;
    }

}

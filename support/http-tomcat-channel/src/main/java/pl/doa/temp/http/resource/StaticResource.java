/**
 *
 */
package pl.doa.temp.http.resource;

import java.io.IOException;
import java.io.InputStream;

import org.apache.naming.resources.Resource;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.entity.IEntity;
import pl.doa.resource.IStaticResource;

/**
 * @author activey
 */
public class StaticResource extends Resource {

    private String resourceLocation;
    private IDOA doa;

    public StaticResource(IStaticResource resource, IDOA doa) {
        this.doa = doa;
        this.resourceLocation = resource.getLocation();
    }

    @Override
    public InputStream streamContent() throws IOException {
        try {
            return getStaticResource().getContentStream();
        } catch (GeneralDOAException e) {
            return null;
        }
    }

    @Override
    public byte[] getContent() {
        try {
            return getStaticResource().getContent();
        } catch (GeneralDOAException e) {
            return null;
        }
    }

    private IStaticResource getStaticResource() {
        IEntity entity = doa.lookupEntityByLocation(resourceLocation);
        if (!(entity instanceof IStaticResource)) {
            return null;
        }
        return (IStaticResource) entity;
    }
}

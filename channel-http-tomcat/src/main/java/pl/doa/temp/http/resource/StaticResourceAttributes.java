/**
 * 
 */
package pl.doa.temp.http.resource;

import java.util.Date;

import org.apache.naming.resources.ResourceAttributes;

import pl.doa.IDOA;
import pl.doa.entity.IEntity;
import pl.doa.resource.IStaticResource;

/**
 * @author activey
 * 
 */
public class StaticResourceAttributes extends ResourceAttributes {

	private String resourceLocation;
	private IDOA doa;

	public StaticResourceAttributes(IStaticResource resource, IDOA doa) {
		this.doa = doa;
		this.resourceLocation = resource.getLocation();
	}

	/**
	 * Is collection.
	 */
	@Override
	public boolean isCollection() {
		return false;
	}

	/**
	 * Get content length.
	 * 
	 * @return content length value
	 */
	@Override
	public long getContentLength() {
		return getStaticResource().getContentSize();
	}

	/**
	 * Get creation time.
	 * 
	 * @return creation time value
	 */
	@Override
	public long getCreation() {
		return getStaticResource().getCreated().getTime();
	}

	/**
	 * Get creation date.
	 * 
	 * @return Creation date value
	 */
	@Override
	public Date getCreationDate() {
		return getStaticResource().getCreated();
	}

	/**
	 * Get last modified time.
	 * 
	 * @return lastModified time value
	 */
	@Override
	public long getLastModified() {
		return getStaticResource().getLastModified().getTime();
	}

	/**
	 * Get lastModified date.
	 * 
	 * @return LastModified date value
	 */
	@Override
	public Date getLastModifiedDate() {
		return getStaticResource().getLastModified();
	}

	/**
	 * Get name.
	 * 
	 * @return Name value
	 */
	@Override
	public String getName() {
		return getStaticResource().getName();
	}


	/**
	 * Get canonical path.
	 * 
	 * @return String the file's canonical path
	 */
	@Override
	public String getCanonicalPath() {
		return getStaticResource().getLocation();
	}

	private IStaticResource getStaticResource() {
		IEntity entity = doa.lookupEntityByLocation(resourceLocation);
		if (!(entity instanceof IStaticResource)) {
			return null;
		}
		return (IStaticResource) entity;
	}
	
	@Override
	public String getMimeType() {
		return getStaticResource().getMimetype();
	}
}

/**
 * 
 */
package pl.doa.wicket.mount;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.request.Url;
import org.apache.wicket.request.mapper.parameter.IPageParametersEncoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.IDOA;
import pl.doa.container.IEntitiesContainer;
import pl.doa.impl.EntityLocationIterator;
import pl.doa.utils.IteratorCollection;
import pl.doa.wicket.WicketDOAApplication;

/**
 * @author activey
 * 
 */
public class EntityLocationEncoder implements IPageParametersEncoder {

	private final static Logger log = LoggerFactory
			.getLogger(EntityLocationEncoder.class);

	private final String containerLocation;

	public EntityLocationEncoder(String containerLocation) {
		this.containerLocation = containerLocation;
	}

	protected IEntitiesContainer getBaseContainer() {
		return WicketDOAApplication.get().getApplicationContainer();
	}

	@Override
	public Url encodePageParameters(PageParameters pageParameters) {
		StringValue locationParam = pageParameters.get("location");
		if (locationParam == null || locationParam.isEmpty()
				|| locationParam.isNull()) {
			return null;
		}

		IDOA doa = WicketDOAApplication.get().getDoa();
		Url url = new Url();
		IEntitiesContainer entitiesContainer =
				(IEntitiesContainer) getBaseContainer().lookupEntityByLocation(
						new EntityLocationIterator(containerLocation, true));
		if (entitiesContainer == null) {
			/*EntityLocationIterator iterator =
					new EntityLocationIterator(locationParam.toString());
			IteratorCollection<String> col =
					new IteratorCollection<String>(iterator);
			url.getSegments().addAll(col);*/
			return null;
		} else {
			String containerLocation = entitiesContainer.getLocation();
			String entityLocation = locationParam.toString();
			if (entityLocation == null) {
				return url;
			}
			try {
				entityLocation = URLDecoder.decode(entityLocation, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				log.error("", e);
			}

			StringValue doaParameter = pageParameters.get("doa");
			if (doaParameter != null && !doaParameter.isEmpty()) {
				long doaId = doaParameter.toLong(0);
				if (doaId > 0) {
					IDOA otherDoa = (IDOA) doa.lookupByUUID(doaId);
					entityLocation = otherDoa.getLocation() + entityLocation;
				}
			}

			if (entityLocation.startsWith(containerLocation)) {
				entityLocation =
						entityLocation.substring(containerLocation.length());
			}
			EntityLocationIterator iterator =
					new EntityLocationIterator(entityLocation);
			IteratorCollection<String> col =
					new IteratorCollection<String>(iterator);
			url.getSegments().addAll(col);
		}

		return url;
	}

	@Override
	public PageParameters decodePageParameters(Url url) {
		List<String> segments = url.getSegments();
		if (segments.size() < 1) {
			return new PageParameters();
		}
		PageParameters parameters = new PageParameters();
		Url docUrl = new Url();
		List<String> urlSegments = docUrl.getSegments();
		urlSegments.addAll(locationToCollection(getBaseContainer()
				.getLocation()));
		urlSegments.addAll(locationToCollection(containerLocation));
		urlSegments.addAll(segments);

		String entityUrl = docUrl.toString();
		try {
			entityUrl = URLDecoder.decode(entityUrl, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("", e);
		}
		parameters.add("location", "/" + entityUrl);
		return parameters;
	}

	private Collection<String> locationToCollection(String location) {
		EntityLocationIterator iterator = new EntityLocationIterator(location);
		IteratorCollection<String> col =
				new IteratorCollection<String>(iterator);
		return col;
	}

}

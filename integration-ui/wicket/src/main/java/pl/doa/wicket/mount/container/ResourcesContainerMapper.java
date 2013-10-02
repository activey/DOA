package pl.doa.wicket.mount.container;

import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.handler.resource.ResourceReferenceRequestHandler;
import org.apache.wicket.request.mapper.AbstractMapper;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.string.StringValue;

import pl.doa.container.IEntitiesContainer;
import pl.doa.resource.IStaticResource;
import pl.doa.wicket.WicketDOAApplication;
import pl.doa.wicket.model.EntityModel;
import pl.doa.wicket.mount.EntityLocationEncoder;
import pl.doa.wicket.ui.resource.StaticResourceReference;

public class ResourcesContainerMapper extends AbstractMapper implements
		IRequestMapper {

	private final String[] mountSegments;

	private final String containerLocation;

	public ResourcesContainerMapper(String mountPath, String containerLocation) {
		this.containerLocation = containerLocation;
		this.mountSegments = getMountSegments(mountPath);
	}

	@Override
	public IRequestHandler mapRequest(Request request) {
		final Url url = new Url(request.getUrl());
		PageParameters parameters =
				extractPageParameters(request, mountSegments.length,
						new EntityLocationEncoder(containerLocation));
		if (urlStartsWith(url, mountSegments) == false) {
			return null;
		}
		StringValue location = parameters.get("location");
		return new ResourceReferenceRequestHandler(new StaticResourceReference(
				new EntityModel<IStaticResource>(location.toString(), false)));
	}

	@Override
	public int getCompatibilityScore(Request request) {
		return 0;
	}

	@Override
	public Url mapHandler(IRequestHandler requestHandler) {
		if ((requestHandler instanceof ResourceReferenceRequestHandler) == false) {
			return null;
		}
		ResourceReferenceRequestHandler handler =
				(ResourceReferenceRequestHandler) requestHandler;
		ResourceReference ref = handler.getResourceReference();
		if ((ref instanceof StaticResourceReference) == false) {
			return null;
		}
		StaticResourceReference resRef = (StaticResourceReference) ref;
		IStaticResource resource = resRef.getResourceModel().getObject();
		IEntitiesContainer applicationContainer =
				WicketDOAApplication.get().getApplicationContainer();
		IEntitiesContainer innerContainer =
				(IEntitiesContainer) applicationContainer
						.lookupEntityByLocation(containerLocation);
		if (innerContainer == null || resource == null
				|| !resource.isInside(innerContainer)) {
			return null;
		}
		Url url = new Url();
		for (String segment : mountSegments) {
			url.getSegments().add(segment);
		}
		WicketDOAApplication.get().getApplicationContainerLocation();
		PageParameters parameters = new PageParameters();
		parameters.add("location", resRef.getResourceModel().getObject()
				.getLocation());
		return encodePageParameters(url, parameters, new EntityLocationEncoder(
				containerLocation));
	}
}

/**
 * 
 */
package pl.doa.wicket.ui.resource;

import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.resource.IStaticResource;
import pl.doa.wicket.model.EntityModel;

/**
 * @author activey
 * 
 */
public class StaticResourceContainer extends DynamicImageResource {

	private final static Logger log = LoggerFactory
			.getLogger(StaticResourceContainer.class);

	private IModel<IEntitiesContainer> containerModel;

	public StaticResourceContainer(IEntitiesContainer resourceContainer) {
		this.containerModel =
				new EntityModel<IEntitiesContainer>(resourceContainer);
	}

	public StaticResourceContainer(String resourceContainerLocation) {
		this.containerModel =
				new EntityModel<IEntitiesContainer>(resourceContainerLocation);
	}

	public StaticResourceContainer(IModel<IEntitiesContainer> containerModel) {
		this.containerModel = containerModel;
	}

	@Override
	protected byte[] getImageData(Attributes attributes) {
		IStaticResource res = null;

		PageParameters parameters = attributes.getParameters();
		if (parameters != null) {
			StringValue resourceName = parameters.get("name");

			if (resourceName != null && !resourceName.isEmpty()) {
				IEntitiesContainer resourceContainer =
						containerModel.getObject();
				if (resourceContainer != null) {
					res =
							(IStaticResource) resourceContainer
									.getEntityByName(resourceName.toString());
				}
			}
		}
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
}

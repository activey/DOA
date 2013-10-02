/**
 * 
 */
package pl.doa.wicket.mount.container;

import org.apache.wicket.core.request.mapper.MountedMapper;

import pl.doa.agent.IAgent;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.wicket.WicketDOAApplication;
import pl.doa.wicket.mount.EntityLocationEncoder;
import pl.doa.wicket.ui.page.EntityPage;

/**
 * @author activey
 * 
 */
public class AgentEntitiesContainerMapper extends MountedMapper {

	public AgentEntitiesContainerMapper(String mountPath,
			Class<? extends EntityPage<? extends IEntity>> pageClass,
			String containerLocation) {
		super(mountPath, pageClass,
				new EntityLocationEncoder(containerLocation) {
					@Override
					protected IEntitiesContainer getBaseContainer() {
						IAgent agent = WicketDOAApplication.getAgent();
						if (agent == null) {
							return WicketDOAApplication.get()
									.getApplicationContainer();
						}
						return agent.getContainer();
					}
				});
	}
}

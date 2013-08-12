/**
 * 
 */
package pl.doa.wicket.mount.container;

import org.apache.wicket.core.request.mapper.MountedMapper;

import pl.doa.entity.IEntity;
import pl.doa.wicket.mount.EntityLocationEncoder;
import pl.doa.wicket.ui.page.EntityPage;

/**
 * @author activey
 * 
 */
public class EntitiesContainerMapper extends MountedMapper {

	public EntitiesContainerMapper(String mountPath,
			Class<? extends EntityPage<? extends IEntity>> pageClass,
			String containerLocation) {
		super(mountPath, pageClass,
				new EntityLocationEncoder(containerLocation));
	}
}

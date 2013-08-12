/**
 * 
 */
package pl.doa.wicket.ui.repeater;

import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;

import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityAttribute;
import pl.doa.wicket.model.EntityAttributesModel;

/**
 * @author activey
 * 
 */
public abstract class EntityAttributesRepeater extends
		ListView<IEntityAttribute> {

	public EntityAttributesRepeater(String id, IModel<IEntity> entityModel) {
		super(id, new EntityAttributesModel(entityModel));
	}
}

/**
 *
 */
package pl.doa.wicket.decorator.impl;

import org.apache.wicket.model.IModel;

import pl.doa.entity.IEntity;
import pl.doa.wicket.ui.panel.EntityPanel;

/**
 * @author activey
 */
public class EmptyDecoratorPanel extends EntityPanel<IEntity> {

    public EmptyDecoratorPanel(String id, IEntity entity) {
        super(id, entity);
    }

    public EmptyDecoratorPanel(String id, IModel<IEntity> entityModel) {
        super(id, entityModel);
    }

    public EmptyDecoratorPanel(String id, String entityLocation) {
        super(id, entityLocation);
    }


}

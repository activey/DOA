/**
 *
 */
package pl.doa.wicket.model;

import java.io.Serializable;

import org.apache.wicket.model.IModel;

import pl.doa.entity.IEntity;

/**
 * @author activey
 */
public abstract class ReadonlyEntityPropertyModel<T extends Serializable>
        extends EntityPropertyModel<T> {

    public ReadonlyEntityPropertyModel(IModel<? extends IEntity> entityModel) {
        super(entityModel);
    }

    protected final void setPropertyValue(
            org.apache.wicket.model.IModel<? extends pl.doa.entity.IEntity> entityModel,
            T propertyValue) {
        // do nothing
    }

    ;
}

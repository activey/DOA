package pl.doa.wicket.decorator;

import org.apache.wicket.model.IModel;

import pl.doa.entity.IEntity;

public interface IEntityTabLabel<T extends IEntity> {

    public String getTabLabel(IModel<T> entity);
}

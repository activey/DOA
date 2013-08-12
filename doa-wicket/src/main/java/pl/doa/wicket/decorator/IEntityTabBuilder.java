package pl.doa.wicket.decorator;

import org.apache.wicket.model.IModel;

import pl.doa.entity.IEntity;
import pl.doa.wicket.ui.panel.EntityPanel;

public interface IEntityTabBuilder<T extends IEntity> extends
        IEntityTabLabel<T> {

    public EntityPanel<T> buildTabPanel(IModel<T> entity, String panelId);

}

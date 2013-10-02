package pl.doa.wicket.ui.link;

import pl.doa.entity.IEntity;

public interface IEntityLinkLabel<T extends IEntity> {

	public String generateLinkLabel(T entity);
}
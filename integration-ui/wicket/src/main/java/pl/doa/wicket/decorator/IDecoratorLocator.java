package pl.doa.wicket.decorator;

import pl.doa.entity.IEntity;

public interface IDecoratorLocator<T extends IEntity> {

    public IEntityDecorator<T> locateDecorator(T entity);

    public void registerDecorator(long entityId,
                                  Class<IEntityDecorator<T>> decoratorClass);
}

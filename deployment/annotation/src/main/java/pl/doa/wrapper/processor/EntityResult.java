package pl.doa.wrapper.processor;

import pl.doa.entity.IEntity;

/**
 * User: activey Date: 07.08.13 Time: 15:04
 */
public class EntityResult<T extends IEntity> implements IIteratorResult<T> {

    private final T entity;

    public EntityResult(T entity) {
        this.entity = entity;
    }

    @Override
    public T getResult() {
        return this.entity;
    }
}

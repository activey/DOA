package pl.doa.wrapper.processor;

import pl.doa.entity.IEntity;

/**
 * User: activey
 * Date: 07.08.13
 * Time: 14:59
 */
public interface IIteratorResult<T extends IEntity> {

    public T getResult();
}

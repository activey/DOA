package pl.doa.wrapper.processor;

import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.entity.IEntity;

/**
 * User: activey Date: 07.08.13 Time: 15:01
 */
public class WaitingIteratorResult<T extends IEntity> implements IIteratorResult<T> {

    private final Class<? extends IEntity> awaitingWrapping;
    private final IEntitiesContainer container;

    public WaitingIteratorResult(Class<? extends IEntity> awaitingWrapping, IEntitiesContainer container) {
        this.awaitingWrapping = awaitingWrapping;
        this.container = container;
    }

    @Override
    public T getResult() {
        throw new WaitingForDependencyException(awaitingWrapping, container);
    }

    public Class<? extends IEntity> getAwaitingWrapping() {
        return awaitingWrapping;
    }
}

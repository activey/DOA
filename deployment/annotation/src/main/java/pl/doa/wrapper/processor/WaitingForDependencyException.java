package pl.doa.wrapper.processor;

import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.entity.IEntity;

/**
 * User: activey Date: 07.08.13 Time: 15:02
 */
public class WaitingForDependencyException extends RuntimeException {

    private final Class<? extends IEntity> awaitingWrapping;
    private final IEntitiesContainer container;

    public WaitingForDependencyException(Class<? extends IEntity> awaitingWrapping, IEntitiesContainer container) {
        this.awaitingWrapping = awaitingWrapping;
        this.container = container;
    }

    public Class<? extends IEntity> getAwaitingWrapping() {
        return awaitingWrapping;
    }

    public IEntitiesContainer getContainer() {
        return this.container;
    }
}

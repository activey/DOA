package pl.doa.wicket.decorator.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.document.IDocument;
import pl.doa.entity.IEntity;
import pl.doa.wicket.decorator.IDecoratorLocator;
import pl.doa.wicket.decorator.IEntityDecorator;

public class DefaultDecoratorLocator implements IDecoratorLocator<IEntity> {

    private final static Logger log = LoggerFactory
            .getLogger(DefaultDecoratorLocator.class);

    private Map<Long, Class<IEntityDecorator<IEntity>>> decorators =
            new HashMap<Long, Class<IEntityDecorator<IEntity>>>();

    @Override
    public IEntityDecorator<IEntity> locateDecorator(IEntity entity) {
        if (entity instanceof IDocument) {
            IDocument doc = (IDocument) entity;
            entity = doc.getDefinition();
        }

        Class<IEntityDecorator<IEntity>> decoratorClass =
                decorators.get(entity.getId());
        if (decoratorClass == null) {
            return new EmptyDecorator();
        }
        try {
            IEntityDecorator<IEntity> decorator = decoratorClass.newInstance();
            return decorator;
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    @Override
    public void registerDecorator(long entityId,
                                  Class<IEntityDecorator<IEntity>> decoratorClass) {
        decorators.put(entityId, decoratorClass);
    }

}

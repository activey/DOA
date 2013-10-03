package pl.doa.wrapper.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.artifact.deploy.IDeploymentProcessor;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.impl.AbstractPathIterator;
import pl.doa.utils.PathIterator;

import java.lang.annotation.Annotation;

/**
 * User: activey Date: 29.07.13 Time: 15:11
 */
public abstract class AbstractAnnotatedIterator<T extends Annotation, S extends IEntity> {

    private final static Logger log = LoggerFactory.getLogger(AbstractAnnotatedIterator.class);

    public IIteratorResult<S> iterate(String className, Class<T> annotationClass) {
        try {
            Class<?> clazz = Class.forName(className);
            T annotation = clazz.getAnnotation(annotationClass);
            try {
                IIteratorResult<S> iteratorResult = iterateType(clazz, annotation);
                return iteratorResult;
            } catch (GeneralDOAException e) {
                log.error("", e);
            }
        } catch (ClassNotFoundException e) {
            log.error("", e);
        }
        return null;
    }

    public abstract IIteratorResult<S> iterateType(Class<?> annotatedType, T annotation) throws GeneralDOAException;

    protected final IEntitiesContainer createClassContainer(IDeploymentProcessor processor, IEntitiesContainer baseContainer, Class<?> clazz) throws GeneralDOAException {
        String packageName = clazz.getPackage().getName();
        PathIterator<String> iterator = new AbstractPathIterator(packageName) {
            @Override
            protected String getPathSeparator() {
                return ".";
            }
        };
        return createClassContainer(processor, baseContainer, iterator);
    }

    private final IEntitiesContainer createClassContainer(IDeploymentProcessor processor, IEntitiesContainer baseContainer, PathIterator<String> packageNameIterator) throws GeneralDOAException {
        if (packageNameIterator.hasNext()) {
            String packagePart = packageNameIterator.next();
            IEntitiesContainer packageContainer = baseContainer.getEntityByName(packagePart, IEntitiesContainer.class);
            if (packageContainer == null) {
                packageContainer = processor.createEntitiesContainer(packagePart, baseContainer);
            }
            return createClassContainer(processor, packageContainer, packageNameIterator);

        }
        return baseContainer;
    }
}

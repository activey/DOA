package pl.doa.wrapper.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.artifact.deploy.IDeploymentProcessor;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.service.IServiceDefinition;
import pl.doa.wrapper.annotation.OutputDefinition;
import pl.doa.wrapper.annotation.ServiceDefinition;
import pl.doa.wrapper.type.TypeWrapper;
import pl.doa.wrapper.utils.ReflectionUtils;

import java.lang.annotation.Annotation;

public class ServiceDefinitionIterator extends AbstractAnnotatedIterator<ServiceDefinition, IServiceDefinition> {

    private final static Logger log = LoggerFactory.getLogger(ServiceDefinitionIterator.class);
    private final IEntitiesContainer container;
    private final IDeploymentProcessor processor;

    public ServiceDefinitionIterator(IDeploymentProcessor processor, IEntitiesContainer container) {
        this.processor = processor;
        this.container = container;
    }

    @Override
    public IIteratorResult<IServiceDefinition> iterateType(Class<?> wrapperType, ServiceDefinition annotation) throws GeneralDOAException {
        String name = annotation.name();
        if (name == null || name.trim().length() == 0) {
            name = wrapperType.getSimpleName();
        }
        IServiceDefinition definition = processor.createServiceDefinition(name, wrapperType
                .getName(), createClassContainer(processor, this.container, wrapperType));
        definition.setAttribute("_wraps_to", wrapperType.getName());

        // setting service definition parameters
        Class<? extends IDocument> serviceInputDefinition = ReflectionUtils
                .getClassType(wrapperType, IDocument.class, 0);
        IDocumentDefinition wrappedDefinition = TypeWrapper
                .unwrapDocumentDefinition(serviceInputDefinition, this.container);
        if (wrappedDefinition != null) {
            definition.setInputDefinition(wrappedDefinition);
        } else {
            log.debug(String.format("Unable to set input definition for service [%s]", name));
            return new WaitingIteratorResult<IServiceDefinition>(serviceInputDefinition, container);
        }

        Annotation[] annotations = wrapperType.getDeclaredAnnotations();
        for (Annotation serviceAnnotation : annotations) {
            if (serviceAnnotation instanceof OutputDefinition) {
                OutputDefinition output = (OutputDefinition) serviceAnnotation;
                Class<? extends IDocument> outputClass = output.definition();
                wrappedDefinition = TypeWrapper.unwrapDocumentDefinition(outputClass, this.container);
                if (wrappedDefinition != null) {
                    definition.addPossibleOutputDefinition(wrappedDefinition);
                } else {
                    log.debug(String
                            .format("Unable to set output definition for service [%s] using output class [%s]", name, outputClass
                                    .getName()));
                }
            }
        }
        return new EntityResult<IServiceDefinition>(definition);
    }
}

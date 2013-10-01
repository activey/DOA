package pl.doa.wrapper.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.artifact.deploy.DeploymentContext;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.service.IServiceDefinition;
import pl.doa.wrapper.annotation.OutputDefinition;
import pl.doa.wrapper.annotation.ServiceDefinition;
import pl.doa.wrapper.type.TypeWrapper;
import pl.doa.wrapper.utils.ReflectionUtils;

import java.lang.annotation.Annotation;

/**
 * Created with IntelliJ IDEA. User: activey Date: 29.07.13 Time: 15:29 To change this template use File | Settings |
 * File Templates.
 */
public class ServiceDefinitionIterator extends AbstractAnnotatedIterator<ServiceDefinition, IServiceDefinition> {

    private final static Logger log = LoggerFactory.getLogger(ServiceDefinitionIterator.class);
    private final DeploymentContext deploymentContext;
    private final IEntitiesContainer container;

    public ServiceDefinitionIterator(DeploymentContext deploymentContext, IEntitiesContainer container) {
        this.deploymentContext = deploymentContext;
        this.container = container;
    }

    @Override
    public IIteratorResult<IServiceDefinition> iterateType(Class<?> wrapperType, ServiceDefinition annotation) throws GeneralDOAException {
        IDOA doa = deploymentContext.getDoa();
        String name = annotation.name();
        if (name == null || name.trim().length() == 0) {
            name = wrapperType.getSimpleName();
        }
        IServiceDefinition definition = doa.createServiceDefinition(name, wrapperType
                .getName(), createClassContainer(doa, this.container, wrapperType));
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

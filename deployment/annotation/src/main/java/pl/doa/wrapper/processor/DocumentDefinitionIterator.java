package pl.doa.wrapper.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.artifact.deploy.DeploymentContext;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.entity.IEntity;
import pl.doa.wrapper.annotation.DocumentDefinition;
import pl.doa.wrapper.annotation.Field;
import pl.doa.wrapper.type.TypeWrapper;
import pl.doa.wrapper.utils.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA. User: activey Date: 29.07.13 Time: 15:29 To change this template use File | Settings |
 * File Templates.
 */
public class DocumentDefinitionIterator extends AbstractAnnotatedIterator<DocumentDefinition, IDocumentDefinition> {

    private final static Logger log = LoggerFactory.getLogger(DocumentDefinitionIterator.class);
    private final DeploymentContext deploymentContext;
    private final IEntitiesContainer container;

    public DocumentDefinitionIterator(DeploymentContext deploymentContext, IEntitiesContainer container) {
        this.deploymentContext = deploymentContext;
        this.container = container;
    }

    @Override
    public IIteratorResult<IDocumentDefinition> iterateType(Class<?> wrapperType, DocumentDefinition annotation) throws GeneralDOAException {
        IDOA doa = deploymentContext.getDoa();
        String name = annotation.name();
        if (name == null || name.trim().length() == 0) {
            name = wrapperType.getSimpleName();
        }

        // checking super class for ancestor
        IDocumentDefinition ancestor = null;
        Class<?>[] interfaces = wrapperType.getInterfaces();
        for (Class<?> superInterface : interfaces) {
            try {
                if (superInterface.equals(IDocument.class)) {
                    continue;
                }
                Class<? extends IDocument> documentType = (Class<? extends IDocument>) superInterface;
                ancestor = TypeWrapper.unwrapDocumentDefinition(documentType, this.container);
                if (ancestor != null) {
                    break;
                } else {
                    return new WaitingIteratorResult<IDocumentDefinition>(documentType, this.container);
                }
            } catch (ClassCastException e) {
                // do nothing, just checking
            }
        }

        // creating entity
        IEntitiesContainer container = createClassContainer(doa, this.container, wrapperType);
        IDocumentDefinition definition = (ancestor != null) ? doa
                .createDocumentDefinition(name, container, ancestor) : doa
                .createDocumentDefinition(name, container);
        definition.setAttribute("_wraps_to", wrapperType.getName());

        // creating document fields upon declared getters and setters
        Method[] methods = wrapperType.getDeclaredMethods();
        for (Method method : methods) {
            Field fieldAnnotation = method.getAnnotation(Field.class);
            if (fieldAnnotation == null) {
                continue;
            }
            String fieldName = fieldAnnotation.name();
            if (fieldName == null || fieldName.trim().length() == 0) {
                fieldName = ReflectionUtils.getPropertyName(method);
            }

            // getting data type for field from interface definition
            Class<?> dataType = null;
            if (ReflectionUtils.isGetter(method)) {
                dataType = method.getReturnType();
            } else if (ReflectionUtils.isSetter(method)) {
                Class<?>[] methodParams = method.getParameterTypes();
                if (methodParams == null || methodParams.length == 0) {
                    log.warn("Setter method [" + method.getName() + "] has no params!");
                } else {
                    dataType = methodParams[0];
                }
            }
            if (dataType == null) {
                log.warn("Unable to recognize data type conversion method for field [" + fieldName + "]");
            }

            // checking if field already exists
            IDocumentFieldType existingField = definition.getFieldType(fieldName);
            if (existingField != null) {
                // TODO checking existing field data type if matches interface definition data type
                DocumentFieldDataType fieldType = existingField.getFieldDataType();

                log.warn(String.format("Field [%s] is already present!", fieldName));
                continue;
            }

            // TODO implement rest of the fields
            if (String.class.isAssignableFrom(dataType)) {
                definition.addField(fieldName, DocumentFieldDataType.string);
            } else if (boolean.class.isAssignableFrom(dataType) || Boolean.class.isAssignableFrom(dataType)) {
                definition.addField(fieldName, DocumentFieldDataType.bool);
            } else if (IEntity.class.isAssignableFrom(dataType)) {
                definition.addField(fieldName, DocumentFieldDataType.reference);
            }
        }
        return new EntityResult<IDocumentDefinition>(definition);
    }
}

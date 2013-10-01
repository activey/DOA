package pl.doa.wrapper.type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.entity.IEntity;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.service.IServiceDefinitionLogic;
import pl.doa.wrapper.annotation.DocumentDefinition;
import pl.doa.wrapper.annotation.ServiceDefinition;
import pl.doa.wrapper.service.AbstractWrappedServiceDefinitionLogic;
import pl.doa.wrapper.utils.ReflectionUtils;

import java.lang.reflect.Proxy;

public class TypeWrapper {

    private final static Logger log = LoggerFactory.getLogger(TypeWrapper.class);

    public static IDocumentDefinition unwrapDocumentDefinition(Class<? extends IDocument> wrappedType, IEntitiesContainer lookupContainer) {
        String className = wrappedType.getName().replaceAll("\\.", "/");
        IEntity def = lookupContainer.lookupEntityByLocation("/" + className);
        if (def == null || !(def instanceof IDocumentDefinition)) {
            log.warn(String.format("Unable to unwrap class: [%s]", wrappedType.getName()));
            return null;
        }
        if (def.getAttribute("_wraps_to", "").equals(wrappedType.getName())) {
            return (IDocumentDefinition) def;
        }
        return null;
    }

    public static IServiceDefinition unwrapServiceDefinition(Class<? extends AbstractWrappedServiceDefinitionLogic<? extends IDocument>> wrappedType, IEntitiesContainer lookupContainer) {
        String className = wrappedType.getName().replaceAll("\\.", "/");
        IEntity def = lookupContainer.lookupEntityByLocation("/" + className);
        if (def == null || !(def instanceof IServiceDefinition)) {
            log.warn(String.format("Unable to unwrap class: [%s]", wrappedType.getName()));
            return null;
        }
        if (def.getAttribute("_wraps_to", "").equals(wrappedType.getName())) {
            return (IServiceDefinition) def;
        }
        return null;
    }

    public static <T extends IDocument> T wrap(IDocument document) throws GeneralDOAException {
        IDocumentDefinition definition = document.getDefinition();
        String wrappedType = definition.getAttribute("_wraps_to");
        if (wrappedType == null) {
            throw new GeneralDOAException("Document definition is not a wrapped type!");
        }
        Class<T> documentClass = null;
        try {
            documentClass = (Class<T>) Class.forName(wrappedType);
        } catch (ClassNotFoundException e) {
            throw new GeneralDOAException(e);
        } catch (ClassCastException e1) {
            log.warn(String.format("Unable to wrap IDocument using wrapper type [%s]", wrappedType));
            return null;
        }
        if (!documentClass.isAnnotationPresent(DocumentDefinition.class)) {
            throw new GeneralDOAException("Document definition annotation not present!");
        }
        return (T) Proxy.newProxyInstance(TypeWrapper.class.getClassLoader(), new Class[]{documentClass},
                new DocumentInvocationHandler(document));
    }

    public static <T extends IDocument> T wrap(IDocument document, Class<T> wrapType) throws GeneralDOAException {
        IDocumentDefinition definition = document.getDefinition();
        String wrappedType = definition.getAttribute("_wraps_to");
        if (wrappedType == null) {
            throw new GeneralDOAException("Document definition is not a wrapped type!");
        }
        Class<T> documentClass = null;
        try {
            documentClass = (Class<T>) Class.forName(wrappedType);
        } catch (ClassNotFoundException e) {
            throw new GeneralDOAException(e);
        }
        if (!documentClass.isAnnotationPresent(DocumentDefinition.class)) {
            throw new GeneralDOAException("Document definition annotation not present!");
        }
        return (T) Proxy.newProxyInstance(TypeWrapper.class.getClassLoader(), new Class[]{documentClass},
                new DocumentInvocationHandler(document));
    }

    public static <T extends IDocument> T wrap(Class<T> definitionClass, IEntitiesContainer container) {
        IDocumentDefinition definition = TypeWrapper.unwrapDocumentDefinition(definitionClass, container);
        if (definition == null) {
            return null;
        }
        IDocument instance = null;
        try {
            instance = definition.createDocumentInstance();
            return TypeWrapper.wrap(instance, definitionClass);
        } catch (GeneralDOAException e) {
            log.error("", e);
            return null;
        }

    }
}

package pl.doa.wrapper.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.artifact.deploy.IDeploymentProcessor;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.alignment.IDocumentAligner;
import pl.doa.wrapper.annotation.Aligner;
import pl.doa.wrapper.type.TypeWrapper;
import pl.doa.wrapper.utils.ReflectionUtils;

/**
 * User: activey Date: 29.07.13 Time: 15:29
 */
public class AlignerIterator extends AbstractAnnotatedIterator<Aligner, IDocumentAligner> {

    private final static Logger log = LoggerFactory.getLogger(AlignerIterator.class);
    private final IDeploymentProcessor processor;
    private final IEntitiesContainer container;

    public AlignerIterator(IDeploymentProcessor processor, IEntitiesContainer container) {
        this.processor = processor;
        this.container = container;
    }

    @Override
    public IIteratorResult<IDocumentAligner> iterateType(Class<?> wrapperType, Aligner annotation) throws GeneralDOAException {
        String name = annotation.name();
        if (name == null || name.trim().length() == 0) {
            name = wrapperType.getSimpleName();
        }

        Class<? extends IDocument> fromDefinition = ReflectionUtils.getClassType(wrapperType, IDocument.class, 0);
        Class<? extends IDocument> toDefinition = ReflectionUtils.getClassType(wrapperType, IDocument.class, 1);

        IDocumentDefinition from = TypeWrapper.unwrapDocumentDefinition(fromDefinition, this.container);
        if (from == null) {
            log.debug(String.format("Unable to set FROM for aligner [%s]", name));
            return new WaitingIteratorResult<IDocumentAligner>(fromDefinition, container);
        }

        IDocumentDefinition to = TypeWrapper.unwrapDocumentDefinition(toDefinition, this.container);
        if (to == null) {
            log.debug(String.format("Unable to set TO for aligner [%s]", name));
            return new WaitingIteratorResult<IDocumentAligner>(toDefinition, container);
        }

        IDocumentAligner aligner = processor.createDocumentAligner(name, from, to, container);
        aligner.setLogicClass(wrapperType.getName());
        aligner.setAttribute("_wraps_to", wrapperType.getName());

        return new EntityResult<IDocumentAligner>(aligner);
    }
}

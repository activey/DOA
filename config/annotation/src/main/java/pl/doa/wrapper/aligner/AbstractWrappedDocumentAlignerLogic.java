package pl.doa.wrapper.aligner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.alignment.impl.AbstractDocumentAlignerLogic;
import pl.doa.wrapper.type.TypeWrapper;

/**
 * User: activey
 * Date: 08.08.13
 * Time: 20:57
 */
public abstract class AbstractWrappedDocumentAlignerLogic<T extends IDocument, S extends IDocument> extends AbstractDocumentAlignerLogic {

    private final static Logger log = LoggerFactory.getLogger(AbstractWrappedDocumentAlignerLogic.class);

    @Override
    public IDocument align(IDocument input, IDocumentDefinition toDefinition) throws GeneralDOAException {
        IDocument to = toDefinition.createDocumentInstance();

        T fromWrapped = TypeWrapper.wrap(input);
        S toWrapped = TypeWrapper.wrap(to);

        if (fromWrapped == null || toWrapped == null) {
            log.warn("From or To is null!");
            return null;
        }

        align(fromWrapped, toWrapped);

        return to;
    }

    private T wrapFrom(IDocument from, Class<T> fromType) throws GeneralDOAException {

        return TypeWrapper.wrap(from, fromType);
    }

    protected abstract void align(T from, S to) throws GeneralDOAException;
}

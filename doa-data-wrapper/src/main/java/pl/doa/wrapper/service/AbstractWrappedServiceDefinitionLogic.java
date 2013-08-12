package pl.doa.wrapper.service;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.service.AbstractServiceDefinitionLogic;
import pl.doa.wrapper.type.TypeWrapper;

/**
 * Created with IntelliJ IDEA.
 * User: activey
 * Date: 30.07.13
 * Time: 15:04
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractWrappedServiceDefinitionLogic<T extends IDocument> extends AbstractServiceDefinitionLogic {

    public final T getInputWrapped(Class<T> type) throws GeneralDOAException {
        IDocument input = getRunningService().getInput();
        return TypeWrapper.wrap(input, type);
    }

    protected final <S extends IDocument> S createOutputDocument(Class<S> outputType)
            throws GeneralDOAException {
        // TODO do it better ...
        IDocument output = createOutputDocument(outputType.getSimpleName());
        return TypeWrapper.wrap(output, outputType);
    }

}

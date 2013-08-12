package pl.doa.wrapper.wicket.ui.link;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.wrapper.type.TypeWrapper;

/**
 * User: activey
 * Date: 08.08.13
 * Time: 20:25
 */
public class ServiceOutput<T extends IDocument> implements IServiceOutput<T> {

    private final static Logger log = LoggerFactory.getLogger(ServiceOutput.class);

    private final IDocument output;
    private final Class<T> outputType;

    public ServiceOutput(IDocument output, Class<T> outputType) {
        this.output = output;
        this.outputType = outputType;
    }

    @Override
    public void get(IOutputOperator<T> operator) {
        operator.setOutput(output);

        try {
            T type = TypeWrapper.wrap(output, outputType);

            operator.doGet(type);
        } catch (GeneralDOAException e) {
            log.error("", e);
            return;
        }

    }
}

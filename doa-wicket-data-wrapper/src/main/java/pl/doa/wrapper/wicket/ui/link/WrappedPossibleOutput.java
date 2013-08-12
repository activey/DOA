package pl.doa.wrapper.wicket.ui.link;

import pl.doa.document.IDocument;

/**
 * User: activey
 * Date: 08.08.13
 * Time: 20:23
 */
public class WrappedPossibleOutput {

    private final IDocument output;

    public WrappedPossibleOutput(IDocument output) {
        this.output = output;
    }

    public <T extends IDocument> IServiceOutput<T> when(Class<T> outputType) {
        return new ServiceOutput<T>(output, outputType);
    }
}

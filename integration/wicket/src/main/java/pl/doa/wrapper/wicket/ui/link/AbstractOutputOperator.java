package pl.doa.wrapper.wicket.ui.link;

import pl.doa.document.IDocument;

/**
 * User: activey
 * Date: 08.08.13
 * Time: 20:38
 */
public abstract class AbstractOutputOperator<T extends IDocument> implements IOutputOperator<T> {

    private IDocument output;

    @Override
    public void setOutput(IDocument output) {
        this.output = output;
    }

    public IDocument getOutput() {
        return output;
    }
}

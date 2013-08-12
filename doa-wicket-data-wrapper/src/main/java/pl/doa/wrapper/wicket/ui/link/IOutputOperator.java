package pl.doa.wrapper.wicket.ui.link;

import pl.doa.document.IDocument;

/**
 * User: activey
 * Date: 08.08.13
 * Time: 20:30
 */
public interface IOutputOperator<T extends IDocument> {

    public void doGet(T type);

    void setOutput(IDocument output);
}

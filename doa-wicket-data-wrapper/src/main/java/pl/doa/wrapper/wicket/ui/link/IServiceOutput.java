package pl.doa.wrapper.wicket.ui.link;

import pl.doa.document.IDocument;

/**
 * User: activey
 * Date: 08.08.13
 * Time: 20:25
 */
public interface IServiceOutput<T extends IDocument> {

    public void get(IOutputOperator<T> operator);
}

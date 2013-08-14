package pl.doa.servlet.filter.processor;

import pl.doa.IDOA;
import pl.doa.document.IDocument;

public interface IRequestProcessor {

    public void processRequest(IDOA doa, IDocument requestDocument,
                               IDocument responseDocument) throws Exception;

    boolean matches(String uri);
}

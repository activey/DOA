package pl.doa.servlet.filter.processor.rest;

import pl.doa.IDOA;
import pl.doa.document.IDocument;

public interface RestCallResponse {

    public void commitResponse(IDOA doa, IDocument responseDocument) throws Exception;
}

package pl.doa.http.ext.webdav.aligner;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.alignment.impl.AbstractDocumentAlignerLogic;
import pl.doa.http.ext.webdav.WebdavFilter;

public class WebdavFilterAligner extends AbstractDocumentAlignerLogic {

    @Override
    public IDocument align(IDocument input, IDocumentDefinition toDefinition)
            throws GeneralDOAException {
        IDocument toDocument = toDefinition.createDocumentInstance(input
                .getName());
        toDocument.setFieldValue("filterClass", WebdavFilter.class.getName());
        toDocument.copyFieldFrom(input, "filterMapping");
        return toDocument;
    }

}

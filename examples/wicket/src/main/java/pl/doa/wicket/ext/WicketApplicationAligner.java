package pl.doa.wicket.ext;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.alignment.impl.AbstractDocumentAlignerLogic;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.IListDocumentFieldValue;
import pl.doa.wicket.filter.WicketFilter;

public class WicketApplicationAligner extends AbstractDocumentAlignerLogic {

	@Override
	public IDocument align(IDocument input, IDocumentDefinition toDefinition)
			throws GeneralDOAException {
        IDocument toDocument = toDefinition.createDocumentInstance(input
                .getName());
        toDocument.setFieldValue("filterClass", WicketFilter.class.getName());
        toDocument.setFieldValue("filterMapping", "/*");
        IListDocumentFieldValue listField = (IListDocumentFieldValue) toDocument.getField("initParams", true);
        IDocumentFieldValue appClass = listField.addField("applicationClassName", DocumentFieldDataType.string);
        appClass.setFieldValue(input.getFieldValueAsString("applicationClassName"));
        return toDocument;
	}

}

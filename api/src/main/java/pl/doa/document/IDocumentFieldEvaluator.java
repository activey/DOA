package pl.doa.document;

import pl.doa.document.field.IDocumentFieldValue;

public interface IDocumentFieldEvaluator {

	public boolean evaluate(IDocumentFieldValue fieldValue);
}

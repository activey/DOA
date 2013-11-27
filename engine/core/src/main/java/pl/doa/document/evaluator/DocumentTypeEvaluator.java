package pl.doa.document.evaluator;

import pl.doa.document.IDocument;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;

public class DocumentTypeEvaluator implements IEntityEvaluator {

    private final String documentDefinitionLocation;

    private DocumentTypeEvaluator(String documentDefinitionLocation) {
        this.documentDefinitionLocation = documentDefinitionLocation;
    }

    @Override
    public boolean isReturnableEntity(IEntity currentEntity) {
        if (!(currentEntity instanceof IDocument)) {
            return false;
        }
        IDocument doc = (IDocument) currentEntity;
        return (doc.isDefinedBy(this.documentDefinitionLocation));
    }

    public static DocumentTypeEvaluator documentDefinedBy(String documentDefinitionLocation) {
        return new DocumentTypeEvaluator(documentDefinitionLocation);
    }
}

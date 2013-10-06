/**
 * 
 */
package com.olender.backend.services.sections;

import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.IListDocumentFieldValue;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.entity.ITransactionCallback;
import pl.doa.service.annotation.EntityRef;

import com.olender.backend.services.BaseServiceDefinitionLogic;

/**
 * @author activey
 * 
 */
public class RemoveSectionService extends BaseServiceDefinitionLogic {

	@EntityRef(location = "/applications/olender-frontpage/documents/application/links")
	private IEntitiesContainer linksContainer;

	/* (non-Javadoc)
	 * @see pl.doa.service.AsynchronousServiceDefinitionLogic#alignAsync()
	 */
	@Override
	protected void alignAsync() throws GeneralDOAException {
		final IDocument input = getInput();

		IEntitiesContainer container =
				getApplicationContainer("/documents/application/sections");
		final IDocument existingSection =
				(IDocument) container.lookupForEntity(new IEntityEvaluator() {

					public boolean isReturnableEntity(IEntity currentEntity) {
						if (!(currentEntity instanceof IDocument)) {
							return false;
						}
						IDocument section = (IDocument) currentEntity;
						return section.equals(input);
					}
				}, true);

		// szukanie wszystich linkow, ktore sa skojarzone z usuwana sekcja
		final Iterable<IEntity> relatedLinks =
				linksContainer.lookupForEntities(new IEntityEvaluator() {

					public boolean isReturnableEntity(IEntity currentEntity) {
						if (!(currentEntity instanceof IDocument)) {
							return false;
						}
						IDocument link = (IDocument) currentEntity;

						IEntity fromEntity =
								(IEntity) link.getFieldValue("fromEntity");
						if (fromEntity.equals(existingSection)) {
							return true;
						}
						IListDocumentFieldValue toEntities =
								(IListDocumentFieldValue) link
										.getField("toEntities");
						if (toEntities == null) {
							return false;
						}

						Iterable<IDocumentFieldValue> toEntitiesList =
								toEntities.iterateFields();
						for (IDocumentFieldValue toEntityField : toEntitiesList) {
							IEntity toEntity =
									(IEntity) toEntityField.getFieldValue();
							if (existingSection.equals(toEntity)) {
								return true;
							}
						}

						return false;
					}
				}, true);

		doa.doInTransaction(new ITransactionCallback<Void>() {

			public Void performOperation() throws Exception {
				for (IEntity relatedLink : relatedLinks) {
					relatedLink.remove();
				}
				existingSection.remove();
				return null;
			}
		});

		setOutput(createOutputDocument("void"));
	}

}

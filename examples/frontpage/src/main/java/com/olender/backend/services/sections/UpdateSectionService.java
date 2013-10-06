/**
 * 
 */
package com.olender.backend.services.sections;

import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.entity.ITransactionCallback;

import com.olender.backend.services.BaseServiceDefinitionLogic;

/**
 * @author activey
 * 
 */
public class UpdateSectionService extends BaseServiceDefinitionLogic {

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

		doa.doInTransaction(new ITransactionCallback<IDocument>() {

			public IDocument performOperation() throws Exception {
				existingSection.copyFieldFrom(input, "name");
				existingSection.copyFieldFrom(input, "href");
				existingSection.copyFieldFrom(input, "front");
				existingSection.copyFieldFrom(input, "backgroundImage");
				return existingSection;
			}
		});

		setOutput(existingSection);
	}

}

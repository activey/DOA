/**
 * 
 */
package com.olender.backend.services.sections.customers;

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
public class UpdateLinkLogoService extends BaseServiceDefinitionLogic {

	/* (non-Javadoc)
	 * @see pl.doa.service.AsynchronousServiceDefinitionLogic#alignAsync()
	 */
	@Override
	protected void alignAsync() throws GeneralDOAException {
		final IDocument input = getInput();

		IEntitiesContainer container =
				getApplicationContainer("/documents/application/links/logos");
		final IDocument existingSection =
				(IDocument) container.lookupForEntity(new IEntityEvaluator() {

					public boolean isReturnableEntity(IEntity currentEntity) {
						if (!(currentEntity instanceof IDocument)) {
							return false;
						}
						IDocument section = (IDocument) currentEntity;
						return section.equals(input);
					}
				}, false);

		doa.doInTransaction(new ITransactionCallback<IDocument>() {

			public IDocument performOperation() throws Exception {
				existingSection.copyFieldFrom(input, "customerUrl");
				existingSection.copyFieldFrom(input, "fromEntity");
				existingSection.copyFieldFrom(input, "toEntities");
				return existingSection;
			}
		});

		setOutput(existingSection);
	}

}

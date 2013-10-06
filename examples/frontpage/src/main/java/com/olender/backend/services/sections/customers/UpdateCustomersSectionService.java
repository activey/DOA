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
import pl.doa.service.annotation.EntityRef;

/**
 * @author activey
 * 
 */
public class UpdateCustomersSectionService extends BaseServiceDefinitionLogic {

    @EntityRef(location = "/documents/application/sections/customers")
    private IEntitiesContainer container;

	/* (non-Javadoc)
	 * @see pl.doa.service.AsynchronousServiceDefinitionLogic#alignAsync()
	 */
	@Override
	protected void alignAsync() throws GeneralDOAException {
		final IDocument input = getInput();

		final IDocument existingSection =
				(IDocument) container.lookupForEntity(new IEntityEvaluator() {

					public boolean isReturnableEntity(IEntity currentEntity) {
						if (!(currentEntity instanceof IDocument)) {
							return false;
						}
						String sectionDocName = input.getName();
						IDocument section = (IDocument) currentEntity;
						String sectionName = section.getName();
						return sectionName.equals(sectionDocName);
					}
				}, false);

		doa.doInTransaction(new ITransactionCallback<IDocument>() {

			public IDocument performOperation() throws Exception {
				existingSection.copyFieldFrom(input, "description");
				return existingSection;
			}
		});

		setOutput(existingSection);
	}

}

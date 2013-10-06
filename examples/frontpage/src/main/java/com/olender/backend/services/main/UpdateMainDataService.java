/**
 * 
 */
package com.olender.backend.services.main;

import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.entity.ITransactionCallback;

import com.olender.backend.services.BaseServiceDefinitionLogic;

/**
 * @author activey
 * 
 */
public class UpdateMainDataService extends BaseServiceDefinitionLogic {

	/* (non-Javadoc)
	 * @see pl.doa.service.AsynchronousServiceDefinitionLogic#alignAsync()
	 */
	@Override
	protected void alignAsync() throws GeneralDOAException {
		final IDocument input = getInput();

		IEntitiesContainer container =
				getApplicationContainer("/documents/application");
		final IDocument existingSection =
				(IDocument) container.getEntityByName("main");

		doa.doInTransaction(new ITransactionCallback<IDocument>() {

			public IDocument performOperation() throws Exception {
				existingSection.copyFieldFrom(input, "logo");
				existingSection.copyFieldFrom(input, "twitterUrl");
				existingSection.copyFieldFrom(input, "facebookUrl");
				existingSection.copyFieldFrom(input, "googlePlusUrl");
				return existingSection;
			}
		});

		setOutput(input);
	}

}

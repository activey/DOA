/**
 * 
 */
package com.olender.backend.services.sections.customers;

import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.entity.ITransactionCallback;
import pl.doa.service.annotation.EntityRef;

import com.olender.backend.services.BaseServiceDefinitionLogic;

/**
 * @author activey
 * 
 */
public class LinkLogoService extends BaseServiceDefinitionLogic {

	@EntityRef(location = "/applications/olender-frontpage/documents/application/links/logos")
	private IEntitiesContainer links = null;

	/* (non-Javadoc)
	 * @see pl.doa.service.AsynchronousServiceDefinitionLogic#alignAsync()
	 */
	@Override
	protected void alignAsync() throws GeneralDOAException {
		final IDocument input = getInput();

		IDocument output =
				doa.doInTransaction(new ITransactionCallback<IDocument>() {

					public IDocument performOperation() throws Exception {
						return links.addEntity(input.createCopy());
					}
				});
		if (output == null) {
			throw new GeneralDOAException("Unable to create link!");
		}
		setOutput(output);
	}

}

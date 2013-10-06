/**
 * 
 */
package com.olender.backend.services.sections.text;

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
public class LinkSectionService extends BaseServiceDefinitionLogic {

	@EntityRef(location = "/applications/olender-frontpage/documents/application/links/links")
	private IEntitiesContainer links = null;

	@EntityRef(location = "/applications/olender-frontpage/documents/application/links/subsections")
	private IEntitiesContainer subsections = null;

	/* (non-Javadoc)
	 * @see pl.doa.service.AsynchronousServiceDefinitionLogic#alignAsync()
	 */
	@Override
	protected void alignAsync() throws GeneralDOAException {
		final IDocument input = getInput();

		IDocument output =
				doa.doInTransaction(new ITransactionCallback<IDocument>() {

					public IDocument performOperation() throws Exception {
						String linkType =
								input.getFieldValueAsString("linkType");
						if ("link".equals(linkType)) {
							return links.addEntity(input.createCopy());
						} else if ("subsection".equals(linkType)) {
							return subsections.addEntity(input.createCopy());
						}
						return null;
					}
				});
		if (output == null) {
			throw new GeneralDOAException("Unable to create link!");
		}
		setOutput(output);
	}

}

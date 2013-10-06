/**
 * 
 */
package com.olender.backend.services.images;

import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.resource.IStaticResource;

import com.olender.backend.services.BaseServiceDefinitionLogic;

/**
 * @author activey
 * 
 */
public class MoveResourceService extends BaseServiceDefinitionLogic {

	/* (non-Javadoc)
	 * @see pl.doa.service.AsynchronousServiceDefinitionLogic#alignAsync()
	 */
	@Override
	protected void alignAsync() throws GeneralDOAException {
		IDocument input = getInput();

		IStaticResource res = (IStaticResource) input.getFieldValue("resource");
		IEntitiesContainer cont =
				(IEntitiesContainer) input.getFieldValue("container");
		res.setContainer(cont);

		setOutput(createOutputDocument("void"));
	}
}

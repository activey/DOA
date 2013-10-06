/**
 * 
 */
package com.olender.backend.services.images;

import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;

import com.olender.backend.services.BaseServiceDefinitionLogic;

/**
 * @author activey
 * 
 */
public class CreateFolderService extends BaseServiceDefinitionLogic {

	/* (non-Javadoc)
	 * @see pl.doa.service.AsynchronousServiceDefinitionLogic#alignAsync()
	 */
	@Override
	protected void alignAsync() throws GeneralDOAException {
		IDocument input = getInput();
		String folderName = input.getFieldValueAsString("folder_name");
		IEntitiesContainer parent = (IEntitiesContainer) input.getFieldValue("parent");
		
		doa.createContainer(folderName, parent);
		
		setOutput(createOutputDocument("void"));
	}
}

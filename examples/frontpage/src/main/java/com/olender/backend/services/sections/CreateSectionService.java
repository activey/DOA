/**
 * 
 */
package com.olender.backend.services.sections;

import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.entity.ITransactionCallback;
import pl.doa.service.annotation.EntityRef;

import com.olender.backend.services.BaseServiceDefinitionLogic;

/**
 * @author activey
 * 
 */
public class CreateSectionService extends BaseServiceDefinitionLogic {

	@EntityRef(location = "/applications/olender-frontpage/documents/application/sections")
	private IEntitiesContainer sectionsContainer;

	/* (non-Javadoc)
	 * @see pl.doa.service.AsynchronousServiceDefinitionLogic#alignAsync()
	 */
	@Override
	protected void alignAsync() throws GeneralDOAException {
		IDocument input = getInput();
		String sectionName = input.getFieldValueAsString("section_name");
		IDocumentDefinition sectionDefinition =
				(IDocumentDefinition) input.getFieldValue("section_definition");

		String targetContainer =
				sectionDefinition.getAttribute("target.container", "text");
		final IEntitiesContainer container =
				(IEntitiesContainer) sectionsContainer
						.getEntityByName(targetContainer);

		final IDocument newSection = sectionDefinition.createDocumentInstance();
		newSection.setFieldValue("name", sectionName);
		newSection.setFieldValue("href",
				sectionName.toLowerCase().replaceAll(" ", "_"));
		newSection.setFieldValue("priority", 0L);

		// dodawanie nowej sekcji
		IDocument stored =
				doa.doInTransaction(new ITransactionCallback<IDocument>() {

					public IDocument performOperation() throws Exception {
						return container.addEntity(newSection);
					}
				});

		setOutput(stored);
	}

}

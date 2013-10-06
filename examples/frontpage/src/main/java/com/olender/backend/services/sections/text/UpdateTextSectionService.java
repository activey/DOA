/**
 * 
 */
package com.olender.backend.services.sections.text;

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
public class UpdateTextSectionService extends BaseServiceDefinitionLogic {

	/* (non-Javadoc)
	 * @see pl.doa.service.AsynchronousServiceDefinitionLogic#alignAsync()
	 */
	@Override
	protected void alignAsync() throws GeneralDOAException {
		final IDocument input = getInput();

		IEntitiesContainer container =
				getApplicationContainer("/documents/application/sections/text");
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
				existingSection.copyFieldFrom(input, "header");
				existingSection.copyFieldFrom(input, "leftImage");
				return existingSection;
			}
		});

		setOutput(existingSection);
	}

}

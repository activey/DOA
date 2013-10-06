/**
 * 
 */
package com.olender.backend.services.sections.contact;

import com.olender.backend.services.BaseServiceDefinitionLogic;
import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.entity.ITransactionCallback;
import pl.doa.service.annotation.EntityRef;

/**
 * @author activey
 * 
 */
public class UpdateContactSectionService extends BaseServiceDefinitionLogic {

    @EntityRef(location = "/documents/application/sections/contact")
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
                        IDocument section = (IDocument) currentEntity;
                        return section.equals(input);
                    }
                }, false);

		doa.doInTransaction(new ITransactionCallback<IDocument>() {

			public IDocument performOperation() throws Exception {
				existingSection.copyFieldFrom(input, "addr1");
				existingSection.copyFieldFrom(input, "addr2");
				existingSection.copyFieldFrom(input, "addr3");
				existingSection.copyFieldFrom(input, "phone1");
				existingSection.copyFieldFrom(input, "phone2");
				existingSection.copyFieldFrom(input, "email");

				existingSection.copyFieldFrom(input, "apiKey");
				existingSection.copyFieldFrom(input, "latitude");
				existingSection.copyFieldFrom(input, "longitude");
				existingSection.copyFieldFrom(input, "description");
				return existingSection;
			}
		});

		setOutput(existingSection);
	}

}

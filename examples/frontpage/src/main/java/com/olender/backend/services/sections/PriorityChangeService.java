/**
 * 
 */
package com.olender.backend.services.sections;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.entity.ITransactionCallback;

import com.olender.backend.services.BaseServiceDefinitionLogic;

/**
 * @author activey
 * 
 */
public class PriorityChangeService extends BaseServiceDefinitionLogic {

	/* (non-Javadoc)
	 * @see pl.doa.service.AsynchronousServiceDefinitionLogic#alignAsync()
	 */
	@Override
	protected void alignAsync() throws GeneralDOAException {
		final IDocument input = getInput();
		final long delta = (Long) input.getFieldValue("delta", 0L);
		final IDocument sectionDoc = (IDocument) input.getFieldValue("section");

		doa.doInTransaction(new ITransactionCallback<IDocument>() {

			public IDocument performOperation() throws Exception {
				long currentPriority =
						(Long) sectionDoc.getFieldValue("priority", 0L);
				sectionDoc.setFieldValue("priority", currentPriority + delta);

				return sectionDoc;
			}
		});

		setOutput(sectionDoc);
	}

}

/**
 * 
 */
package com.olender.backend.services.authentication;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;

import com.olender.backend.services.BaseServiceDefinitionLogic;

/**
 * @author activey
 * 
 */
public class LoginService extends BaseServiceDefinitionLogic {

	/* (non-Javadoc)
	 * @see pl.doa.service.AsynchronousServiceDefinitionLogic#alignAsync()
	 */
	@Override
	protected void alignAsync() throws GeneralDOAException {
		IDocument input = getInput();
		setOutput(createOutputDocument("void"));
	}
}

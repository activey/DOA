/*******************************************************************************
 * Copyright 2011 Inhibi Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are
 * permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright 
 * notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright 
 * notice, this list
 *        of conditions and the following disclaimer in the documentation 
 * and/or other materials
 *        provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY INHIBI LTD ``AS IS'' AND ANY 
 * EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * INHIBI LTD OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation 
 * are those of the authors and should not be interpreted as representing 
 * official policies, either expressed or implied, of Inhibi Ltd.
 *
 * Contributors:
 *    Inhibi Ltd - initial API and implementation
 *******************************************************************************/
package pl.doa.document.field;

import java.util.List;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocumentDefinition;

public interface IDocumentFieldType {

	public String getName();

	public void setName(String name);

	/**
	 * Metoda zwraca typ pola.
	 * 
	 * @return
	 */
	public DocumentFieldDataType getFieldDataType();

	public void setFieldDataType(DocumentFieldDataType fieldDataType);

	public boolean isRequired();

	public void setRequired(boolean required);

	public boolean isAuthorizable();

	public void setAuthorizable(boolean authorizable);

	public IDocumentFieldValue createValueInstance(String fieldName)
			throws GeneralDOAException;

	public IDocumentFieldValue createValueInstance() throws GeneralDOAException;

	public Object getAttribute(String attributeName);

	public Object getAttribute(String attributeName, Object defaultValue);

	public List<String> getAttributeNames();
	
	public void setAttribute(String attributeName, Object attributeValue);

	public IDocumentDefinition getDocumentDefinition();
	
	public void remove();
	
	public void remove(boolean forceRemove);

}
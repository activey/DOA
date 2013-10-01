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
package pl.doa.document;

import java.util.Iterator;
import java.util.List;

import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.entity.IEntity;

public interface IDocumentDefinition extends IEntity {

	public abstract IDocumentFieldType addField(String fieldName,
			DocumentFieldDataType dataType) throws GeneralDOAException;

	/**
	 * Metoda dodaje nowe pole do definicji odkumentu.
	 * 
	 * @param fieldName
	 *            Nazwa nowego pola.
	 * @param fieldClass
	 *            Klasa opisujaca typ pola. Musi byc instancja klasy
	 *            pl.doa.core.repository.document.field.DocumentFieldType
	 * @throws GeneralDOAException
	 */
	public abstract IDocumentFieldType addField(String fieldName,
			DocumentFieldDataType dataType, boolean required,
			boolean authorizable) throws GeneralDOAException;

	/**
	 * Metoda modyfikuje pole definicji dokumentu.
	 * 
	 * @param fieldName
	 *            Nazwa pola do zmodyfikowania.
	 * @param newName
	 *            Nowa nazwa pola.
	 * @param newType
	 *            Nowy typ pola.
	 * @throws GeneralDOAException
	 */
	public abstract void modifyField(String fieldName, String newName,
			DocumentFieldDataType newType, boolean required,
			boolean authorizable) throws GeneralDOAException;

	/**
	 * Metoda usuwa pole z definicji dokumentu.
	 * 
	 * @param fieldName
	 *            Nazwa pola do usuniecia.
	 */

	public abstract void removeField(String fieldName);

	public abstract IDocument createDocumentInstance(String name) throws GeneralDOAException;

	public abstract IDocument createDocumentInstance() throws GeneralDOAException;

	public abstract IDocument createDocumentInstance(String name,
			IEntitiesContainer container) throws GeneralDOAException;

	public abstract Iterator<String> getFieldNames();

	public abstract IDocumentFieldType getFieldType(String fieldName);

	public abstract void setDocumentFields(
			List<IDocumentFieldType> documentFields);

	public abstract Iterator<IDocumentFieldType> getDocumentFields();

	public abstract Iterator<IDocumentFieldType> getRequiredFields();

	public abstract Iterator<IDocumentFieldType> getAuthorizableFields();

}
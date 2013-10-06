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

import pl.doa.GeneralDOAException;
import pl.doa.document.alignment.IDocumentAligner;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.entity.IEntity;

import java.util.Iterator;
import java.util.List;

public interface IDocument extends IEntity {

    /**
     * Metoda zwraca referencje do definicji dokumentu.
     *
     * @return
     */
    public IDocumentDefinition getDefinition();

    public void setDefinition(IDocumentDefinition definition);

    /**
     * Metoda sprawdza czy pole o podanej nazwie jest dostepne w danym dokumencie.
     *
     * @param fieldName
     * @return
     */
    public boolean isFieldAvailable(String fieldName);

    /**
     * Metoda ustawia wartosc pola. Wartosc jest sprawdzana z definicja dokumentu.
     *
     * @param fieldName  Nazwa pola.
     * @param fieldValue Wartosc pola.s
     * @throws GeneralDOAException
     * @throws GeneralDOAException
     */

    public void setFieldValue(String fieldName, Object fieldValue)
            throws GeneralDOAException;

    public void setFieldValue(String fieldName, Object fieldValue,
                              DocumentFieldDataType dataType) throws GeneralDOAException;

    public void setFieldValue(String fieldName, IDocumentFieldValue otherField)
            throws GeneralDOAException;

    public IDocumentFieldValue getField(String fieldName, boolean createIfNull)
            throws GeneralDOAException;

    public IDocumentFieldValue getField(String fieldName);

    /**
     * Metoda zwraca wartosc pola o podanej nazwie.
     *
     * @param fieldName
     * @return
     */
    public Object getFieldValue(String fieldName);

    public Object getFieldValue(String fieldName, Object whenNull);

    /**
     * Metoda zwraca liste wszystkich pol dokumentu
     *
     * @return
     */
    public Iterator<String> getFieldsNames();

    public Iterator<IDocumentFieldValue> getFields();

    public void setFields(List<IDocumentFieldValue> list);

    public String getFieldValueAsString(String fieldName);

    public void validateDocument(IDocumentDefinition definition)
            throws DocumentValidationException;

    public void validateDocument() throws DocumentValidationException;

    /**
     * Metoda zwraca flage, ktora informuje, czy dokument jest zbudowany na podstawie definicji dokumentu, ktorej
     * lokalizacja jest podana jako parametr.
     *
     * @param documentDefinitionPath Lokalizacja definicji dokumentu.
     * @return
     */
    public boolean isDefinedBy(IDocumentDefinition documentDefinition);

    public boolean isDefinedBy(String documentDefinitionLocation);

    public IDocument align(IDocumentDefinition toDefinition)
            throws GeneralDOAException;

    public IDocumentAligner getAligner(IDocumentDefinition toDefinition);

    public void copyFieldFrom(IDocument input, String fieldName)
            throws GeneralDOAException;

    public void copyFieldFrom(IDocument input, String sourceFieldName,
                              String destFileName) throws GeneralDOAException;

    public IDocument createCopy() throws GeneralDOAException;

    public IDocument createCopy(IDocumentFieldEvaluator evaluator)
            throws GeneralDOAException;

}
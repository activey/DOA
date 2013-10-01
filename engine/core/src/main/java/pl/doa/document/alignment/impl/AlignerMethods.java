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
/**
 *
 */
package pl.doa.document.alignment.impl;

import java.lang.reflect.Method;
import java.text.MessageFormat;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.IListDocumentFieldValue;

/**
 * @author activey
 */
public class AlignerMethods extends ScriptableObject {

    private final static Logger log = LoggerFactory
            .getLogger(AlignerMethods.class);

    private final IDocument input;
    private final IDocument output;

    public AlignerMethods(IDocument input, IDocument output) {
        this.input = input;
        this.output = output;
        // initialize methods
        initMethods();
    }

    private void initMethods() {
        Method[] methods = this.getClass().getDeclaredMethods();
        for (Method method : methods) {
            method.setAccessible(true);
            String methodName = method.getName();
            if (method.isAnnotationPresent(Function.class)) {
                FunctionObject function =
                        new FunctionObject(methodName, method, this);
                /*
				 * defineFunctionProperties(new String[] { methodName },
				 * AlignerMethods.class, ScriptableObject.DONTENUM);
				 */
                this.put(methodName, this, function);
            }
        }
    }

    @Function
    public static void set(Context cx, Scriptable thisObj, Object[] args,
                           org.mozilla.javascript.Function func) {
        AlignerMethods methods = (AlignerMethods) thisObj;
        String fieldName = (String) args[0];
        Object fieldValue = args[1];
        try {
            log.debug(MessageFormat.format("rewriting field: [{0}]",
                    (String) args[0]));
            methods.output.setFieldValue(fieldName, fieldValue);
        } catch (GeneralDOAException e) {
            log.error("", e);
        }
    }

    @Function
    public static void rewrite(Context cx, Scriptable thisObj, Object[] args,
                               org.mozilla.javascript.Function func) {
        AlignerMethods methods = (AlignerMethods) thisObj;

        try {
            log.debug(MessageFormat.format("rewriting field: [{0}]",
                    (String) args[0]));
            methods.output.copyFieldFrom(methods.input, (String) args[0]);
        } catch (GeneralDOAException e) {
            log.error("", e);
        }
    }

    @Function
    public static void copy(Context cx, Scriptable thisObj, Object[] args,
                            org.mozilla.javascript.Function func) {
        AlignerMethods methods = (AlignerMethods) thisObj;
        try {
            methods.output.setFieldValue((String) args[1],
                    methods.input.getFieldValue((String) args[0]));
        } catch (GeneralDOAException e) {
            log.error("", e);
        }
    }

    @Function
    public static void substract(Context cx, Scriptable thisObj, Object[] args,
                                 org.mozilla.javascript.Function func) {
        if (args.length == 0) {
            return;
        }
        AlignerMethods methods = (AlignerMethods) thisObj;
        String listFieldName = (String) args[0];
        IListDocumentFieldValue listField =
                (IListDocumentFieldValue) methods.input.getField(listFieldName);
        if (args.length == 1) {
            Iterable<IDocumentFieldValue> listFields = listField.iterateFields();
            for (IDocumentFieldValue field : listFields) {
                try {
                    methods.output.setFieldValue(field.getFieldName(), field);
                } catch (GeneralDOAException e) {
                    log.error("", e);
                }
            }
            return;
        }
        for (int i = 1; i < args.length; i++) {
            String fieldName = (String) args[i];
            IDocumentFieldValue fieldValue = listField.getListField(fieldName);
            if (fieldValue != null) {
                try {
                    IDocumentFieldValue outputField =
                            methods.output.getField(fieldName, true);
                    outputField.setFieldValue(fieldValue.getFieldValue());
                } catch (GeneralDOAException e) {
                    log.error("", e);
                }
            }
        }
    }

    @Override
    public String getClassName() {
        return this.getClass().getSimpleName();
    }

}

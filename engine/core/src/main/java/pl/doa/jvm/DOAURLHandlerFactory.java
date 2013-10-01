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
package pl.doa.jvm;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import pl.doa.IDOA;

public class DOAURLHandlerFactory implements URLStreamHandlerFactory {

    private final IDOA doa;
    private final URLStreamHandlerFactory otherFactory;

    public DOAURLHandlerFactory(IDOA doa, URLStreamHandlerFactory otherFactory) {
        this.doa = doa;
        this.otherFactory = otherFactory;
    }

    public DOAURLHandlerFactory(IDOA doa) {
        this(doa, null);
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if ("doa".equals(protocol)) {
            return new DOAStreamHandler(doa);
        }
        if (otherFactory == null) {
            return null;
        }
        return otherFactory.createURLStreamHandler(protocol);
    }

    public static void attachFactory(IDOA doa) throws Exception {
        Field factoryField = URL.class.getDeclaredField("factory");
        factoryField.setAccessible(true);

        URLStreamHandlerFactory factorySet =
                (URLStreamHandlerFactory) factoryField.get(URL.class);
        if (factorySet == null) {
            URL.setURLStreamHandlerFactory(new DOAURLHandlerFactory(doa));
        } else {
            URLStreamHandlerFactory newFactory =
                    new DOAURLHandlerFactory(doa, factorySet);
            factoryField.set(URL.class, newFactory);
        }

    }
}

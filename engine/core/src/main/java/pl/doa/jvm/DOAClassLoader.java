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
package pl.doa.jvm;

import org.lightwolf.tools.LightWolfEnhancer;
import org.lightwolf.tools.PublicByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.IDOA;
import pl.doa.artifact.IArtifact;
import pl.doa.entity.IEntityEvaluator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author activey
 */
public class DOAClassLoader extends URLClassLoader {

    private final static Logger log = LoggerFactory
            .getLogger(DOAClassLoader.class);

    private ClassBytesLoader mBytesLoader;

    private IDOA doa;

    private List<URL> urls = new ArrayList<URL>();

    public DOAClassLoader(IDOA doa) {
        this(doa, (ClassLoader) null);
    }

    public DOAClassLoader(IDOA doa, ClassLoader parent) {
        this(doa, parent, null);
    }

    public DOAClassLoader(IDOA doa, IEntityEvaluator evaluator) {
        this(doa, null, evaluator);
    }

    public DOAClassLoader(IDOA doa, ClassLoader parent,
                          IEntityEvaluator evaluator) {
        super(new URL[0], parent, new DOAURLHandlerFactory(doa));
        this.doa = doa;
        log.debug(MessageFormat.format(
                "Initializing repository class loader, parent: {0}", parent));
        log.debug("Loading artifacts ...");
        Iterable<IArtifact> artifacts = doa.getArtifacts(evaluator);
        for (IArtifact artifact : artifacts) {
            // rejestrowanie nowej pozycji w classloaderze
            try {
                registerClassloaderArtifact(artifact);
            } catch (Exception e) {
                log.error("Unable to regiter classloader artifact", e);
                continue;
            }
        }

        this.mBytesLoader = new ClassBytesLoader(this);
    }

    public byte[] getClassBytes(String className, boolean reloadAutomatically)
            throws ClassNotFoundException {
        if (mBytesLoader == null) {
            return null;
        }
        return mBytesLoader.getClassBytes(className.replace('.', '/')
                + ".class");
    }

    public Class<?> loadContinuableClass(String name)
            throws ClassNotFoundException, IOException {
        return loadContinuableClass(name, false);
    }

    public Class<?> loadContinuableClass(String name, boolean normalFirst)
            throws ClassNotFoundException, IOException {
        if (normalFirst) {
            Class<?> clazz = loadClass(name);
            if (clazz != null) {
                return clazz;
            }
        }

        synchronized (name.intern()) {
            byte[] classBytes = getClassBytes(name, false);
            if (classBytes == null) {
                return loadClass(name);
            }
            PublicByteArrayOutputStream classStream =
                    new PublicByteArrayOutputStream();
            classStream.write(classBytes);

            DOAClassLoader separate = new DOAClassLoader(doa, getClass().getClassLoader());
            LightWolfEnhancer enhancer = new LightWolfEnhancer(separate);
            int transformResult = enhancer.transform(classStream);
            if (transformResult == 0) {
                log.warn(MessageFormat
                        .format("Class [{0}] can not be processed as continuable class! " +
                                "Make sure that @FlowMethod annotation is used. " +
                                "Using normal classloader mechanism ...",
                                name));
                return (loadClass(name, true));
            }

            byte[] transformedBytes = classStream.toByteArray();

            if (transformedBytes != null) {
                classBytes = transformedBytes;
            }
            // tworzenie nowego classloadera
            return separate.defineClass(name, transformedBytes, 0,
                    transformedBytes.length);
        }
    }

    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, true);
    }

    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = loadClass0(name, false);
        if (clazz != null && resolve) {
            resolveClass(clazz);
        }
        return clazz;
    }

    public Class<?> loadClass0(String name, boolean separateClassLoader)
            throws ClassNotFoundException {
        //log.debug(MessageFormat.format("Loading class: [{0}]", name));
        if (separateClassLoader) {
            ClassLoader separate = new DOAClassLoader(doa, this.getParent());
            return separate.loadClass(name);
        }
        Class loadedClass = findLoadedClass(name);
        if (loadedClass == null) {
            try {
                if (getParent() != null) {
                    loadedClass = getParent().loadClass(name);
                } else {
                    loadedClass = getSystemClassLoader().loadClass(name);
                }
            } catch (ClassNotFoundException e) {
            } catch (NoClassDefFoundError e1) {
            }
        }
        if (loadedClass == null) {
            try {
                loadedClass = findClass(name);
            } catch (ClassNotFoundException cnfe) {
                // ignore
            }
        }
        if (loadedClass == null) {
            throw new ClassNotFoundException(name);
        }
        return loadedClass;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        URL resource = findResource(name);
        if (resource == null && getParent() != null) {
            InputStream parentStream = getParent().getResourceAsStream(name);
            return parentStream;
        }
        try {
            return (resource == null) ? null : resource.openStream();
        } catch (IOException e) {
            log.error("", e);
            return null;
        }
    }

    @Override
    public URL[] getURLs() {
        return super.getURLs();
    }

    public IDOA getDoa() {
        return doa;
    }

    public void setDoa(IDOA doa) {
        this.doa = doa;
    }

    public void addURL(URL url) {
        urls.add(url);
        super.addURL(url);
    }

    private void registerClassloaderArtifact(IArtifact artifact)
            throws Exception {
        String artifactUrl =
                MessageFormat.format("doa:{0}/{1}", IDOA.ARTIFACTS_CONTAINER,
                        artifact.getName());
        addURL(new URL(artifactUrl));
    }
}

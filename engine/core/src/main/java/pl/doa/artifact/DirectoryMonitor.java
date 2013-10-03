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
package pl.doa.artifact;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.*;

public class DirectoryMonitor {
    private final static Logger log = LoggerFactory
            .getLogger(DirectoryMonitor.class);
    private final long pollingInterval;
    private Timer timer_;
    private File directory_;
    private List<File> directoryFiles = new ArrayList<File>();
    private Collection listeners_; // of WeakReference(FileListener)
    private long lastModifiedTime;

    public DirectoryMonitor(long pollingInterval) {
        this.pollingInterval = pollingInterval;
        listeners_ = new ArrayList();
    }

    public void stop() {
        timer_.cancel();
    }

    public void setDirectory(File directory) {
        if (!directory.isDirectory()) {
            return;
        }
        directory_ = directory;
        /*
         * pobieranie listy plikow z katalogu
		 */
        File[] dirFiles = directory.listFiles();
        for (File file : dirFiles) {
            directoryFiles.add(file);
        }
    }

    public void addListener(DirectoryListener fileListener) {
        // Don't add if its already there
        for (Iterator i = listeners_.iterator(); i.hasNext(); ) {
            WeakReference reference = (WeakReference) i.next();
            DirectoryListener listener = (DirectoryListener) reference.get();
            if (listener == fileListener)
                return;
        }

        // Use WeakReference to avoid memory leak if this becomes the
        // sole reference to the object.
        listeners_.add(new WeakReference(fileListener));
    }

    public void removeListener(DirectoryListener fileListener) {
        for (Iterator i = listeners_.iterator(); i.hasNext(); ) {
            WeakReference reference = (WeakReference) i.next();
            DirectoryListener listener = (DirectoryListener) reference.get();
            if (listener == fileListener) {
                i.remove();
                break;
            }
        }
    }

    public void start() {
        log.debug("starting up directory monitor ...");
        timer_ = new Timer(true);
        timer_.schedule(new FileMonitorNotifier(), 0, pollingInterval);
    }

    private class FileMonitorNotifier extends TimerTask {
        public void run() {
            if (directory_ == null) {
                log.debug("Unable to find deploy directory, set it first and restart DOA");
                return;
            }
            long newModifiedTime =
                    directory_.exists() ? directory_.lastModified() : -1;

            // Chek if file has changed
            if (newModifiedTime != lastModifiedTime) {

                // Register new modified time
                lastModifiedTime = new Long(newModifiedTime);

                // Notify listeners
                for (Iterator j = listeners_.iterator(); j.hasNext(); ) {
                    WeakReference reference = (WeakReference) j.next();
                    DirectoryListener listener =
                            (DirectoryListener) reference.get();
                    /*
					 * lista plikow po modyfikacji
					 */
                    int type = -1;
                    File[] afterModFiles = directory_.listFiles();
                    List<File> modifiedFiles = new ArrayList<File>();
                    if (directoryFiles.size() < afterModFiles.length) {
                        for (File modFile : afterModFiles) {
                            if (!directoryFiles.contains(modFile)) {
                                modifiedFiles.add(modFile);
                            }
                        }
                        type = DirectoryListener.TYPE_ADD;
                    } else {
                        for (File file : directoryFiles) {
                            boolean exists = false;
                            inner:
                            for (File modifiedFile : afterModFiles) {
                                if (modifiedFile.equals(file)) {
                                    exists = true;
                                    break inner;
                                }
                            }
                            if (!exists) {
                                modifiedFiles.add(file);
                            }
                        }
                        type = DirectoryListener.TYPE_REMOVE;
                    }
					/*
					 * tworzenie nowej listy plikow w katalogu
					 */
                    directoryFiles = new ArrayList<File>();
                    for (File modFile : afterModFiles) {
                        directoryFiles.add(modFile);
                    }
                    // Remove from list if the back-end object has been GC'd
                    if (listener == null)
                        j.remove();
                    else
                        listener.directoryContentsChanged(directory_,
                                modifiedFiles.toArray(new File[0]), type);
                }
            }
        }
    }
}

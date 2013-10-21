package pl.doa.utils;

import pl.doa.artifact.IJarEntryMatcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarUtils {

    public static InputStream findJarEntry(File dataFile, IJarEntryMatcher entryMatcher)
            throws IOException {
        JarFile jarFile = new JarFile(dataFile);
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            if (entryMatcher.entryMatch(jarEntry)) {
                return jarFile.getInputStream(jarEntry);
            }
        }
        return null;
    }
}

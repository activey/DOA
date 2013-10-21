package pl.doa.artifact;

import java.util.jar.JarEntry;

public class JarEntryNameMatcher implements IJarEntryMatcher {

    private final String entryName;

    public JarEntryNameMatcher(String entryName) {
        this.entryName = entryName;
    }

    @Override
    public boolean entryMatch(JarEntry entry) {
        if (entry.getName().endsWith(this.entryName)) {
            onEntryMatched(entry);
            return true;
        }
        return false;
    }

    protected void onEntryMatched(JarEntry entry) {

    }
}

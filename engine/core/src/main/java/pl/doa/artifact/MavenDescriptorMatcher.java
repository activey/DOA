package pl.doa.artifact;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.jar.JarEntry;

public class MavenDescriptorMatcher extends JarEntryNameMatcher {

    private static final Logger LOG = LoggerFactory.getLogger(MavenDescriptorMatcher.class);

    private static final String JAR_ENTRY_MAVEN_DESCRIPTOR = "pom.xml";

    public MavenDescriptorMatcher() {
        super(JAR_ENTRY_MAVEN_DESCRIPTOR);
    }

    @Override
    protected void onEntryMatched(JarEntry entry) {
        LOG.debug(MessageFormat.format("Found maven artifact descriptor file: {0}, processing ...",
                entry.getName()));

    }
}

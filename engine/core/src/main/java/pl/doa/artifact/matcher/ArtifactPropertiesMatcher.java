package pl.doa.artifact.matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.jar.JarEntry;

public class ArtifactPropertiesMatcher extends JarEntryNameMatcher {

    private static final Logger LOG = LoggerFactory.getLogger(ArtifactPropertiesMatcher.class);

    private static final String JAR_ENTRY_ARTIFACT_PROPERTIES = "artifact.properties";

    public ArtifactPropertiesMatcher() {
        super(JAR_ENTRY_ARTIFACT_PROPERTIES);
    }

    @Override
    protected void onEntryMatched(JarEntry entry) {
        LOG.debug(MessageFormat.format("Found artifact properties file: {0}",
                JAR_ENTRY_ARTIFACT_PROPERTIES));

    }
}

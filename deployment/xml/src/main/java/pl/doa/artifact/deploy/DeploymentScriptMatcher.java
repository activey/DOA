package pl.doa.artifact.deploy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.artifact.matcher.JarEntryNameMatcher;

import java.text.MessageFormat;
import java.util.jar.JarEntry;

public class DeploymentScriptMatcher extends JarEntryNameMatcher {
    private static final Logger LOG = LoggerFactory.getLogger(DeploymentScriptMatcher.class);

    private static final String JAR_ENTRY_DEPLOYMENT_SCRIPT = "deploy.xml";

    public DeploymentScriptMatcher() {
        super(JAR_ENTRY_DEPLOYMENT_SCRIPT);
    }

    @Override
    protected void onEntryMatched(JarEntry entry) {
        LOG.debug(MessageFormat.format("Found deployment script file: {0}",
                JAR_ENTRY_DEPLOYMENT_SCRIPT));

    }
}

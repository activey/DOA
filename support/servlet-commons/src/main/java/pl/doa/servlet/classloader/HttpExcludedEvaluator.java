package pl.doa.servlet.classloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.artifact.IArtifact;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;

import java.text.MessageFormat;

public class HttpExcludedEvaluator implements IEntityEvaluator {

    public static final Logger LOG = LoggerFactory.getLogger(HttpExcludedEvaluator.class);

    @Override
    public boolean isReturnableEntity(IEntity currentEntity) {
        if (!(currentEntity instanceof IArtifact)) {
            return false;
        }
        IArtifact artifact = (IArtifact) currentEntity;
        String artifactId = artifact.getArtifactId();
        String groupId = artifact.getGroupId();
        if ((artifactId != null && artifactId.contains("servlet-api"))
                || (groupId != null && groupId
                .startsWith("org.apache.tomcat"))) {
            LOG.debug(MessageFormat.format(
                    "Ignoring [{0}] artifact ...", artifactId));
            return false;
        }
        return true;
    }
}

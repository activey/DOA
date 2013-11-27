package pl.doa.entity.evaluator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.artifact.IArtifact;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class EntityArtifactDependenciesEvaluator implements IEntityEvaluator {

    private final static Logger LOG = LoggerFactory.getLogger(EntityArtifactDependenciesEvaluator.class);

    private final List<IArtifact> classpath = new ArrayList<IArtifact>();

    private EntityArtifactDependenciesEvaluator(IEntity entity) {
        collectEntityArtifactDependencies(entity);
    }

    private EntityArtifactDependenciesEvaluator(IArtifact entityArtifact) {
        collectArtifactDependencies(entityArtifact);
    }

    @Override
    public boolean isReturnableEntity(IEntity currentEntity) {
        if (!(currentEntity instanceof IArtifact)) {
            return false;
        }
        IArtifact artifact = (IArtifact) currentEntity;
        boolean oneOf = false;
        for (IArtifact dependency : classpath) {
            if (dependency.equals(artifact)) {
                oneOf = true;
                break;
            }
        }
        if (!oneOf) {
            return false;
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace(MessageFormat.format("Using artifact dependency: [{0}.{1}.{2}]", artifact.getGroupId(),
                    artifact.getArtifactId(), artifact.getVersion()));
        }
        return true;
    }

    private void collectEntityArtifactDependencies(IEntity entity) {
        IArtifact artifact = entity.getArtifact();
        if (artifact == null) {
            return;
        }
        collectArtifactDependencies(artifact);
    }

    private void collectArtifactDependencies(IArtifact artifact) {
        classpath.add(artifact);
        if (LOG.isTraceEnabled()) {
            LOG.trace(MessageFormat.format("Analyzing dependencies for artifact: [{0}.{1}.{2}]", artifact.getGroupId(),
                    artifact.getArtifactId(), artifact.getVersion()));
        }
        List<IArtifact> dependencies = artifact.getDependencies();
        for (IArtifact dependency : dependencies) {
            collectArtifactDependencies(dependency);
        }
    }

    public static EntityArtifactDependenciesEvaluator withEntityDependencies(IEntity entityWithArtifact) {
        return new EntityArtifactDependenciesEvaluator(entityWithArtifact);
    }

    public static EntityArtifactDependenciesEvaluator withArtifactDependencies(IArtifact artifact) {
        return new EntityArtifactDependenciesEvaluator(artifact);
    }

}

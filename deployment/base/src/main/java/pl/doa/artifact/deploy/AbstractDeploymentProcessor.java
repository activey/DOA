package pl.doa.artifact.deploy;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.agent.IAgent;
import pl.doa.artifact.IArtifact;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityReference;

/**
 * @author activey
 * @date 02.10.13 20:41
 */
public abstract class AbstractDeploymentProcessor implements IDeploymentProcessor {

    private final IArtifact artifact;
    private final IDOA doa;

    public AbstractDeploymentProcessor(IArtifact artifact, IDOA doa) {
        this.artifact = artifact;
        this.doa = doa;
    }

    public final IEntitiesContainer createEntitiesContainer(String name, IEntitiesContainer parent) throws GeneralDOAException {
        IEntitiesContainer container = doa.createContainer(name, parent);
        artifact.registerEntity(container);
        return container;
    }

    @Override
    public final IEntityReference createReference(String name, IEntity entity, IEntitiesContainer parent) throws GeneralDOAException {
        IEntityReference reference = doa.createReference(name, entity);
        parent.addEntity(reference);
        return reference;
    }

    @Override
    public final IAgent createAgent(String name, IEntitiesContainer parent) throws GeneralDOAException {
        IAgent agent = doa.createAgent(name, parent);
        return agent;
    }
}

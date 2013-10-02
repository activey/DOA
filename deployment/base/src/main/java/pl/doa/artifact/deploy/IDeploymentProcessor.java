package pl.doa.artifact.deploy;

import pl.doa.GeneralDOAException;
import pl.doa.agent.IAgent;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntityReference;
import pl.doa.entity.IEntity;

import java.io.File;

public interface IDeploymentProcessor {

    public void process(File deployedFile) throws Exception;

    public IEntitiesContainer createEntitiesContainer(String name, IEntitiesContainer parent) throws GeneralDOAException;

    public IEntityReference createReference(String name, IEntity entity, IEntitiesContainer parent) throws GeneralDOAException;

    public IAgent createAgent(String name, IEntitiesContainer parent) throws GeneralDOAException;
}

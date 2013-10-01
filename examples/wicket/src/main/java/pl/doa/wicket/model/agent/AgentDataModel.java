package pl.doa.wicket.model.agent;

import org.apache.wicket.model.IModel;

import pl.doa.agent.IAgent;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.wicket.WicketDOAApplication;

public class AgentDataModel<T extends IEntity> implements IModel<T> {

	private final String agentContainerPath;

	public AgentDataModel(String agentContainerPath) {
		this.agentContainerPath = agentContainerPath;
	}

	@Override
	public void detach() {
	}

	@Override
	public T getObject() {
		IAgent agent = WicketDOAApplication.getAgent();
		if (agent == null) {
			return null;
		}
		IEntitiesContainer homeContainer = agent.getContainer();
		IEntity entity =
				homeContainer.lookupEntityByLocation(agentContainerPath);
		if (entity == null) {
			return null;
		}
		try {
			return (T) entity;
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Override
	public void setObject(T object) {
	}

}

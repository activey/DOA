package pl.doa.wicket.model.container;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import pl.doa.IDOA;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.wicket.WicketDOAApplication;

public class ContainerEntitiesModel<T extends IEntity> extends
		AbstractReadOnlyModel<Iterable<T>> {

	private IModel<IEntitiesContainer> containerModel;
	private long containerId = 0;
	private IEntityEvaluator evaluator;

	public ContainerEntitiesModel(long containerId) {
		this.containerId = containerId;
	}

	public ContainerEntitiesModel(IModel<IEntitiesContainer> containerModel,
			IEntityEvaluator evaluator) {
		this.containerModel = containerModel;
		this.evaluator = evaluator;
	}

	public ContainerEntitiesModel(IEntitiesContainer container,
			IEntityEvaluator evaluator) {
		this(new EntitiesContainerModel(container), evaluator);
	}

	public ContainerEntitiesModel(String entityLocation,
			IEntityEvaluator evaluator) {
		this(new EntitiesContainerModel(entityLocation), evaluator);
	}

	@Override
	public void detach() {
	}

	@Override
	public Iterable<T> getObject() {
		IEntitiesContainer container = null;
		if (this.containerModel != null) {
			IEntitiesContainer modelValue = this.containerModel.getObject();
			if (modelValue != null) {
				container = modelValue;
			}
		}
		IDOA doa = WicketDOAApplication.get().getDoa();
		if (container == null && containerId != 0) {
			container = (IEntitiesContainer) doa.lookupByUUID(containerId);
		}
		if (container == null) {
			return null;
		}
		Iterable<T> containerEntities =
				(Iterable<T>) container.getEntities(evaluator);
		return containerEntities;
	}

}

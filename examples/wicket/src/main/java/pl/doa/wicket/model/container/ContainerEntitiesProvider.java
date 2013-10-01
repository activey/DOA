package pl.doa.wicket.model.container;

import java.util.Iterator;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.entity.sort.IEntitiesSortComparator;
import pl.doa.wicket.model.EntityModel;

public class ContainerEntitiesProvider<T extends IEntity> implements
		IDataProvider<T> {

	private IModel<IEntitiesContainer> containerModel;
	private IEntityEvaluator evaluator;
	private final boolean deep;

	public ContainerEntitiesProvider(IModel<IEntitiesContainer> containerModel,
			IEntityEvaluator evaluator, boolean deep) {
		this.containerModel = containerModel;
		this.evaluator = evaluator;
		this.deep = deep;
	}

	public ContainerEntitiesProvider(IModel<IEntitiesContainer> containerModel,
			IEntityEvaluator evaluator) {
		this(containerModel, evaluator, false);
	}

	public ContainerEntitiesProvider(IModel<IEntitiesContainer> containerModel,
			boolean deep) {
		this(containerModel, null, deep);
	}

	public ContainerEntitiesProvider(IModel<IEntitiesContainer> containerModel) {
		this(containerModel, false);
	}

	public ContainerEntitiesProvider(IEntitiesContainer container,
			IEntityEvaluator evaluator, boolean deep) {
		this(new EntitiesContainerModel(container), evaluator, deep);
	}

	public ContainerEntitiesProvider(IEntitiesContainer container,
			IEntityEvaluator evaluator) {
		this(container, evaluator, false);
	}

	public ContainerEntitiesProvider(IEntitiesContainer container) {
		this(container, false);
	}

	public ContainerEntitiesProvider(IEntitiesContainer container, boolean deep) {
		this(container, null, deep);
	}

	public ContainerEntitiesProvider(String entityLocation,
			IEntityEvaluator evaluator, boolean deep) {
		this(new EntitiesContainerModel(entityLocation), evaluator, deep);
	}

	public ContainerEntitiesProvider(String entityLocation,
			IEntityEvaluator evaluator) {
		this(entityLocation, evaluator, false);
	}

	public ContainerEntitiesProvider(String entityLocation) {
		this(entityLocation, false);
	}

	public ContainerEntitiesProvider(String entityLocation, boolean deep) {
		this(entityLocation, null, deep);
	}

	@Override
	public void detach() {
	}

	protected IEntitiesSortComparator<T> getSortComparator() {
		return null;
	}

	@Override
	public Iterator<? extends T> iterator(long first, long count) {
		IEntitiesContainer container = containerModel.getObject();
		if (container == null) {
			return null;
		}

		return (Iterator<? extends T>) container.getEntities((int) first,
				(int) count, getSortComparator(), evaluator, deep).iterator();
	}

	@Override
	public long size() {
		IEntitiesContainer container = containerModel.getObject();
		return (container == null) ? 0 : container.countEntities(evaluator,
				deep);
	}

	@Override
	public IModel<T> model(T object) {
		return new EntityModel<T>(object);
	}

}

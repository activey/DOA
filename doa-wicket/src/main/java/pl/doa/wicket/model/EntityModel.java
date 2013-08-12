/**
 * 
 */
package pl.doa.wicket.model;

import org.apache.wicket.model.LoadableDetachableModel;

import pl.doa.IDOA;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.DetachedEntity;
import pl.doa.entity.IEntity;
import pl.doa.impl.EntityLocationIterator;
import pl.doa.utils.PathIterator;
import pl.doa.wicket.WicketDOAApplication;

/**
 * @author activey
 * 
 */
public class EntityModel<T extends IEntity> extends LoadableDetachableModel<T> {

	private T entity;
	private PathIterator<String> entityLocation;
	private long entityId = -1;
	private long doaId = 0;

	public EntityModel(long entityId) {
		this.entityId = entityId;
	}

	public EntityModel(T entity) {
		if (entity == null) {
			return;
		}
		if (entity.isStored()) {
			this.doaId = entity.getDoa().getId();
			this.entityLocation =
					new EntityLocationIterator(entity.getLocation(), true);
			this.entityId = entity.getId();
			return;
		}
		this.entity = entity;
	}

	public EntityModel(String entityLocation, boolean applicationRelative) {
		if (applicationRelative) {
			IEntitiesContainer appContainer =
					WicketDOAApplication.get().getApplicationContainer();
			this.entityLocation =
					new EntityLocationIterator(appContainer.getLocation()
							+ entityLocation, true);
			return;
		}

		this.entityLocation = new EntityLocationIterator(entityLocation, true);
	}

	public EntityModel(String entityLocation) {
		this(entityLocation, true);
	}

	@Override
	public void detach() {
		if (entity == null) {
			return;
		}
		if (entity instanceof DetachedEntity) {
			DetachedEntity detached = (DetachedEntity) entity;
			//detached.detach();
			return;
		}
		this.entity = null;
		//this.entityLocation = null;
		//this.entityId = -1;
		//this.doaId = 0;	
	}

	@Override
	public T load() {
		if (this.entity != null) {
			return this.entity;
		}
		T overrided = getEntity();
		if (overrided != null) {
			this.entity = overrided;
			return this.entity;
		}
		IDOA doa = WicketDOAApplication.get().getDoa();
		if (doaId > 0) {
			doa = (IDOA) doa.lookupByUUID(doaId);
		}
		if (entityLocation != null && entityLocation.getLength() > 0) {
			this.entity = (T) doa.lookupEntityByLocation(entityLocation);
			if (entity != null) {
				return this.entity;
			}
		}
		T foundById = null;
		if (entityId > -1) {
			try {
				foundById = (T) doa.lookupByUUID(entityId);
				if (foundById != null) {
					this.entity = foundById;
				}
			} catch (RuntimeException e) {
				//
			}
		}
		return this.entity;
	}

	@Override
	public void setObject(T entity) {
		if (entity == null) {
			return;
		}
		this.entity = null;
		this.entityLocation = null;
		this.entityId = -1;
		this.doaId = 0;

		if (entity.isStored()) {
			this.doaId = entity.getDoa().getId();
			this.entityLocation =
					new EntityLocationIterator(entity.getLocation(), true);
			this.entityId = entity.getId();
			super.setObject(entity);
			return;
		}
		this.entity = entity;
		super.setObject(entity);
	}

	protected T getEntity() {
		return this.entity;
	}
}

package pl.doa.entity.event;

public enum EntityEventType {

	ENTITY_CREATED(false),

	SERVICE_EXECUTED(true),
	
	SERVICE_EXECUTED_BY(true);

	private final boolean removeAfterProcessing;

	EntityEventType(boolean removeAfterProcessing) {
		this.removeAfterProcessing = removeAfterProcessing;

	}

	public boolean isRemoveAfterProcessing() {
		return removeAfterProcessing;
	}

}

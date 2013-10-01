package pl.doa.entity.event;

import pl.doa.entity.IEntity;

public interface IEntityEventDescription {

	
	public static final String EVENT_TX_ID = "eventTransactionId";
	/**
	 * Zwraca referencje do obiektu, ktory wygenerowal zdarzenie.
	 * 
	 * @return
	 */
	public IEntity getSourceEntity();

	public void setSourceEntity(IEntity sourceEntity);

	/**
	 * Zwraca typ zdarzenia wygenerowanego przez obiekt.
	 * 
	 * @return
	 */
	public EntityEventType getEventType();

	public Iterable<String> getEventPropertyNames();

	public Object getEventProperty(String propertyName);

	public void setEventProperty(String propertyName, Object propertyValue);

	public String getStringProperty(String propertyName);

	public void setStringProperty(String propertyName, String propertyValue);

	public Integer getIntProperty(String propertyName);

	public void setIntProperty(String propertyName, int propertyValue);

	public IEntity getReferenceProperty(String propertyName);

	public void setReferenceProperty(String propertyName, IEntity propertyValue);

}

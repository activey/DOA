/**
 * 
 */
package pl.doa.entity.event;


/**
 * @author activey
 * 
 */
public interface IEntityEventReceiver {

	public void handleEvent(IEntityEventDescription eventDescription)
			throws Exception;

}

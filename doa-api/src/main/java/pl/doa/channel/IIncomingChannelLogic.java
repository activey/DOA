/**
 * 
 */
package pl.doa.channel;

import java.io.Serializable;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.entity.event.IEntityEventReceiver;
import pl.doa.entity.startable.IStartableEntityLogic;
import pl.doa.service.IRunningService;

/**
 * @author Damian
 *
 */
public interface IIncomingChannelLogic extends IEntityEventReceiver,
		IStartableEntityLogic, Serializable {

	public abstract IRunningService handleIncoming(IDocument document)
			throws GeneralDOAException;
}

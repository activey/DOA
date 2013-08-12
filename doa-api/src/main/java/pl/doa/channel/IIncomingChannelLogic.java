/**
 *
 */
package pl.doa.channel;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.entity.event.IEntityEventReceiver;
import pl.doa.entity.startable.IStartableEntityLogic;
import pl.doa.service.IRunningService;

import java.io.Serializable;

/**
 * @author Damian
 */
public interface IIncomingChannelLogic extends IEntityEventReceiver,
        IStartableEntityLogic, Serializable {

    public abstract IRunningService handleIncoming(IDocument document)
            throws GeneralDOAException;
}

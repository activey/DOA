/**
 *
 */
package pl.doa.channel;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.entity.event.IEntityEventReceiver;
import pl.doa.entity.startable.IStartableEntity;
import pl.doa.service.IRunningService;

/**
 * @author damian
 */
public interface IIncomingChannel extends IStartableEntity,
        IEntityEventReceiver {

    public abstract IRunningService handleIncoming(IDocument document)
            throws GeneralDOAException;
}

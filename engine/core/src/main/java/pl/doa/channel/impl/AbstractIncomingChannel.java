/**
 *
 */
package pl.doa.channel.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.channel.IIncomingChannel;
import pl.doa.channel.IIncomingChannelLogic;
import pl.doa.document.IDocument;
import pl.doa.entity.event.IEntityEventDescription;
import pl.doa.entity.startable.impl.AbstractStartableEntity;
import pl.doa.service.IRunningService;

/**
 * @author Damian
 */
public abstract class AbstractIncomingChannel extends AbstractStartableEntity implements
        IIncomingChannel {

    private final static Logger log = LoggerFactory
            .getLogger(AbstractChannel.class);

    public AbstractIncomingChannel(IDOA doa) {
        super(doa);
    }

    @Override
    public final IRunningService handleIncoming(IDocument document)
            throws GeneralDOAException {
        IIncomingChannelLogic logic;
        try {
            logic = (IIncomingChannelLogic) getDoa().getRunning(this);
        } catch (ClassCastException e) {
            log.error("", e);
            return null;
        }
        return logic.handleIncoming(document);
    }

    @Override
    public final void handleEvent(IEntityEventDescription eventDescription)
            throws Exception {
        try {
            IIncomingChannelLogic logic = (IIncomingChannelLogic) getDoa().getRunning(this);
            if (logic == null) {
                log.debug("Channel not started yet ...");
                return;
            }
            logic.handleEvent(eventDescription);
        } catch (ClassCastException e) {
            log.error("", e);
        }

    }

}

/**
 *
 */
package pl.doa.channel.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.channel.IOutgoingChannel;
import pl.doa.channel.IOutgoingChannelLogic;
import pl.doa.document.IDocument;
import pl.doa.entity.startable.impl.AbstractStartableEntity;

/**
 * @author Damian
 */
public abstract class AbstractOutgoingChannel extends AbstractStartableEntity implements
        IOutgoingChannel {

    private final static Logger log = LoggerFactory
            .getLogger(AbstractChannel.class);

    public AbstractOutgoingChannel(IDOA doa) {
        super(doa);
    }

    @Override
    public final IDocument handleOutgoing(IDocument document)
            throws GeneralDOAException {
        IOutgoingChannelLogic logic;
        try {
            logic = (IOutgoingChannelLogic) getDoa().getRunning(this);
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
        return logic.handleOutgoing(document);
    }

}

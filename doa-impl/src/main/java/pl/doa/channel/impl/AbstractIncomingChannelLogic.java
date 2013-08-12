/**
 *
 */
package pl.doa.channel.impl;

import pl.doa.IDOA;
import pl.doa.channel.IIncomingChannel;
import pl.doa.channel.IIncomingChannelLogic;
import pl.doa.entity.event.IEntityEventDescription;
import pl.doa.entity.startable.IStartableEntity;

/**
 * @author Damian
 */
public abstract class AbstractIncomingChannelLogic implements IIncomingChannelLogic {

    protected IDOA doa;

    protected IIncomingChannel channel;

    @Override
    public final void setDoa(IDOA doa) {
        this.doa = doa;
    }

    @Override
    public final void setStartableEntity(IStartableEntity startableEntity) {
        setChannel((IIncomingChannel) startableEntity);
    }

    public void setChannel(IIncomingChannel channel) {
        this.channel = channel;
    }

    public IIncomingChannel getChannel() {
        return this.channel;
    }

    @Override
    public void handleEvent(IEntityEventDescription eventDescription)
            throws Exception {
    }

}

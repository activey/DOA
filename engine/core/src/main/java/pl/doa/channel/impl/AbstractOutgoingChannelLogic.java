/**
 *
 */
package pl.doa.channel.impl;

import pl.doa.IDOA;
import pl.doa.channel.IOutgoingChannel;
import pl.doa.channel.IOutgoingChannelLogic;
import pl.doa.entity.startable.IStartableEntity;

/**
 * @author Damian
 */
public abstract class AbstractOutgoingChannelLogic implements IOutgoingChannelLogic {

    protected IDOA doa;

    protected IOutgoingChannel channel;

    @Override
    public final void setDoa(IDOA doa) {
        this.doa = doa;
    }

    @Override
    public final void setStartableEntity(IStartableEntity startableEntity) {
        setChannel((IOutgoingChannel) startableEntity);
    }

    public void setChannel(IOutgoingChannel channel) {
        this.channel = channel;
    }

    public IOutgoingChannel getChannel() {
        return this.channel;
    }

}

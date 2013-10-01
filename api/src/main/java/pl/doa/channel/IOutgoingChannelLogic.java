/**
 *
 */
package pl.doa.channel;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.entity.startable.IStartableEntityLogic;

import java.io.Serializable;

/**
 * @author Damian
 */
public interface IOutgoingChannelLogic extends IStartableEntityLogic, Serializable {

    /**
     * TODO opis metody
     *
     * @param out
     * @return
     */
    public abstract IDocument handleOutgoing(IDocument out)
            throws GeneralDOAException;

}

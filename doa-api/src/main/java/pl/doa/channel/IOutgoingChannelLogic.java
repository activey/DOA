/**
 * 
 */
package pl.doa.channel;

import java.io.Serializable;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.entity.startable.IStartableEntity;
import pl.doa.entity.startable.IStartableEntityLogic;

/**
 * @author Damian
 *
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

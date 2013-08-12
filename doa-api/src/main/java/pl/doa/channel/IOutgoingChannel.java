/**
 *
 */
package pl.doa.channel;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.entity.startable.IStartableEntity;

/**
 * @author damian
 */
public interface IOutgoingChannel extends IStartableEntity {

    public abstract IDocument handleOutgoing(IDocument document)
            throws GeneralDOAException;
}

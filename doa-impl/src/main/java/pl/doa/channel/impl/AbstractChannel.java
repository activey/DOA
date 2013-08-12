/*******************************************************************************
 * Copyright 2011 Inhibi Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are
 * permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright 
 * notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright 
 * notice, this list
 *        of conditions and the following disclaimer in the documentation 
 * and/or other materials
 *        provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY INHIBI LTD ``AS IS'' AND ANY 
 * EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * INHIBI LTD OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation 
 * are those of the authors and should not be interpreted as representing 
 * official policies, either expressed or implied, of Inhibi Ltd.
 *
 * Contributors:
 *    Inhibi Ltd - initial API and implementation
 *******************************************************************************/
/**
 *
 */
package pl.doa.channel.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.channel.IChannel;
import pl.doa.channel.IChannelLogic;
import pl.doa.document.IDocument;
import pl.doa.entity.event.IEntityEventDescription;
import pl.doa.entity.startable.impl.AbstractStartableEntity;
import pl.doa.service.IRunningService;

/**
 * @author activey
 */
public abstract class AbstractChannel extends AbstractStartableEntity implements
        IChannel {

    private final static Logger log = LoggerFactory
            .getLogger(AbstractChannel.class);

    public AbstractChannel(IDOA doa) {
        super(doa);
    }

    @Override
    public final IDocument handleOutgoing(IDocument document)
            throws GeneralDOAException {
        IChannelLogic logic;
        try {
            logic = (IChannelLogic) getDoa().getRunning(this);
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
        return logic.handleOutgoing(document);
    }

    @Override
    public final IRunningService handleIncoming(IDocument document)
            throws GeneralDOAException {
        IChannelLogic logic;
        try {
            logic = (IChannelLogic) getDoa().getRunning(this);
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
            IChannelLogic logic = (IChannelLogic) getDoa().getRunning(this);
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

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
package pl.doa.artifact.tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.entity.IEntity;
import pl.doa.entity.event.EntityEventType;
import pl.doa.entity.event.IEntityEventListener;
import pl.doa.entity.event.IEntityEventReceiver;
import pl.doa.templates.tags.Tag;

public class EventListenerTag extends EntityTag {

	private final static Logger log = LoggerFactory
			.getLogger(EventListenerTag.class);

	private IEntity sourceEntity;

	private EntityEventType eventType;

	@Override
	public IEntity createEntity() throws GeneralDOAException {
		if (sourceEntity == null) {
			throw new GeneralDOAException("Event source entity is not set!");
		}
		Tag parent = getParent();
		if (!(parent instanceof EntityTag)) {
			throw new GeneralDOAException("Unable to use eventListener here!");
		}
		EntityTag parentTag = (EntityTag) parent;
		IEntity parentEntity = parentTag.entity;
		if (!(parentEntity instanceof IEntityEventReceiver)) {
			throw new GeneralDOAException(
					"Entity type [{0}] does not support event receiveing!",
					sourceEntity.getClass().getName());
		}
		IEntityEventReceiver receiver = (IEntityEventReceiver) parentEntity;
		IEntityEventListener listener = null;
		try {
			listener =
					getDoa().createEntityEventListener(sourceEntity, receiver,
							eventType);
		} catch (Exception e) {
			log.error("", e);
			return null;
		}
		return listener;
	}

	public IEntity getSourceEntity() {
		return sourceEntity;
	}

	public void setSourceEntity(IEntity sourceEntity) {
		this.sourceEntity = sourceEntity;
	}

	public EntityEventType getEventType() {
		return eventType;
	}

	public void setEventType(EntityEventType eventType) {
		this.eventType = eventType;
	}

}

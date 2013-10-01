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
package pl.doa.renderer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.entity.IEntity;
import pl.doa.entity.startable.IStartableEntity;
import pl.doa.resource.IStaticResource;

public abstract class AbstractRendererLogic implements IRendererLogic,
        Serializable {

    protected IRenderer renderer;

    protected IDOA doa;

    @Override
    public IStaticResource renderEntity(IEntity entity)
            throws GeneralDOAException {
        return renderEntity(entity, (IRenderingContext) null);
    }

    @Override
    public IStaticResource renderEntity(IEntity entity,
                                        IRenderingContext renderingContext) throws GeneralDOAException {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        long rendererdSize = renderEntity(entity, byteStream);

        IStaticResource newResource = doa.createStaticResource(renderer
                .getMimetype());
        newResource.setContentFromStream(
                new ByteArrayInputStream(byteStream.toByteArray()),
                rendererdSize);
        return newResource;
    }

    @Override
    public long renderEntity(IEntity entity, final OutputStream output)
            throws GeneralDOAException {
        return renderEntity(entity, output, null);
    }

    @Override
    public final void setDoa(IDOA doa) {
        this.doa = doa;
    }

    @Override
    public final void setStartableEntity(IStartableEntity startableEntity) {
        this.renderer = (IRenderer) startableEntity;
    }
}

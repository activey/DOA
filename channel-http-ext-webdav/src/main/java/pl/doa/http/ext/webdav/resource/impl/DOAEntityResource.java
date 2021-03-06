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
package pl.doa.http.ext.webdav.resource.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.http.ext.webdav.resource.builder.ResourceBuilderFactory;

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.CopyableResource;
import com.bradmcevoy.http.CustomProperty;
import com.bradmcevoy.http.CustomPropertyResource;
import com.bradmcevoy.http.DeletableResource;
import com.bradmcevoy.http.GetableResource;
import com.bradmcevoy.http.MoveableResource;
import com.bradmcevoy.http.Range;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.Request.Method;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;

/**
 * @author activey
 */
public class DOAEntityResource<T extends IEntity> implements GetableResource,
        CustomPropertyResource, MoveableResource, CopyableResource, DeletableResource {

    private static final String NAMESPACE = "http://doaplatform.net/";
    protected final T entity;
    protected final ResourceBuilderFactory factory;

    public DOAEntityResource(ResourceBuilderFactory factory, T entity) {
        this.factory = factory;
        this.entity = entity;
    }

    /*
     * (non-Javadoc)
     * @see com.bradmcevoy.http.Resource#getUniqueId()
     */
    @Override
    public String getUniqueId() {
        return entity.getId() + "";
    }

    /*
     * (non-Javadoc)
     * @see com.bradmcevoy.http.Resource#getName()
     */
    @Override
    public String getName() {
        return entity.getName();
    }

    /*
     * (non-Javadoc)
     * @see com.bradmcevoy.http.Resource#authenticate(java.lang.String,
     * java.lang.String)
     */
    @Override
    public Object authenticate(String user, String password) {
        return "doa.test";
    }

    /*
     * (non-Javadoc)
     * @see com.bradmcevoy.http.Resource#authorise(com.bradmcevoy.http.Request,
     * com.bradmcevoy.http.Request.Method, com.bradmcevoy.http.Auth)
     */
    @Override
    public boolean authorise(Request request, Method method, Auth auth) {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.bradmcevoy.http.Resource#getRealm()
     */
    @Override
    public String getRealm() {
        return "doa.test";
    }

    /*
     * (non-Javadoc)
     * @see com.bradmcevoy.http.Resource#getModifiedDate()
     */
    @Override
    public Date getModifiedDate() {
        return entity.getLastModified();
    }

    /*
     * (non-Javadoc)
     * @see
     * com.bradmcevoy.http.Resource#checkRedirect(com.bradmcevoy.http.Request)
     */
    @Override
    public String checkRedirect(Request request) {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.bradmcevoy.http.GetableResource#sendContent(java.io.OutputStream,
     * com.bradmcevoy.http.Range, java.util.Map, java.lang.String)
     */
    @Override
    public void sendContent(OutputStream out, Range range,
                            Map<String, String> params, String contentType) throws IOException,
            NotAuthorizedException, BadRequestException {

    }

    /*
     * (non-Javadoc)
     * @see
     * com.bradmcevoy.http.GetableResource#getMaxAgeSeconds(com.bradmcevoy.http
     * .Auth)
     */
    @Override
    public Long getMaxAgeSeconds(Auth auth) {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.bradmcevoy.http.GetableResource#getContentType(java.lang.String)
     */
    @Override
    public String getContentType(String accepts) {
        return "application/octet-stream";
    }

    /*
     * (non-Javadoc)
     * @see com.bradmcevoy.http.GetableResource#getContentLength()
     */
    @Override
    public Long getContentLength() {
        return null;
    }

    @Override
    public final Date getCreateDate() {
        return entity.getCreated();
    }

    @Override
    public Set<String> getAllPropertyNames() {
        return new TreeSet<String>(entity.getAttributeNames());
    }

    @Override
    public CustomProperty getProperty(String name) {
        final String propertyValue = entity.getAttribute(name);
        CustomProperty property = new CustomProperty() {

            @Override
            public void setFormattedValue(String s) {

            }

            @Override
            public Class getValueClass() {
                return String.class;
            }

            @Override
            public Object getTypedValue() {
                return propertyValue;
            }

            @Override
            public String getFormattedValue() {
                return propertyValue;
            }
        };
        return property;
    }

    @Override
    public String getNameSpaceURI() {
        return NAMESPACE;
    }

    public IEntity getEntity() {
        return entity;
    }

    @Override
    public void copyTo(CollectionResource toCollection, String name) {
        /*DOAEntitiesContainerResource containerRes =
                (DOAEntitiesContainerResource) toCollection;
		DOAEntitiesContainer destCont =
				(DOAEntitiesContainer) containerRes.entity;
		entity.setContainer(destCont);*/
    }

    @Override
    public void moveTo(CollectionResource rDest, String name)
            throws ConflictException {
        entity.setName(name);
        DOAEntitiesContainerResource containerRes =
                (DOAEntitiesContainerResource) rDest;
        IEntitiesContainer destCont =
                (IEntitiesContainer) containerRes.entity;
        if (destCont.equals(entity.getContainer())) {
            return;
        }
        try {
            entity.setContainer(destCont);
        } catch (GeneralDOAException e) {
            throw new ConflictException(rDest);
        }
    }

    @Override
    public void delete() throws NotAuthorizedException, ConflictException,
            BadRequestException {
        entity.remove();
    }


}

package pl.doa.resource.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.DetachedEntity;
import pl.doa.entity.IEntity;
import pl.doa.resource.IStaticResource;
import pl.doa.utils.DataUtils;

public class DetachedStaticResource extends DetachedEntity implements
        IStaticResource {

    private String mimetype;
    private byte[] bytes;

    public DetachedStaticResource(IDOA doa, String name, String mimeType) {
        super(doa);
        this.mimetype = mimeType;
        if (name != null) {
            setName(name);
        }
    }

    public DetachedStaticResource(IDOA doa, String mimeType) {
        this(doa, null, mimeType);
    }

    @Override
    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    @Override
    public String getMimetype() {
        return this.mimetype;
    }

    @Override
    public long getContentSize() {
        return (this.bytes == null) ? 0 : this.bytes.length;
    }

    @Override
    public byte[] getContent() throws GeneralDOAException {
        return this.bytes;
    }

    @Override
    public InputStream getContentStream() throws GeneralDOAException {
        return (this.bytes == null) ? null : new ByteArrayInputStream(bytes);
    }

    @Override
    public void setContentFromStream(InputStream contentStream, Long contentSize)
            throws GeneralDOAException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            DataUtils.copyStream(contentStream, output, 512);
            this.bytes = output.toByteArray();
        } catch (Exception e) {
            throw new GeneralDOAException(e);
        }
    }

    @Override
    public void setContentFromBytes(byte[] bytes) throws GeneralDOAException {
        this.bytes = bytes;
    }

    @Override
    protected IEntity buildAttached(IEntitiesContainer container)
            throws GeneralDOAException {
        IStaticResource stored =
                doa.createStaticResource(getName(), this.mimetype, container);
        if (bytes != null) {
            stored.setContentFromBytes(bytes);
        }
        return stored;
    }

}

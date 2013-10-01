package pl.doa.document.field.impl;

import java.io.Serializable;

import pl.doa.document.field.IDocumentFieldValue;

public abstract class BaseAbstractFieldValue implements IDocumentFieldValue,
        Serializable {

    private static final long serialVersionUID = -1040084670895972550L;

    public final String toString() {
        return getFieldValueAsString();
    }

    @Override
    public void remove() {
    }
}

/**
 *
 */
package pl.doa.document.field.impl.detached;

import java.io.Serializable;
import java.util.List;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.document.field.IDocumentFieldValue;

/**
 * @author activey
 */
public class DetachedFieldType implements IDocumentFieldType, Serializable {

    private static final long serialVersionUID = 4639427683471042054L;
    private DocumentFieldDataType dataType;
    private boolean required;
    private boolean authorizable;

    public DetachedFieldType(IDocumentDefinition definition,
                             DocumentFieldDataType dataType, String fieldName) {
        this.dataType = dataType;
        if (definition == null) {
            return;
        }
        IDocumentFieldType fieldType = definition.getFieldType(fieldName);
        if (fieldType == null) {
            return;
        }
        this.required = fieldType.isRequired();
        this.authorizable = fieldType.isAuthorizable();
    }

    /* (non-Javadoc)
     * @see pl.doa.document.field.IDocumentFieldType#getName()
     */
    @Override
    public String getName() {
        return null;
    }

    /* (non-Javadoc)
     * @see pl.doa.document.field.IDocumentFieldType#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
    }

    /* (non-Javadoc)
     * @see pl.doa.document.field.IDocumentFieldType#getFieldDataType()
     */
    @Override
    public DocumentFieldDataType getFieldDataType() {
        return this.dataType;
    }

    /* (non-Javadoc)
     * @see pl.doa.document.field.IDocumentFieldType#setFieldDataType(pl.doa.document.field.DocumentFieldDataType)
     */
    @Override
    public void setFieldDataType(DocumentFieldDataType fieldDataType) {
    }

    /* (non-Javadoc)
     * @see pl.doa.document.field.IDocumentFieldType#isRequired()
     */
    @Override
    public boolean isRequired() {
        return this.required;
    }

    /* (non-Javadoc)
     * @see pl.doa.document.field.IDocumentFieldType#setRequired(boolean)
     */
    @Override
    public void setRequired(boolean required) {
    }

    /* (non-Javadoc)
     * @see pl.doa.document.field.IDocumentFieldType#isAuthorizable()
     */
    @Override
    public boolean isAuthorizable() {
        return this.authorizable;
    }

    /* (non-Javadoc)
     * @see pl.doa.document.field.IDocumentFieldType#setAuthorizable(boolean)
     */
    @Override
    public void setAuthorizable(boolean authorizable) {
    }

    /* (non-Javadoc)
     * @see pl.doa.document.field.IDocumentFieldType#createValueInstance(java.lang.String)
     */
    @Override
    public IDocumentFieldValue createValueInstance(String fieldName)
            throws GeneralDOAException {
        return null;
    }

    /* (non-Javadoc)
     * @see pl.doa.document.field.IDocumentFieldType#createValueInstance()
     */
    @Override
    public IDocumentFieldValue createValueInstance() throws GeneralDOAException {
        return null;
    }

    /* (non-Javadoc)
     * @see pl.doa.document.field.IDocumentFieldType#getAttribute(java.lang.String)
     */
    @Override
    public Object getAttribute(String attributeName) {
        return null;
    }

    /* (non-Javadoc)
     * @see pl.doa.document.field.IDocumentFieldType#getAttribute(java.lang.String, java.lang.Object)
     */
    @Override
    public Object getAttribute(String attributeName, Object defaultValue) {
        return null;
    }

    /* (non-Javadoc)
     * @see pl.doa.document.field.IDocumentFieldType#getAttributeNames()
     */
    @Override
    public List<String> getAttributeNames() {
        return null;
    }

    /* (non-Javadoc)
     * @see pl.doa.document.field.IDocumentFieldType#setAttribute(java.lang.String, java.lang.Object)
     */
    @Override
    public void setAttribute(String attributeName, Object attributeValue) {
    }

    /* (non-Javadoc)
     * @see pl.doa.document.field.IDocumentFieldType#getDocumentDefinition()
     */
    @Override
    public IDocumentDefinition getDocumentDefinition() {
        return null;
    }

    @Override
    public void remove() {
    }

    @Override
    public void remove(boolean forceRemove) {
    }

}

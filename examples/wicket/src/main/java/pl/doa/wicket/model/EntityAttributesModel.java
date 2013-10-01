package pl.doa.wicket.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import pl.doa.IDOA;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityAttribute;
import pl.doa.impl.EntityLocationIterator;
import pl.doa.utils.PathIterator;
import pl.doa.wicket.WicketDOAApplication;

public class EntityAttributesModel extends
        AbstractReadOnlyModel<List<IEntityAttribute>> {

    private IModel<IEntity> entityModel;
    private PathIterator<String> entityLocation;
    private long containerId = 0;

    public EntityAttributesModel(long containerId) {
        this.containerId = containerId;
    }

    public EntityAttributesModel(IModel<IEntity> containerModel) {
        this.entityModel = containerModel;
    }

    public EntityAttributesModel(String entityLocation) {
        this(new EntityLocationIterator(entityLocation));
    }

    public EntityAttributesModel(PathIterator<String> containerLocation) {
        this.entityLocation = containerLocation;
    }

    @Override
    public void detach() {
    }

    @Override
    public List<IEntityAttribute> getObject() {
        IEntity entity = null;
        if (this.entityModel != null) {
            IEntity modelValue = this.entityModel.getObject();
            if (modelValue != null) {
                entity = modelValue;
            }
        }
        IDOA doa = WicketDOAApplication.get().getDoa();
        if (entity == null && containerId != 0) {
            entity = (IEntity) doa.lookupByUUID(containerId);
        }
        if (entity == null) {
            entity = (IEntity) doa.lookupEntityByLocation(entityLocation);
        }
        if (entity == null) {
            return null;
        }
        List<IEntityAttribute> attributes = new ArrayList<IEntityAttribute>();
        Collection<String> attributeNames = entity.getAttributeNames();
        for (String attributeName : attributeNames) {
            attributes.add(entity.getAttributeObject(attributeName));
        }
        return attributes;
    }

}

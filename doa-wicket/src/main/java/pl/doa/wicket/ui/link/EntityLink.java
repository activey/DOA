/**
 *
 */
package pl.doa.wicket.ui.link;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.doa.entity.IEntity;
import pl.doa.wicket.model.EntityModel;
import pl.doa.wicket.ui.page.EntityPage;

/**
 * @author activey
 */
public class EntityLink<T extends IEntity> extends BookmarkablePageLink<T> {

    private static final long serialVersionUID = 1L;

    public EntityLink(String id, T entity,
                      Class<? extends EntityPage<T>> pageClass,
                      final IEntityLinkLabel<T> label) {
        this(id, new EntityModel<T>(entity), pageClass, label);
    }

    public EntityLink(String id, T entity,
                      Class<? extends EntityPage<T>> pageClass) {
        this(id, new EntityModel<T>(entity), pageClass);
    }

    public EntityLink(String id, Class<? extends EntityPage<T>> pageClass) {
        this(id, new EntityModel<T>((T) null), pageClass);
    }

    public EntityLink(String id, Class<? extends EntityPage<T>> pageClass,
                      final IEntityLinkLabel<T> label) {
        this(id, new EntityModel<T>((T) null), pageClass, label);
    }

    public EntityLink(String id, final IModel<T> entityModel,
                      Class<? extends EntityPage<T>> pageClass) {
        this(id, entityModel, pageClass, null);
    }

    public EntityLink(String id, final IModel<T> entityModel,
                      Class<? extends EntityPage<T>> pageClass,
                      final IEntityLinkLabel<T> label) {
        super(id, pageClass);
        if (!isEnabled()) {
            return;
        }
        final T entity = entityModel.getObject();
        if (entity == null) {
            return;
        }
        setModel(entityModel);
        if (label != null) {
            setBody(new AbstractReadOnlyModel() {

                @Override
                public String getObject() {
                    return label.generateLinkLabel(entity);
                }

            });
        }
    }

    protected IEntity overrideEntity() {
        return getModelObject();
    }

    @Override
    public PageParameters getPageParameters() {
        IEntity entity = overrideEntity();
        PageParameters parameters = new PageParameters();
        if (entity == null) {
            return parameters;
        }
        parameters.add("location", entity.getLocation());
        parameters.add("doa", entity.getDoa().getId());
        return parameters;
    }

    public final boolean isIdenticDestination() {
        Object pageModel = getPage().getDefaultModelObject();
        if (!(pageModel instanceof IEntity)) {
            return false;
        }
        IEntity currentEntity = (IEntity) pageModel;
        if (getModel() == null) {
            Class<? extends Page> linkPageClass = this.getPageClass();
            Class<? extends Page> thisPageClass = getPage().getClass();

            return thisPageClass.equals(linkPageClass);
        }
        IEntity linkEntity = getModelObject();
        return linkEntity.equals(currentEntity);
    }
}

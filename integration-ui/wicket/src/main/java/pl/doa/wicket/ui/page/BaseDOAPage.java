/**
 *
 */
package pl.doa.wicket.ui.page;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.doa.IDOA;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.wicket.WicketDOAApplication;

/**
 * @author activey
 */
public class BaseDOAPage<T> extends WebPage {

    public BaseDOAPage() {
        super();
    }

    public BaseDOAPage(IModel<T> model) {
        super(model);
    }

    public BaseDOAPage(PageParameters parameters) {
        super(parameters);
    }

    protected IDOA getDoa() {
        return WicketDOAApplication.get().getDoa();
    }

    protected IEntitiesContainer getApplicationContainer() {
        return WicketDOAApplication.get().getApplicationContainer();
    }

    public final IModel<T> getModel() {
        return (IModel<T>) getDefaultModel();
    }

    public final T getModelObject() {
        return (T) getDefaultModelObject();
    }

    public final <T extends IEntity> Component decorateEntity(T entity,
                                                              String componentId) throws Exception {
        return WicketDOAApplication.get().decorateEntity(entity, componentId);
    }

    public final <T extends IEntity> Component decorateEntity(IModel<T> entityModel,
                                                              String componentId) throws Exception {
        return WicketDOAApplication.get().decorateEntity(entityModel,
                componentId);
    }
}

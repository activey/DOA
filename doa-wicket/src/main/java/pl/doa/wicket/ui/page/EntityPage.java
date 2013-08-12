/**
 *
 */
package pl.doa.wicket.ui.page;

import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.IDOA;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.impl.EntityLocationIterator;
import pl.doa.utils.PathIterator;
import pl.doa.wicket.WicketDOAApplication;
import pl.doa.wicket.model.EntityModel;

/**
 * @author activey
 */
public class EntityPage<T extends IEntity> extends BaseDOAPage<T> {

    private final static Logger log = LoggerFactory.getLogger(EntityPage.class);

    private static final String DEFAULT_ENTITY_LOCATION = "/";

    public EntityPage() {
        IEntitiesContainer appContainer = getApplicationContainer();
        if (appContainer == null) {
            return;
        }
        IModel<T> entityModel = getDefaultEntityModel();
        if (entityModel == null) {
            return;
        }
        setEntity(entityModel);
    }

    public EntityPage(T entity) {
        this(new PageParameters().add("location", entity.getLocation()));
    }

    public EntityPage(String documentLocation) {
        this(new EntityLocationIterator(documentLocation, true));
    }

    public EntityPage(PathIterator<String> documentPath) {
        setEntity(documentPath);
    }

    public EntityPage(IModel<T> model) {
        this(model.getObject());
    }

    public EntityPage(PageParameters parameters) {
        super(parameters);
        StringValue parameter = parameters.get("location");
        if (parameter == null || parameter.isEmpty()) {
            setEntity(getDefaultEntityModel());
            return;
        }
        String location = parameter.toString();
        IDOA doa = WicketDOAApplication.get().getDoa();
        T entity = (T) doa.lookupEntityByLocation(location);
        setEntity(entity);
    }

    protected IModel<T> getDefaultEntityModel() {
        return new EntityModel<T>(DEFAULT_ENTITY_LOCATION);
    }

    private void setEntity(PathIterator<String> entityPath) {
        IEntitiesContainer appContainer = getApplicationContainer();
        if (appContainer == null) {
            return;
        }
        T entity = (T) appContainer.lookupEntityByLocation(entityPath);
        setEntity(entity);
    }

    protected void setEntity(T entity) {
        IModel<T> entityModel = null;
        if (entity == null) {
            error("Unable to find entity at given location.");
            log.error("Unable to find entity at given location. Using default entity model.");
            entityModel = getDefaultEntityModel();
        } else {
            entityModel = new EntityModel<T>(entity);
        }
        setEntity(entityModel);
    }

    protected final void setEntity(IModel<T> entityModel) {
        try {
            setDefaultModel(entityModel);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    protected final void onInitialize() {
        super.onInitialize();
        try {
            initEntityPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void initEntityPage() throws Exception {
    }
}

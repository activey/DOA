/**
 *
 */
package pl.doa.wicket.ui.link;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes.Method;
import org.apache.wicket.ajax.attributes.IAjaxCallListener;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.entity.IEntity;
import pl.doa.entity.ITransactionCallback;
import pl.doa.wicket.WicketDOAApplication;

/**
 * @author activey
 */
public abstract class EntityAjaxLink<T extends IEntity> extends AjaxLink<T> {

    private final boolean transactional;

    private final static Logger log = LoggerFactory
            .getLogger(EntityAjaxLink.class);

    public EntityAjaxLink(String id, IModel<T> model) {
        this(id, model, false);
    }

    public EntityAjaxLink(String id, IModel<T> model, IModel<String> labelModel) {
        this(id, model, labelModel, false);
    }

    public EntityAjaxLink(String id, IModel<T> model, boolean transactional) {
        this(id, model, null, transactional);
    }

    public EntityAjaxLink(String id, IModel<T> model,
                          IModel<String> labelModel, boolean transactional) {
        super(id, model);
        setBody(labelModel);
        this.transactional = transactional;
    }

    @Override
    protected boolean getStatelessHint() {
        return getPage().getStatelessHint();
    }

    @Override
    public final void onClick(final AjaxRequestTarget target) {
        if (transactional) {
            WicketDOAApplication.get().getDoa()
                    .doInTransaction(new ITransactionCallback<Void>() {

                        @Override
                        public Void performOperation() throws Exception {
                            onClick(getModel(), target);
                            return null;
                        }
                    });
        } else {
            try {
                onClick(getModel(), target);
            } catch (GeneralDOAException e) {
                log.error("", e);
            }
        }
    }

    protected void onClick(IModel<T> entityModel, AjaxRequestTarget target)
            throws GeneralDOAException {
    }

    protected void populateListeners(List<IAjaxCallListener> listeners) {

    }

    @Override
    protected final void updateAjaxAttributes(AjaxRequestAttributes attributes) {
        attributes.setMethod(Method.POST);
        populateListeners(attributes.getAjaxCallListeners());
    }
}

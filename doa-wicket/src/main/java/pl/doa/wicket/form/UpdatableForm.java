/**
 *
 */
package pl.doa.wicket.form;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author activey
 */
public abstract class UpdatableForm<T> extends Form<T> {

    private final static Logger log = LoggerFactory
            .getLogger(UpdatableForm.class);

    public UpdatableForm(String id, IModel<T> model) {
        super(id, model);
    }

    public UpdatableForm(String id) {
        super(id);
    }

    public final void updateFormModel() {
        FormComponent.visitFormComponentsPostOrder(this,
                new IVisitor<FormComponent<?>, Void>() {

                    @Override
                    public void component(FormComponent<?> object,
                                          IVisit<Void> visit) {
                        object.validate();
                    }

                });
    }

    @Override
    protected final void onInitialize() {
        super.onInitialize();

        try {
            initForm();
        } catch (Exception e) {
            log.error("", e);
        }
    }

    protected abstract void initForm() throws Exception;

}

package pl.doa.wrapper.wicket.ui.link;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.service.IRunningService;
import pl.doa.wicket.form.CallServiceForm;
import pl.doa.wicket.ui.link.FormCallServiceLink;
import pl.doa.wrapper.wicket.service.WrappedCallServiceForm;

/**
 * User: activey
 * Date: 08.08.13
 * Time: 20:19
 */
public class WrappedCallServiceLink extends FormCallServiceLink {

    public WrappedCallServiceLink(String id, WrappedCallServiceForm serviceForm) {
        super(id, serviceForm);
    }

    public WrappedCallServiceLink(String id) {
        super(id);
    }


    @Override
    protected final void onAfterRun(IModel<IRunningService> runningService, AjaxRequestTarget target) throws GeneralDOAException {
        IRunningService service = runningService.getObject();
        IDocument output = service.getOutput();

        WrappedPossibleOutput possibleOutput = new WrappedPossibleOutput(output);
        onAfterRun(possibleOutput);
    }

    protected void onAfterRun(WrappedPossibleOutput possibleOutput) {

    }
}

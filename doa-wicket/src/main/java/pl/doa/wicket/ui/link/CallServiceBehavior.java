package pl.doa.wicket.ui.link;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.entity.ITransactionCallback;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.wicket.WicketDOAApplication;
import pl.doa.wicket.model.service.ServiceInputModel;

public class CallServiceBehavior extends AjaxFormSubmitBehavior {

	private final static Logger log = LoggerFactory
			.getLogger(CallServiceBehavior.class);

	private final boolean asynchronous;
	private IModel<IDocument> inputModel;

	private final IModel<IServiceDefinition> serviceModel;

	public CallServiceBehavior(String event,
			IModel<IServiceDefinition> serviceModel, boolean asynchronous) {
		this(event, serviceModel, new ServiceInputModel(serviceModel),
				asynchronous);
	}

	public CallServiceBehavior(String event,
			IModel<IServiceDefinition> serviceModel,
			IModel<IDocument> inputModel, boolean asynchronous) {
		super(event);
		this.serviceModel = serviceModel;
		this.inputModel = inputModel;
		this.asynchronous = asynchronous;
	}

	@Override
	protected final void onSubmit(AjaxRequestTarget target) {
		final IServiceDefinition service = serviceModel.getObject();
		if (this.inputModel == null) {
			this.inputModel = new ServiceInputModel(service);
		}
		final IDocument input = inputModel.getObject();

		try {
			onBefeforeRun(service, input, target);

			IRunningService running = null;
			if (isTransactional()) {
				running =
						WicketDOAApplication
								.get()
								.getDoa()
								.doInTransaction(
										new ITransactionCallback<IRunningService>() {

											@Override
											public IRunningService performOperation()
													throws Exception {
												return service.executeService(
														input,
														WicketDOAApplication
																.getAgent(),
														asynchronous);
											}
										});
			} else {
				running =
						service.executeService(input,
								WicketDOAApplication.getAgent(), asynchronous);
			}
			onAfterRun(running, input, target);
		} catch (Exception e) {
			log.error("", e);
		}

	}

	protected boolean isTransactional() {
		return false;
	}

	@Override
	protected void onError(AjaxRequestTarget target) {

	}

	protected void onBefeforeRun(IServiceDefinition service, IDocument input,
			AjaxRequestTarget target) throws GeneralDOAException {
	}

	protected void onAfterRun(IRunningService runningService, IDocument input,
			AjaxRequestTarget target) throws GeneralDOAException {
	}

}

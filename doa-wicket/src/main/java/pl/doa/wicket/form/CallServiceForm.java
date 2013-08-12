/**
 * 
 */
package pl.doa.wicket.form;

import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.agent.IAgent;
import pl.doa.document.IDocument;
import pl.doa.document.impl.DetachedDocument;
import pl.doa.entity.ITransactionCallback;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.wicket.WicketDOAApplication;
import pl.doa.wicket.model.EntityModel;
import pl.doa.wicket.model.service.ServiceInputModel;
import pl.doa.wicket.ui.link.FormCallServiceLink;

/**
 * @author activey
 * 
 */
public abstract class CallServiceForm<T extends IDocument> extends EntityForm<T> {

	private final static Logger log = LoggerFactory
			.getLogger(CallServiceForm.class);

	protected final IModel<IServiceDefinition> serviceModel;

	protected final IModel<T> inputModel;

	protected IModel<IRunningService> runningServiceModel;

	public CallServiceForm(String id, String serviceLocation) {
		this(id, new EntityModel<IServiceDefinition>(serviceLocation),
				new ServiceInputModel<T>(serviceLocation));
	}

	public CallServiceForm(String id, IModel<IServiceDefinition> serviceModel) {
		this(id, serviceModel, new ServiceInputModel<T>(serviceModel));
	}

	public CallServiceForm(String id, IModel<IServiceDefinition> serviceModel,
			IModel<T> inputModel) {
		super(id, inputModel);
		this.serviceModel = serviceModel;
		this.inputModel = inputModel;
	}

	@Override
	protected final void onSubmit() {
		if (!isEnabled()) {
			return;
		}
		final IServiceDefinition service = serviceModel.getObject();
		final T input = inputModel.getObject();
		try {
			IRunningService running = null;
			final IAgent agent = WicketDOAApplication.getAgent(input);
			if (isTransactional()) {
				running = WicketDOAApplication
						.get()
						.getDoa()
						.doInTransaction(
								new ITransactionCallback<IRunningService>() {

									@Override
									public IRunningService performOperation()
											throws Exception {

										return service.executeService(input,
												agent, isAsynchronous());
									}
								});
			} else {
				running = service
						.executeService(input, agent, isAsynchronous());
			}
			this.runningServiceModel = new EntityModel<IRunningService>(running);

			onServiceExecuted();
		} catch (Exception e) {
			log.error("", e);
			this.error(e.getMessage());
		}
	}

	protected void onServiceExecuted() {

	}

	protected boolean isTransactional() {
		return false;
	}

	protected boolean isAsynchronous() {
		return false;
	}

	public final IModel<IServiceDefinition> getServiceModel() {
		return serviceModel;
	}

	public final IModel<T> getInputModel() {
		return inputModel;
	}

	public final IModel<IRunningService> getRunningServiceModel() {
		return runningServiceModel;
	}

	public final FormCallServiceLink crateCallLink(String linkId) {
		return new FormCallServiceLink(linkId, this);
	}

	public final void clearServiceInput() {
		T input = inputModel.getObject();
		if (input == null) {
			return;
		}
		if (input instanceof DetachedDocument) {
			DetachedDocument detached = (DetachedDocument) input;
			detached.detach();
		}
	}
}

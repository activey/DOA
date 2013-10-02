/**
 * 
 */
package pl.doa.wicket.ui.link;

import org.apache.wicket.ajax.AjaxRequestTarget;
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

/**
 * @author activey
 * 
 */
public class ManualCallServiceLink extends EntityAjaxLink<IServiceDefinition> {

	private final static Logger log = LoggerFactory
			.getLogger(ManualCallServiceLink.class);

	private IModel<IDocument> inputModel;

	private final IModel<IServiceDefinition> serviceModel;

	public ManualCallServiceLink(String id,
			IModel<IServiceDefinition> serviceModel) {
		this(id, serviceModel, new ServiceInputModel(serviceModel));
	}

	public ManualCallServiceLink(String id,
			IModel<IServiceDefinition> serviceModel,
			IModel<IDocument> inputModel) {
		super(id, null);
		this.serviceModel = serviceModel;
		this.inputModel = inputModel;
	}

	protected boolean isTransactional() {
		return false;
	}

	protected boolean isAsynchronous() {
		return false;
	}

	@Override
	protected final void onClick(IModel<IServiceDefinition> entityModel,
			AjaxRequestTarget target) {

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
														isAsynchronous());
											}
										});
			} else {
				running =
						service.executeService(input,
								WicketDOAApplication.getAgent(),
								isAsynchronous());
			}
			onAfterRun(running, input, target);
		} catch (Exception e) {
			log.error("", e);
			this.error(e.getMessage());
		}
	}

	protected void onBefeforeRun(IServiceDefinition service, IDocument input,
			AjaxRequestTarget target) throws GeneralDOAException {
	}

	protected void onAfterRun(IRunningService runningService, IDocument input,
			AjaxRequestTarget target) throws GeneralDOAException {
	}
}

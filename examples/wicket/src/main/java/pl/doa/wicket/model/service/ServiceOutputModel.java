package pl.doa.wicket.model.service;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.IDOA;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.entity.ITransactionCallback;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.wicket.WicketDOAApplication;
import pl.doa.wicket.model.document.DocumentModel;

public class ServiceOutputModel extends AbstractReadOnlyModel<IDocument> {

	private final static Logger log = LoggerFactory
			.getLogger(ServiceOutputModel.class);

	private final IModel<IDocument> input;
	private final String serviceDefinitionLocation;

	public ServiceOutputModel(String serviceDefinitionLocation, IDocument input) {
		this(serviceDefinitionLocation, new DocumentModel(input));
	}

	public ServiceOutputModel(String serviceDefinitionLocation,
			IModel<IDocument> documentModel) {
		this.input = documentModel;
		this.serviceDefinitionLocation = serviceDefinitionLocation;
	}

	public ServiceOutputModel(String serviceDefinitionLocation) {
		this(serviceDefinitionLocation, (IDocument) null);
	}

	@Override
	public void detach() {
	}

	@Override
	public IDocument getObject() {
		IEntitiesContainer appContainer =
				WicketDOAApplication.get().getApplicationContainer();
		final IServiceDefinition service =
				(IServiceDefinition) appContainer
						.lookupEntityByLocation(serviceDefinitionLocation);
		IDocument output = null;

		IDOA doa = WicketDOAApplication.get().getDoa();
		output = doa.doInTransaction(new ITransactionCallback<IDocument>() {

			@Override
			public IDocument performOperation() throws Exception {
				IRunningService running =
						service.executeService(input.getObject(), false);
				return running.getOutput();
			}
		});
		return output;
	}
}

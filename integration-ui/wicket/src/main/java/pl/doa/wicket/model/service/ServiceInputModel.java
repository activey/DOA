/**
 * 
 */
package pl.doa.wicket.model.service;

import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.service.IServiceDefinition;
import pl.doa.wicket.model.EntityModel;
import pl.doa.wicket.model.document.DocumentModel;

/**
 * @author activey
 * 
 */
public class ServiceInputModel<T extends IDocument> extends DocumentModel<T> {

	private final static Logger log = LoggerFactory
			.getLogger(ServiceInputModel.class);

	private IModel<T> inputModel;

	public ServiceInputModel(IModel<IServiceDefinition> serviceModel) {
		super(-1);
		IDocumentDefinition inputDefinition =
				serviceModel.getObject().getInputDefinition();
		if (inputDefinition == null) {
			return;
		}
		IDocument input;
		try {
			input = inputDefinition.createDocumentInstance();
		} catch (GeneralDOAException e) {
			log.error("", e);
			return;
		}
		this.inputModel = new DocumentModel(input);
	}

	public ServiceInputModel(IServiceDefinition service) {
		this(new EntityModel<IServiceDefinition>(service));
	}

	public ServiceInputModel(String serviceLocation) {
		this(new EntityModel<IServiceDefinition>(serviceLocation));
	}

	@Override
	public T getEntity() {
		if (inputModel != null) {
			return inputModel.getObject();
		}
		return null;
	}

}

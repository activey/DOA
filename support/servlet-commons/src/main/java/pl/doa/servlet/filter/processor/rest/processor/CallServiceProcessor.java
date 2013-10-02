/**
 *
 */
package pl.doa.servlet.filter.processor.rest.processor;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.agent.IAgent;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.entity.IEntity;
import pl.doa.entity.transaction.CallServiceTransaction;
import pl.doa.resource.IStaticResource;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.servlet.filter.processor.rest.EmptyResponse;
import pl.doa.servlet.filter.processor.rest.RestCallResponse;
import pl.doa.servlet.filter.processor.rest.json.entity.JsonDocumentResponse;

/**
 * @author activey
 */
public class CallServiceProcessor extends BasicRestProcessor {

    private String serviceToCall;

    public CallServiceProcessor(String uriPattern, String modifyPattern,
                                String serviceToCall) {
        super(uriPattern, modifyPattern);
        this.serviceToCall = serviceToCall;
    }

    @Override
    protected final RestCallResponse doGetEntity(IEntity entity,
                                                 IDocument requestDocument, IDocument responseDocument, IDOA doa)
            throws Exception {
        IEntity serviceEntity = doa.lookupEntityByLocation(serviceToCall);
        if (serviceEntity == null
                || !(serviceEntity instanceof IServiceDefinition)) {
            return new EmptyResponse();
        }
        IServiceDefinition serviceDef = (IServiceDefinition) serviceEntity;
        IDocumentDefinition inputDef = serviceDef.getInputDefinition();
        IDocument serviceInput = null;
        if (inputDef != null) {
            serviceInput = inputDef.createDocumentInstance();
            onBeforeCall(serviceInput, entity);
        }
        IAgent agent = (IAgent) responseDocument.getFieldValue("agent");

        IRunningService runningService = doa
                .doInTransaction(new CallServiceTransaction(serviceDef,
                        serviceInput, agent));
        return new JsonDocumentResponse(runningService.getOutput());
    }

    protected void onBeforeCall(IDocument input, IEntity relatedEntity)
            throws GeneralDOAException {

    }

    @Override
    protected final RestCallResponse doGetDocument(IDocument document) {
        return new EmptyResponse();
    }

    @Override
    protected final RestCallResponse doGetContainer(IEntitiesContainer container)
            throws Exception {
        return new EmptyResponse();
    }

    @Override
    protected final RestCallResponse doGetResource(IStaticResource resource)
            throws Exception {
        return new EmptyResponse();
    }

    @Override
    protected final RestCallResponse doDeleteEntity(IEntity entity,
                                                    IDocument requestDocument, IDocument responseDocument, IDOA doa)
            throws Exception {
        return new EmptyResponse();
    }

}

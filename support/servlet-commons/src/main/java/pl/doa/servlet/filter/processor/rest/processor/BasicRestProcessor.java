package pl.doa.servlet.filter.processor.rest.processor;

import java.util.Map;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.entity.IEntity;
import pl.doa.entity.ITransactionCallback;
import pl.doa.entity.transaction.DeleteEntityTransaction;
import pl.doa.resource.IStaticResource;
import pl.doa.servlet.filter.processor.BasicMappingProcessor;
import pl.doa.servlet.filter.processor.rest.EmptyResponse;
import pl.doa.servlet.filter.processor.rest.RestCallResponse;
import pl.doa.servlet.filter.processor.rest.json.entity.JsonContainerResponse;
import pl.doa.servlet.filter.processor.rest.json.entity.JsonDocumentResponse;
import pl.doa.servlet.filter.processor.rest.json.entity.JsonEntityResponse;
import pl.doa.servlet.filter.processor.rest.json.entity.JsonResourceResponse;
import pl.doa.servlet.filter.processor.rest.json.simple.SimpleValueResponse;

public class BasicRestProcessor extends BasicMappingProcessor {

    private enum MethodType {
        GET, POST, PUT, DELETE
    }

    ;

    public BasicRestProcessor(String uriPattern, String modifyPattern) {
        super(uriPattern, modifyPattern);
    }

    @Override
    public final void processRequest(IDOA doa, IDocument requestDocument,
                                     IDocument responseDocument, Map<String, String> uriParams)
            throws Exception {
        String httpMethod = requestDocument.getFieldValueAsString("method");
        MethodType methodType = MethodType.GET;
        try {
            methodType = MethodType.valueOf(httpMethod);
        } catch (IllegalArgumentException e) {
            // not recognized method
        }

        // response from rest call
        IEntity foundEntity = (IEntity) responseDocument
                .getFieldValue("response");
        RestCallResponse response = new EmptyResponse();
        if (foundEntity == null) {
            response.commitResponse(doa, responseDocument);
            return;
        }

        switch (methodType) {
            case GET: {
                response = doGetEntity(foundEntity, requestDocument,
                        responseDocument, doa);
                break;
            }
            case POST: {

                break;
            }

            case PUT: {
                response = doPutEntity(foundEntity, requestDocument, doa);
                break;
            }

            case DELETE: {
                response = doDeleteEntity(foundEntity, requestDocument,
                        responseDocument, doa);
                break;
            }

            default:
                break;
        }
        response.commitResponse(doa, responseDocument);
    }

    protected RestCallResponse doGetEntity(IEntity entity,
                                           IDocument requestDocument, IDocument responseDocument, IDOA doa)
            throws Exception {
        if (entity instanceof IEntitiesContainer) {
            return doGetContainer((IEntitiesContainer) entity);
        } else if (entity instanceof IStaticResource) {
            return doGetResource((IStaticResource) entity);
        } else if (entity instanceof IDocument) {
            return doGetDocument((IDocument) entity);
        }
        return new JsonEntityResponse<IEntity>(entity);
    }

    protected RestCallResponse doGetDocument(IDocument document) {
        return new JsonDocumentResponse(document);
    }

    protected RestCallResponse doGetContainer(IEntitiesContainer container)
            throws Exception {
        return new JsonContainerResponse(container);
    }

    protected RestCallResponse doGetResource(IStaticResource resource)
            throws Exception {
        return new JsonResourceResponse(resource);
        // return new BinaryResourceResponse(resource);
    }

    protected RestCallResponse doDeleteEntity(final IEntity entity,
                                              IDocument requestDocument, IDocument responseDocument, IDOA doa)
            throws Exception {
        return new SimpleValueResponse<Boolean>(
                doa.doInTransaction(new DeleteEntityTransaction(entity)));
    }

    protected RestCallResponse doPutEntity(IEntity entity,
                                           IDocument requestDocument, IDOA doa) throws Exception {
        IStaticResource requestResource = (IStaticResource) requestDocument
                .getFieldValue("body");
        if (requestResource == null) {
            return new EmptyResponse();
        }

        if (entity instanceof IEntitiesContainer) {
            return doPutContainer((IEntitiesContainer) entity, requestResource,
                    doa);
        }
        return new EmptyResponse();
    }

    protected RestCallResponse doPutContainer(
            final IEntitiesContainer container,
            final IStaticResource inputResource, IDOA doa)
            throws GeneralDOAException {
        doa.doInTransaction(new ITransactionCallback<Object>() {

            @Override
            public Void performOperation() throws Exception {
                container.addEntity(inputResource);
                return null;
            }
        });
        return new JsonContainerResponse(container);
    }
}
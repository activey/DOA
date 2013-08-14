package pl.doa.servlet.filter.processor;

import java.util.HashMap;
import java.util.Map;

import pl.doa.IDOA;
import pl.doa.agent.IAgent;
import pl.doa.document.IDocument;
import pl.doa.entity.IEntity;
import pl.doa.servlet.filter.processor.IRequestProcessor;
import pl.doa.servlet.filter.processor.uri.PathUtil;

public class BasicMappingProcessor implements IRequestProcessor {

    private final String uriPattern;
    private final String modifyPattern;

    public BasicMappingProcessor(String uriPattern, String modifyPattern) {
        this.uriPattern = uriPattern;
        this.modifyPattern = modifyPattern;
    }

    public boolean matches(String uri) {
        return PathUtil.matches(uri, uriPattern);
    }

    private String modify(String applicationUri, Map<String, String> parameters) {
        if (parameters == null) {
            parameters = new HashMap<String, String>();
        }
        Map<String, String> uriParams = PathUtil.getPathVariables(uriPattern,
                applicationUri);
        parameters.putAll(uriParams);

        String outputUri = modifyPattern;
        for (String uriParam : parameters.keySet()) {
            outputUri = outputUri.replaceAll("\\{" + uriParam + "\\}",
                    parameters.get(uriParam));
        }
        return outputUri;
    }

    @Override
    public final void processRequest(IDOA doa, IDocument requestDocument,
                                     IDocument responseDocument) throws Exception {
        String applicationUri = requestDocument
                .getFieldValueAsString("applicationUri");
        Map<String, String> uriParams = new HashMap<String, String>();
        IAgent agent = (IAgent) responseDocument.getFieldValue("agent");
        if (agent != null) {
            uriParams
                    .put("agent_container", agent.getContainer().getLocation());
        }
        applicationUri = modify(applicationUri, uriParams);
        IEntity entity = doa.lookupEntityByLocation(applicationUri);
        if (entity == null) {
            IDocument applicationDocument = (IDocument) requestDocument
                    .getFieldValue("applicationDocument");
            entity = applicationDocument.getContainer().lookupEntityByLocation(
                    applicationUri);
        }
        if (entity != null) {
            responseDocument.setFieldValue("response", entity);
            responseDocument.setFieldValue("httpCode", 200);
        } else {
            responseDocument.setFieldValue("httpCode", 404);
        }

        processRequest(doa, requestDocument, responseDocument, uriParams);
    }

    protected void processRequest(IDOA doa, IDocument requestDocument,
                                  IDocument responseDocument, Map<String, String> uriParams)
            throws Exception {

    }

}
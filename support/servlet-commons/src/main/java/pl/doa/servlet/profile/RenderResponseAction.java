/**
 *
 */
package pl.doa.servlet.profile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.renderer.IRenderer;
import pl.doa.renderer.IRenderingContext;
import pl.doa.resource.IStaticResource;
import pl.doa.utils.DataUtils;
import pl.doa.utils.profile.IProfiledAction;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author activey
 */
public class RenderResponseAction implements IProfiledAction<Boolean> {

    private final static Logger log = LoggerFactory
            .getLogger(RenderResponseAction.class);

    private static final String DEFAULT_CONTENT_TYPE = "text/html";

    private final HttpServletResponse httpResponse;
    private IDocument output;
    private final IDocument appDocument;
    private final IDOA doa;

    private IRenderer renderer;

    public RenderResponseAction(IDOA doa, HttpServletResponse httpResponse, IDocument output, IDocument appDocument) {
        this.doa = doa;
        this.httpResponse = httpResponse;
        this.output = output;
        this.appDocument = appDocument;
    }

    private void renderException(final HttpServletResponse response,
            final Throwable throwable) {
        try {
            renderResponse(response, doa.createExceptionDocument(throwable));
        } catch (GeneralDOAException e) {
            log.error("", e);
        }
    }

    private boolean renderResponse(HttpServletResponse response,
            IDocument document) throws GeneralDOAException {
        if (output == null) {
            log.error("Output document is not available!");
            return false;
        }

        if (output.isDefinedBy("/documents/system/exception")) {
            // response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            httpResponse.setStatus(HttpServletResponse.SC_OK);
        } else if (output
                .isDefinedBy("/channels/http/http_response_definition")) {
            Integer httpCode = (Integer) output.getFieldValue("httpCode");
            if (httpCode != null) {
                httpResponse.setStatus(httpCode);
            }
            Iterable<IDocumentFieldValue> headers = (Iterable<IDocumentFieldValue>) output
                    .getFieldValue("headers");
            if (headers != null) {
                for (IDocumentFieldValue header : headers) {
                    httpResponse.addHeader(header.getFieldName(),
                            header.getFieldValueAsString());
                }
            }
            IEntity responseEntity = (IEntity) output.getFieldValue("response");
            if (responseEntity != null) {
                if (responseEntity instanceof IStaticResource) {
                    IStaticResource responseResource = (IStaticResource) responseEntity;
                    // addDateHeaders(request, response, responseEntity);
                    httpResponse.setContentType(responseResource.getMimetype());
                    httpResponse.setContentLength(new Long(responseResource
                            .getContentSize()).intValue());
                    /*
                     * if (!httpResponse.isCommitted()) {
					 * httpResponse.setBufferSize(128); }
					 */
                    try {
                        ServletOutputStream outStream = httpResponse
                                .getOutputStream();
                        DataUtils.copyStream(
                                responseResource.getContentStream(), outStream,
                                128);
                        httpResponse.getOutputStream().flush();
                        httpResponse.flushBuffer();
                        return true;
                    } catch (Exception e) {
                        log.error("", e);
                        return false;
                    }
                } else if (responseEntity instanceof IDocument) {
                    output = (IDocument) responseEntity;
                }
            } else {
                try {
                    httpResponse.getOutputStream().flush();
                    httpResponse.flushBuffer();
                } catch (IOException e) {
                    log.error("", e);
                    return false;
                }
                return true;
            }
        }

        // parametry dla renderera
        Map<String, Object> rendererContextVariables = new HashMap<String, Object>();

        String contentType = DEFAULT_CONTENT_TYPE;

        // szukanie renderera wedlug contentType
        this.renderer = (appDocument == null) ? getRenderer(contentType, doa)
                : getRenderer(contentType, appDocument.getContainer(), doa);
        if (renderer == null) {
            log.error("Unable to find renderer for content type: [{0}]",
                    contentType);
            return false;
        }

        // tworzenie kontekstu renderowania
        IRenderingContext context = renderer.createContext();
        context.setVariables(rendererContextVariables);

        OutputStream outputStream = null;
        try {
            outputStream = httpResponse.getOutputStream();
            httpResponse.setHeader("Content-Type", renderer.getMimetype());
            httpResponse.setHeader("Content-Length", renderer.renderEntity(this.output, outputStream) + "");
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return true;
        } catch (Exception e) {
            log.error("", e);
            // renderExceptionRaw(httpResponse, e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }
        return false;
    }

    @Override
    public Boolean invoke() throws GeneralDOAException {
        return renderResponse(httpResponse, output);
    }

    @Override
    public String getActionData() {
        return (renderer == null) ? "" : renderer.getName();
    }

    @Override
    public String getActionName() {
        return "RenderResponseAction";
    }

    private IRenderer getRenderer(final String contentType,
            IEntitiesContainer... lookupContainers) {
        return (IRenderer) doa.lookupEntityFromLocation("/renderers",
                new IEntityEvaluator() {

                    @Override
                    public boolean isReturnableEntity(IEntity currentEntity) {
                        if (!(currentEntity instanceof IRenderer)) {
                            return false;
                        }
                        IRenderer renderer = (IRenderer) currentEntity;
                        return contentType.equals(renderer.getMimetype());
                    }
                }, true, lookupContainers);
    }

}

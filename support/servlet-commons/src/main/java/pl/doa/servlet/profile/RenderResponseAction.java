/**
 *
 */
package pl.doa.servlet.profile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

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
import pl.doa.renderer.ITemplateFinder;
import pl.doa.resource.IStaticResource;
import pl.doa.servlet.renderer.MultiContainerFinder;
import pl.doa.utils.DataUtils;
import pl.doa.utils.profile.IProfiledAction;

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

    public RenderResponseAction(IDOA doa, HttpServletResponse httpResponse,
                                IDocument output, IDocument appDocument) {
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

        // identyfikator szablonu dla renderera
        long templateResourceId = 0L;

        // parametry dla renderera
        Map<String, Object> rendererContextVariables = new HashMap<String, Object>();

        String contentType = DEFAULT_CONTENT_TYPE;
		/*
		 * Enumeration<String> headerNames = httpRequest.getHeaderNames(); while
		 * (headerNames.hasMoreElements()) { String headerName = (String)
		 * headerNames.nextElement(); if (headerName == null) { continue; } if
		 * ("accept".equalsIgnoreCase(headerName)) { Enumeration<String>
		 * acceptType = httpRequest .getHeaders(headerName); while
		 * (acceptType.hasMoreElements()) { String accept = (String)
		 * acceptType.nextElement(); String[] acceptParts = accept.split(",");
		 * for (String acceptPart : acceptParts) { if (!"
		 *//*
			 * ".equals(acceptPart)) { contentType = acceptPart.trim(); break; }
			 * } } } // ustalanie parametrow dla renderera na podstawie
			 * naglowkow if (headerName.startsWith("doa.renderer")) { String
			 * headerValue = httpRequest.getHeader(headerName); if
			 * (headerName.equals("doa.renderer.template.id")) { try {
			 * templateResourceId = Long.parseLong(headerValue); } catch
			 * (NumberFormatException e) { // do nothing ... } }
			 * rendererContextVariables.put(headerName, headerValue); } }
			 */

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

        final IDocument toRender = output;
        OutputStream outputStream = null;
        try {
            long renderedSize = 0L;
            outputStream = httpResponse.getOutputStream();
            if (templateResourceId > 0) {
                IEntity entity = doa.lookupByUUID(templateResourceId);
                if (entity == null || !(entity instanceof IStaticResource)) {
                    renderException(httpResponse, new GeneralDOAException(
                            "Unable to find template with id = "
                                    + templateResourceId));
                    return false;
                }
                IStaticResource templateResource = (IStaticResource) entity;
                httpResponse.setHeader("Content-Type", renderer.getMimetype());
                renderedSize = renderer.renderEntity(toRender, outputStream,
                        templateResource, context);
            } else {
                ITemplateFinder templateFinder = null;
                if (appDocument != null) {
                    String templateFinderClass = (String) appDocument
                            .getFieldValue("templateFinder");
                    if (templateFinderClass != null) {
                        try {
                            templateFinder = (ITemplateFinder) doa
                                    .instantiateObject(templateFinderClass,
                                            false);
                        } catch (Exception e) {
                            log.error("Could not instantinate templateFinder class: "
                                    + templateFinderClass);
                        }
                    }
                }
                ITemplateFinder finder = new MultiContainerFinder(
                        templateFinder, (appDocument == null) ? doa
                        : appDocument.getContainer());
                httpResponse.setHeader("Content-Type", renderer.getMimetype());
                renderedSize = renderer.renderEntity(toRender, outputStream,
                        finder, context);
            }
            httpResponse.setHeader("Content-Length", renderedSize + "");
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

	/*
	 * private void renderExceptionRaw(final HttpServletResponse response, final
	 * Throwable throwable) { // tworzenie dokumentu wyjatku PrintWriter writer
	 * = null; try { response.setContentType("text/plain");
	 * response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); writer
	 * = response.getWriter();
	 * writer.println(MessageFormat.format("Exception: {0}",
	 * throwable.getMessage())); writer.println("Stack trace:");
	 * writer.println("------------"); StackTraceElement[] stackTrace =
	 * throwable.getStackTrace(); for (StackTraceElement stackTraceElement :
	 * stackTrace) { writer.println(MessageFormat.format("--- {0}",
	 * stackTraceElement.toString())); } } catch (IOException e) { log.error("",
	 * e); } finally { if (writer != null) { writer.flush(); writer.close(); } }
	 * }
	 */

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

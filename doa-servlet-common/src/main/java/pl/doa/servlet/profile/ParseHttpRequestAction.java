/**
 *
 */
package pl.doa.servlet.profile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.IListDocumentFieldValue;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.entity.IEntityReference;
import pl.doa.resource.IStaticResource;
import pl.doa.utils.profile.IProfiledAction;

/**
 * @author activey
 */
public class ParseHttpRequestAction implements IProfiledAction<IDocument> {

    private static final Logger log = LoggerFactory
            .getLogger(ParseHttpRequestAction.class);

    private static final String ATTRIBUTE_REQUEST_DOCUMENT = "DOA-Request-Document";

    private static final Object HEADER_SERVICE_MODE = "DOA-Service-Mode";

    private static final Object HEADER_SERVICE_MODE_ASYNC = "async";
    private final HttpServletRequest httpRequest;

    private final IDOA doa;

    private final IEntitiesContainer channelContainer;

    private final Collection<String> filterMappings;

    public ParseHttpRequestAction(IDOA doa, HttpServletRequest httpRequest,
                                  IEntitiesContainer channelContainer,
                                  Collection<String> filterMappings) {
        this.doa = doa;
        this.httpRequest = httpRequest;
        this.channelContainer = channelContainer;
        this.filterMappings = filterMappings;
    }

    @Override
    public IDocument invoke() throws GeneralDOAException {
        IDocument document = (IDocument) httpRequest
                .getAttribute(ATTRIBUTE_REQUEST_DOCUMENT);
        if (document != null) {
            return document;
        }

        // tworzenie dokumentu wejsciowego
        IDocumentDefinition httpRequestDefinition = channelContainer
                .getEntityByName("http_request_definition",
                        IDocumentDefinition.class);
        IDocument httpRequestDocument = httpRequestDefinition
                .createDocumentInstance();
        httpRequestDocument.setFieldValue("method", httpRequest.getMethod());
        httpRequestDocument.setFieldValue("url", httpRequest.getRequestURL()
                .toString());
        httpRequestDocument.setFieldValue("remoteAddress",
                httpRequest.getRemoteAddr());
        HttpSession httpSession = httpRequest.getSession(true);
        if (httpSession != null) {
            httpRequestDocument.setFieldValue("sessionId", httpSession.getId());
        }
        IListDocumentFieldValue parameters = (IListDocumentFieldValue) httpRequestDocument
                .getField("parameters", true);

        // wczytywanie zalacznikow
        if ("POST".equals(httpRequest.getMethod())) {
            loadAtachements(httpRequest, parameters);
        }
        Enumeration<String> paramsEnum = httpRequest.getParameterNames();
        while (paramsEnum.hasMoreElements()) {
            String paramName = paramsEnum.nextElement();

            String[] paramValues = httpRequest.getParameterValues(paramName);
            if (paramValues.length > 1) {
                for (int i = 0; i < paramValues.length; i++) {
                    String paramValue = paramValues[i];
                    parameters.addStringField(paramName + "." + (i + 1),
                            paramValue);
                }
            } else {
                String paramValue = httpRequest.getParameter(paramName);
                parameters.addStringField(paramName, paramValue);
            }
        }
        // czytanie body
        try {
            httpRequestDocument.setFieldValue("body",
                    readRequestBody(httpRequest));
        } catch (Exception e) {
            throw new GeneralDOAException(e);
        }

        // flaga okreslajaca czy wykonywac usluge w trybie asynchronicznym
        boolean asynchronous = false;

        // przepisywanie naglowkow
        IListDocumentFieldValue headers = (IListDocumentFieldValue) httpRequestDocument
                .getField("headers", true);
        Enumeration<String> headersEnum = httpRequest.getHeaderNames();
        while (headersEnum.hasMoreElements()) {
            // miedzyczasie sprawdzamy, czy istnieje naglowek
            // DOA-SERVICE-MODE
            String headerName = headersEnum.nextElement();
            String headerValue = httpRequest.getHeader(headerName);
            if (HEADER_SERVICE_MODE.equals(headerName)) {
                if (HEADER_SERVICE_MODE_ASYNC.equals(headerValue)) {
                    asynchronous = true;
                }
            }
            headers.addStringField(headerName, headerValue);
        }
        httpRequestDocument.setFieldValue("asynchronous", asynchronous);

        // przepisywanie ciastek, jezeli jakies istnieja
        Cookie[] cookies = httpRequest.getCookies();
        if (cookies != null) {
            IListDocumentFieldValue cookiesField = (IListDocumentFieldValue) httpRequestDocument
                    .getField("cookies", true);
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                String cookieValue = cookie.getValue();
                cookiesField.addStringField(cookieName, cookieValue);
            }
        }

        String uri = URLDecoder.decode(httpRequest.getRequestURI());
        // wyznaczanie nazwy kontekstu i uri
        final StringBuffer applicationContext = new StringBuffer();
        StringTokenizer uriTokenizer = new StringTokenizer(uri, "/");
        int bufferIndex = 0;

        StringBuffer rootUri = new StringBuffer();
        StringBuffer applicationUri = new StringBuffer();
        main_loop:
        while (uriTokenizer.hasMoreTokens()) {
            String uriElement = uriTokenizer.nextToken();
            if (bufferIndex == 0) {
                applicationContext.append(uriElement);
                rootUri.append("/").append(uriElement);
            } else {
                for (String filterMapping : filterMappings) {
                    StringTokenizer mappingTokenizer = new StringTokenizer(
                            filterMapping, "/");
                    int tokenBuffer = 1;
                    mapping_iterator:
                    while (mappingTokenizer.hasMoreTokens()) {
                        String mappingPart = mappingTokenizer.nextToken();
                        if ("*".equals(mappingPart)) {
                            continue mapping_iterator;
                        }
                        if (tokenBuffer == bufferIndex) {
                            if (mappingPart.equals(uriElement)) {
                                rootUri.append("/").append(mappingPart);
                                continue main_loop;
                            }

                        }
                        tokenBuffer++;
                    }
                }
                applicationUri.append("/").append(uriElement);
            }
            bufferIndex = bufferIndex + 1;
        }

        httpRequestDocument.setFieldValue("rootUri", rootUri.toString());
        httpRequestDocument.setFieldValue("uri", uri);
        httpRequestDocument.setFieldValue("applicationUri", (applicationUri
                .length() == 0) ? "/" : applicationUri.toString());
        httpRequestDocument.setFieldValue("applicationContext",
                applicationContext.toString());

        IEntitiesContainer applicationsContainer = channelContainer
                .getEntityByName("applications", IEntitiesContainer.class);
        IEntityReference applicationDocument = (IEntityReference) applicationsContainer
                .lookupForEntity(new IEntityEvaluator() {

                    @Override
                    public boolean isReturnableEntity(IEntity reference) {
                        return applicationContext.toString().equals(
                                reference.getName());
                    }
                }, false);
        if (applicationDocument != null) {
            httpRequestDocument.setFieldValue("applicationDocument",
                    applicationDocument.getEntity());
        } else {
            throw new GeneralDOAException(
                    "Unable to find application document for context [{0}]",
                    httpRequestDocument
                            .getFieldValueAsString("applicationContext"));
        }

        httpRequest.setAttribute(ATTRIBUTE_REQUEST_DOCUMENT,
                httpRequestDocument);
        return httpRequestDocument;
    }

    @Override
    public String getActionData() {
        return httpRequest.getRequestURL().toString();
    }

    @Override
    public String getActionName() {
        return "ParseHttpRequestAction";
    }

    private void loadAtachements(HttpServletRequest request,
                                 IListDocumentFieldValue parametersField) {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        List<FileItem> items = null;
        try {
            items = upload.parseRequest(request);
        } catch (FileUploadException e1) {
            log.error(e1.getMessage());
            return;
        }
        if (items == null || items.size() < 1) {
            return;
        }
        for (FileItem fileItem : items) {
            try {
                if (fileItem.isFormField()) {
                    parametersField.addStringField(fileItem.getFieldName(),
                            fileItem.getString());
                } else {
                    IDocumentFieldValue referenceField = parametersField
                            .addReferenceField(fileItem.getFieldName());

                    IStaticResource fileResource = doa.createStaticResource(
                            fileItem.getName(), "application/octet-stream");
                    if (fileItem.getContentType() != null) {
                        fileResource.setMimetype(fileItem.getContentType());
                    } else {
                        fileResource.setMimetype("application/octet-stream");
                    }
                    fileResource.setContentFromStream(
                            fileItem.getInputStream(), fileItem.getSize());
                    referenceField.setFieldValue(fileResource);
                }
            } catch (Exception e) {
                log.error("Could not add parameter: " + fileItem.getName());
                continue;
            }
        }
    }

    private IStaticResource readRequestBody(HttpServletRequest request)
            throws Exception {
        try {
            IStaticResource requestBody = doa.createStaticResource(request
                    .getContentType());
            requestBody.setContentFromStream(request.getInputStream(),
                    new Long(request.getContentLength()));
            return requestBody;
        } catch (IOException ex) {
            throw ex;
        }
    }

}

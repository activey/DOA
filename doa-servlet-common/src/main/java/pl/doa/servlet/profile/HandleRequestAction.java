package pl.doa.servlet.profile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.document.IDocument;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.utils.profile.IProfiledAction;

public class HandleRequestAction implements IProfiledAction<IDocument> {

    private static final String SERVICE_HANDLE_SESSION = "/channels/http/handle_session";

    private final static Logger log = LoggerFactory
            .getLogger(HandleRequestAction.class);

    private final IDOA doa;

    private final IDocument httpRequestDocument;

    public HandleRequestAction(IDOA doa, IDocument httpRequestDocument) {
        this.doa = doa;
        this.httpRequestDocument = httpRequestDocument;
    }

    @Override
    public IDocument invoke() throws GeneralDOAException {
        // uruchamianie uslugi, ktora zajmie sie obsluga zadania
        final IServiceDefinition handleSessionService = (IServiceDefinition) doa
                .lookupEntityByLocation(SERVICE_HANDLE_SESSION);
        if (handleSessionService == null) {
            throw new GeneralDOAException("Unable to find service [{0}]",
                    SERVICE_HANDLE_SESSION);
        }
        IRunningService serviceInstance = handleSessionService.executeService(
                httpRequestDocument, null, false);
        return serviceInstance.getOutput();
    }

	/*
     * @Override public String getActionData() { String queryString =
	 * httpRequest.getQueryString(); return
	 * httpRequest.getRequestURL().toString() + ((queryString == null) ? "" :
	 * ("?" + httpRequest .getQueryString())); }
	 */

    @Override
    public String getActionData() {
        return "";
    }

    @Override
    public String getActionName() {
        return "HandleRequestAction";
    }

}

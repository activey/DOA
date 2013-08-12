package pl.doa.utils.profile.impl;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.agent.IAgent;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.utils.profile.IProfiledAction;

public class ProfileAgentAction implements IProfiledAction<IAgent> {

    private IDocument incomingDocument;
    private String startLookupLocation;
    private IDOA doa;

    public ProfileAgentAction(IDOA doa, IDocument incomingDocument,
                              String startLookupLocation) {
        this.doa = doa;
        this.incomingDocument = incomingDocument;
        this.startLookupLocation = startLookupLocation;

    }

    @Override
    public IAgent invoke() throws GeneralDOAException {
        if (incomingDocument == null) {
            return null;
        }
        IEntityEvaluator evaluator = new IncomingDocumentEvaluator(
                incomingDocument);
        if (startLookupLocation == null) {
            startLookupLocation = "/";
        }

        // wyszukiwanie dokumentu oczekiwanego agenta
        IDocument waitingDoc = (IDocument) doa.lookupEntityFromLocation(
                startLookupLocation, evaluator, true);
        if (waitingDoc == null) {
            return null;
        }

        IEntitiesContainer agentHome = waitingDoc.getContainer().getContainer();
        // wyszukiwanie agenta w jego katalogu domowym

        IAgent agent = (IAgent) agentHome.lookupForEntity(
                new IEntityEvaluator() {

                    @Override
                    public boolean isReturnableEntity(IEntity entity) {
                        return (entity instanceof IAgent);
                    }
                }, false);
        return agent;
    }

    @Override
    public String getActionData() {
        return null;
    }

    @Override
    public String getActionName() {
        return "ProfileAgentAction";
    }

}

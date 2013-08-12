package pl.doa.utils.profile.impl;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.agent.IAgent;
import pl.doa.document.IDocument;
import pl.doa.entity.ITransactionCallback;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;
import pl.doa.utils.profile.IProfiledAction;

public class ExecuteServiceAction implements IProfiledAction<IRunningService> {

    private final static Logger perfLog = LoggerFactory
            .getLogger(ExecuteServiceAction.class);

    private final IDOA doa;
    private final IDocument input;
    private final IAgent runAs;
    private final boolean asynchronous;
    private final IServiceDefinition service;

    private boolean transactional = false;

    public ExecuteServiceAction(IDOA doa, IServiceDefinition service,
                                IDocument input, IAgent runAs, boolean asynchronous,
                                boolean transactional) {
        this.doa = doa;
        this.service = service;
        this.input = input;
        this.runAs = runAs;
        this.asynchronous = asynchronous;
        this.transactional = transactional;
    }

    public ExecuteServiceAction(IDOA doa, IServiceDefinition service,
                                IDocument input, IAgent runAs, boolean asynchronous) {
        this(doa, service, input, runAs, asynchronous, false);
    }

    @Override
    public IRunningService invoke() throws GeneralDOAException {
        perfLog.debug(MessageFormat.format("Executing service: [{0}]",
                service.getLocation()));
        if (transactional) {
            return doa
                    .doInTransaction(new ITransactionCallback<IRunningService>() {

                        @Override
                        public IRunningService performOperation()
                                throws Exception {
                            return doa.executeService(service, input, asynchronous, runAs);
                        }
                    });
        }
        return doa.executeService(service, input, asynchronous, runAs);
    }

    @Override
    public String getActionData() {
        return service.getLocation();
    }

    @Override
    public String getActionName() {
        return "ExecuteServiceAction";
    }

}

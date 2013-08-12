/**
 *
 */
package pl.doa.entity.transaction;

import pl.doa.agent.IAgent;
import pl.doa.document.IDocument;
import pl.doa.entity.ITransactionCallback;
import pl.doa.service.IRunningService;
import pl.doa.service.IServiceDefinition;

/**
 * @author activey
 */
public class CallServiceTransaction implements
        ITransactionCallback<IRunningService> {

    private IServiceDefinition definition;
    private IDocument input;
    private IAgent agent;

    public CallServiceTransaction(IServiceDefinition definition,
                                  IDocument input, IAgent agent) {
        this.definition = definition;
        this.input = input;
        this.agent = agent;
    }

    @Override
    public IRunningService performOperation() throws Exception {
        return definition.executeService(input, agent, false);
    }

}

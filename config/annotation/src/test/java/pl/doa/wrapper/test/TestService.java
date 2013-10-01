package pl.doa.wrapper.test;

import pl.doa.GeneralDOAException;
import pl.doa.wrapper.annotation.ServiceDefinition;
import pl.doa.wrapper.service.AbstractWrappedServiceDefinitionLogic;

@ServiceDefinition
public class TestService extends AbstractWrappedServiceDefinitionLogic<TestType> {

    @Override
    public void align() throws GeneralDOAException {

    }
}

package pl.doa.wicket.model.service;

import pl.doa.service.IServiceDefinition;
import pl.doa.wicket.model.EntityModel;

/**
 * Created with IntelliJ IDEA.
 * User: activey
 * Date: 31.07.13
 * Time: 15:59
 * To change this template use File | Settings | File Templates.
 */
public class ServiceDefinitionModel extends EntityModel<IServiceDefinition> {

    public ServiceDefinitionModel(long entityId) {
        super(entityId);
    }

    public ServiceDefinitionModel(IServiceDefinition entity) {
        super(entity);
    }

    public ServiceDefinitionModel(String entityLocation, boolean applicationRelative) {
        super(entityLocation, applicationRelative);
    }

    public ServiceDefinitionModel(String entityLocation) {
        super(entityLocation);
    }
}

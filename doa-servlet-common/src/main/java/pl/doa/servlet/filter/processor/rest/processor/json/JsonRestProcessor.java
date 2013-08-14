/**
 *
 */
package pl.doa.servlet.filter.processor.rest.processor.json;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.container.IEntitiesContainer;
import pl.doa.resource.IStaticResource;
import pl.doa.servlet.filter.processor.rest.RestCallResponse;
import pl.doa.servlet.filter.processor.rest.processor.BasicRestProcessor;

/**
 * @author activey
 */
public class JsonRestProcessor extends BasicRestProcessor {

    public JsonRestProcessor(String uriPattern, String modifyPattern) {
        super(uriPattern, modifyPattern);
    }

    @Override
    protected RestCallResponse doPutContainer(IEntitiesContainer container,
                                              IStaticResource inputResource, IDOA doa) throws GeneralDOAException {

        return super.doPutContainer(container, inputResource, doa);
    }

}

package pl.doa.artifact.deploy;

import pl.doa.IDOA;
import pl.doa.artifact.IArtifact;
import pl.doa.container.IEntitiesContainer;

/**
 * @author activey
 * @date 02.10.13 20:42
 */
public class XMLDeploymentProcessor extends AbstractDeploymentProcessor {

    public XMLDeploymentProcessor(IArtifact artifact, IDOA doa) {
        super(artifact, doa);
    }

    @Override
    public void process(IEntitiesContainer baseContainer) throws Exception {
    }
}

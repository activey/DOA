/**
 *
 */
package pl.doa.artifact.tag;


import nu.xom.Element;
import nu.xom.Nodes;
import pl.doa.artifact.IArtifact;
import pl.doa.container.IEntitiesContainer;

/**
 * @author activey
 */
public class DeployTag extends DeploymentProcessorSupportTag {

    private IEntitiesContainer defaultContainer;

    /* (non-Javadoc)
     * @see pl.doa.templates.tags.Tag#processTagStart()
     */
    @Override
    public void processTagStart() throws Exception {

    }

    /* (non-Javadoc)
     * @see pl.doa.templates.tags.Tag#processTagEnd()
     */
    @Override
    public Nodes processTagEnd() throws Exception {
        IArtifact artifact = getArtifact();
        artifact.setBaseContainer(getDefaultContainer());

        Element result = new Element("result");
        result.appendChild("done");
        return new Nodes(result);
    }

    public IEntitiesContainer getDefaultContainer() {
        return defaultContainer;
    }

    public void setDefaultContainer(IEntitiesContainer defaultContainer) {
        this.defaultContainer = defaultContainer;
    }

}

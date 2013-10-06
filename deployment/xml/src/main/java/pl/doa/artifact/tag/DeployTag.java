/**
 *
 */
package pl.doa.artifact.tag;


import nu.xom.Element;
import nu.xom.Nodes;

/**
 * @author activey
 */
public class DeployTag extends DeploymentProcessorSupportTag {

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
        Element result = new Element("result");
        result.appendChild("done");
        return new Nodes(result);
    }
}

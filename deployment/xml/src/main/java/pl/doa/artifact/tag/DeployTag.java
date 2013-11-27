/**
 *
 */
package pl.doa.artifact.tag;


import nu.xom.Nodes;
import pl.doa.templates.xml.element.BaseElement;

import java.util.Date;

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
        BaseElement result = createElement("deployment");
        result.addAttribute("time", new Date().getTime() + "");
        result.appendChildren(element.getChildNodes());
        return new Nodes(result);
    }
}

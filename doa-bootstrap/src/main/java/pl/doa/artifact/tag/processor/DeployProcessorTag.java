/**
 * 
 */
package pl.doa.artifact.tag.processor;

import nu.xom.Nodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.artifact.tag.DeployTag;
import pl.doa.artifact.tag.EntitiesContainerTag;
import pl.doa.artifact.tag.FieldValueTag;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.templates.tags.Tag;

/**
 * @author activey
 * 
 */
public class DeployProcessorTag extends Tag {

    private final static Logger log = LoggerFactory.getLogger(DeployProcessorTag.class);

	private IDeploymentProcessor processor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.doa.templates.tags.Tag#processTagStart()
	 */
	@Override
	public void processTagStart() throws Exception {
		Tag parent = getParent();
        IEntitiesContainer container = null;
		if (parent instanceof EntitiesContainerTag) {
			EntitiesContainerTag containerTag = (EntitiesContainerTag) parent;
            container = containerTag.getEntity();
		} else if (parent instanceof DeployTag) {
			DeployTag deployTag = (DeployTag) parent;
            container =
					deployTag.getDefaultContainer();
		}
        log.debug("Running deployment processor: " + processor.getClass().getName());
		processor.process(container, context);
	}


	
	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.doa.templates.tags.Tag#processTagEnd()
	 */
	@Override
	public Nodes processTagEnd() throws Exception {
		return new Nodes();
	}

	public IDeploymentProcessor getProcessor() {
		return processor;
	}

	public void setProcessor(IDeploymentProcessor processor) {
		this.processor = processor;
	}

	
}

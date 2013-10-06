/**
 *
 */
package pl.doa.artifact.deploy;

import pl.doa.container.IEntitiesContainer;
import pl.doa.templates.TemplateContext;

import java.io.File;

/**
 * @author activey
 */
public class DeploymentContext extends TemplateContext {

    public static final String VAR_PROCESSOR = "deploy.processor";

    public static final String VAR_DEPLOYED = "deploy.file";

    public static final String VAR_ROOT = "deploy.root";

    public DeploymentContext(IDeploymentProcessor processor, File deployedFile, IEntitiesContainer root) {
        setVariable(VAR_PROCESSOR, processor);
        setVariable(VAR_DEPLOYED, deployedFile);
        setVariable(VAR_ROOT, root);
    }

    public IDeploymentProcessor getProcessor() {
        return (IDeploymentProcessor) getVariable(VAR_PROCESSOR);
    }

    public IEntitiesContainer getRoot() {
        return (IEntitiesContainer) getVariable(VAR_ROOT);
    }
}

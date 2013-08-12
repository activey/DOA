package pl.doa.artifact.tag.processor;

import pl.doa.container.IEntitiesContainer;
import pl.doa.templates.TemplateContext;

public interface IDeploymentProcessor {

    public void process(IEntitiesContainer container, TemplateContext context) throws Exception;

}

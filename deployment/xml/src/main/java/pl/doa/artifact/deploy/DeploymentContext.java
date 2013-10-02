/**
 *
 */
package pl.doa.artifact.deploy;

import java.io.File;
import java.util.List;

import pl.doa.IDOA;
import pl.doa.artifact.IArtifact;
import pl.doa.artifact.tag.DeploymentProcessorSupportTag;
import pl.doa.entity.startable.IStartableEntity;
import pl.doa.templates.TemplateContext;

/**
 * @author activey
 */
public class DeploymentContext extends TemplateContext {

    public void setArtifact(IArtifact artifact) {
        setVariable(DeploymentProcessorSupportTag.VAR_ARTIFACT, artifact);
    }

    public IArtifact getArtifact() {
        return (IArtifact) getVariable(DeploymentProcessorSupportTag.VAR_ARTIFACT);
    }

    public void setDoa(IDOA doa) {
        setVariable(DeploymentProcessorSupportTag.VAR_DOA, doa);
    }

    public IDOA getDoa() {
        return (IDOA) getVariable(DeploymentProcessorSupportTag.VAR_DOA, false);
    }

    public void setAutostart(List<IStartableEntity> autostartEntities) {
        setVariable("autostart", autostartEntities);
    }

    public void setArtifactJar(File artifactJarFile) {
        setVariable("artifactJarFile", artifactJarFile.getAbsolutePath());
        setVariable("artifactFile", artifactJarFile.getAbsolutePath());
    }

    public File getArtifactJar() {
        String fileLocation = (String) getVariable("artifactFile");
        return new File(fileLocation);
    }

}

/**
 *
 */
package pl.doa.artifact.deploy;

import pl.doa.templates.TemplateContext;

import java.io.File;

/**
 * @author activey
 */
public class DeploymentContext extends TemplateContext {

    public void setArtifactJar(File artifactJarFile) {
        setVariable("artifactJarFile", artifactJarFile.getAbsolutePath());
        setVariable("artifactFile", artifactJarFile.getAbsolutePath());
    }

    public File getArtifactJar() {
        String fileLocation = (String) getVariable("artifactFile");
        return new File(fileLocation);
    }

}

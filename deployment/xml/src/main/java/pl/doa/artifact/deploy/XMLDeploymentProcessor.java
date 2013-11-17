package pl.doa.artifact.deploy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.artifact.tag.DeploymentTagLibrary;
import pl.doa.container.IEntitiesContainer;
import pl.doa.templates.TemplateContext;

import java.io.File;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author activey
 * @date 02.10.13 20:42
 */
public class XMLDeploymentProcessor extends AbstractDeploymentProcessor {

    private final static Logger log = LoggerFactory.getLogger(XMLDeploymentProcessor.class);

    @Override
    public void deployArtifact(File deployedFile, IEntitiesContainer root) throws Exception {
        JarFile jarFile = new JarFile(deployedFile);
        JarEntry deployScriptEntry = jarFile.getJarEntry("deploy.xml");
        if (deployScriptEntry == null) {
            log.warn("Unable to find deployment script, skipping ...");
            return;
        }
        try {
            InputStream deployFile =
                    jarFile.getInputStream(deployScriptEntry);
            TemplateContext context = new DeploymentContext(this, deployedFile, root);
            try {
                context.registerTagLibrary(new DeploymentTagLibrary());
            } catch (Exception e) {
                throw new GeneralDOAException(e);
            }
            log.debug("Executing deployment script ...");
            try {
                String renderedContent = context.execute(deployFile);
            } catch (Exception e) {
                throw new GeneralDOAException(e);
            }
            deployFile.close();
        } catch (Exception e) {
            throw new GeneralDOAException(
                    "Error while processing deployment script ...", e);
        }
    }
}

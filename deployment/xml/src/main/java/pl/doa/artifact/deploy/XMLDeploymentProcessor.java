package pl.doa.artifact.deploy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.artifact.tag.DeploymentTagLibrary;
import pl.doa.container.IEntitiesContainer;

import java.io.File;
import java.io.IOException;
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
    public void process(File deployedFile, IEntitiesContainer root) throws Exception {
        JarFile jarFile;
        try {
            jarFile = new JarFile(deployedFile);
        } catch (IOException e) {
            throw new GeneralDOAException(e);
        }
        JarEntry deployScriptEntry = jarFile.getJarEntry("deploy.core");
        if (deployScriptEntry == null) {
            deployScriptEntry = jarFile.getJarEntry("deploy.xml");
        }
        if (deployScriptEntry == null) {
            log.warn("unable to find deploy.core, skipping ...");
            return;
        }
        try {
            InputStream deployFile =
                    jarFile.getInputStream(deployScriptEntry);
            // uruchamianie skryptu z pliku "deploy.core"
            DeploymentContext context = new DeploymentContext();
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
                    "error while processing deployment plan file ...", e);
        }
    }
}

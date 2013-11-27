package pl.doa.artifact.deploy;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.artifact.tag.DeploymentTagLibrary;
import pl.doa.container.IEntitiesContainer;
import pl.doa.templates.TemplateContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static pl.doa.utils.JarUtils.findJarEntry;

/**
 * @author activey
 * @date 02.10.13 20:42
 */
public class XMLDeploymentProcessor extends AbstractDeploymentProcessor {

    private final static Logger LOG = LoggerFactory.getLogger(XMLDeploymentProcessor.class);

    @Override
    public void deployArtifact(File artifactFile, IEntitiesContainer root) throws Exception {
        try {
            InputStream deployFile = findJarEntry(artifactFile, new DeploymentScriptMatcher());
            TemplateContext context = new DeploymentContext(this, artifactFile, root);
            try {
                context.registerTagLibrary(new DeploymentTagLibrary());
            } catch (Exception e) {
                throw new GeneralDOAException(e);
            }
            LOG.debug("Executing deployment script ...");
            try {
                String renderedContent = context.execute(deployFile);
                OutputStream output = new FileOutputStream(File.createTempFile("deployment", "-history"));
                IOUtils.write(renderedContent, output);
            } catch (Exception e) {
                throw new GeneralDOAException(e);
            } finally {
                deployFile.close();
            }
        } catch (Exception e) {
            throw new GeneralDOAException(
                    "Error while processing deployment script ...", e);
        }
    }
}

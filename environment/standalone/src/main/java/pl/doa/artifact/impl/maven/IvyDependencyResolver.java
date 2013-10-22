package pl.doa.artifact.impl.maven;

import org.apache.ivy.core.event.EventManager;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.plugins.resolver.ChainResolver;
import org.apache.ivy.plugins.resolver.FileSystemResolver;
import org.apache.ivy.plugins.resolver.IBiblioResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Properties;

public class IvyDependencyResolver extends ChainResolver {

    private static final Logger LOG = LoggerFactory.getLogger(IvyDependencyResolver.class);
    private final EventManager eventManager;
    private final IvySettings settings;

    public IvyDependencyResolver(String resolverName, EventManager eventManager, IvySettings settings) {
        setName(resolverName);

        this.eventManager = eventManager;
        this.settings = settings;
        try {
            initialize();
        } catch (IOException e) {
            LOG.error("", e);
        }
    }

    private void initialize() throws IOException {
        // setting default repositories
        FileSystemResolver fsResolver = new FileSystemResolver();
        fsResolver.setEventManager(eventManager);
        fsResolver.setSettings(settings);
        fsResolver.setName("local-maven-repo");
        fsResolver.setM2compatible(true);
        fsResolver.setLocal(true);
        fsResolver.setCheckconsistency(false);
        fsResolver
                .addArtifactPattern(System.getProperty("user.home")
                        + "/.m2/repository/[organisation]/[module]/[revision]/[module]-[revision](-[classifier]).jar");
        fsResolver
                .addIvyPattern(System.getProperty("user.home")
                        + "/.m2/repository/[organisation]/[module]/[revision]/[module]-[revision](-[classifier]).pom");
        add(fsResolver);

        // reading maven repositories from properties file
        InputStream propsStream =
                getClass().getClassLoader().getResourceAsStream(
                        "repositories.properties");
        if (propsStream != null) {
            Properties repos = new Properties();
            repos.load(propsStream);

            Enumeration<Object> reposNames = repos.keys();
            while (reposNames.hasMoreElements()) {
                String repoName = (String) reposNames.nextElement();
                String repoUrl = repos.getProperty(repoName);

                LOG.debug(MessageFormat.format("Adding maven repository: {0} [{1}]", repoName, repoUrl));

                IBiblioResolver mavenResolver = new IBiblioResolver();
                mavenResolver.setEventManager(eventManager);
                mavenResolver.setSettings(settings);
                mavenResolver.setName(repoName);
                mavenResolver.setM2compatible(true);
                mavenResolver.setCheckconsistency(false);
                mavenResolver.setRoot(repoUrl);
                mavenResolver
                        .setPattern("[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier]).[ext]");
                add(mavenResolver);
            }
        }
    }


}

/*******************************************************************************
 * Copyright 2011 Inhibi Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are
 * permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright 
 * notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright 
 * notice, this list
 *        of conditions and the following disclaimer in the documentation 
 * and/or other materials
 *        provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY INHIBI LTD ``AS IS'' AND ANY 
 * EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * INHIBI LTD OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation 
 * are those of the authors and should not be interpreted as representing 
 * official policies, either expressed or implied, of Inhibi Ltd.
 *
 * Contributors:
 *    Inhibi Ltd - initial API and implementation
 *******************************************************************************/
package pl.doa.artifact.impl;

import org.apache.commons.io.IOUtils;
import org.apache.ivy.Ivy;
import org.apache.ivy.core.event.EventManager;
import org.apache.ivy.core.module.descriptor.Artifact;
import org.apache.ivy.core.module.descriptor.DependencyDescriptor;
import org.apache.ivy.core.module.descriptor.ExcludeRule;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.module.id.ModuleId;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.report.ArtifactDownloadReport;
import org.apache.ivy.core.report.DownloadReport;
import org.apache.ivy.core.report.DownloadStatus;
import org.apache.ivy.core.resolve.DownloadOptions;
import org.apache.ivy.core.resolve.ResolvedModuleRevision;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.plugins.parser.m2.PomModuleDescriptorParser;
import org.apache.ivy.plugins.repository.Resource;
import org.apache.ivy.plugins.repository.url.URLResource;
import org.apache.ivy.plugins.resolver.ChainResolver;
import org.apache.ivy.plugins.resolver.FileSystemResolver;
import org.apache.ivy.plugins.resolver.IBiblioResolver;
import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.io.URLInputStreamFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.IDOA;
import pl.doa.artifact.IArtifact;
import pl.doa.artifact.IDirectoryListener;
import pl.doa.artifact.IJarEntryMatcher;
import pl.doa.artifact.deploy.IDeploymentProcessor;
import pl.doa.artifact.deployment.IArtifactManager;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class AbstractArtifactManagerOld
        implements IArtifactManager,IDirectoryListener {

    private final static Logger log = LoggerFactory
            .getLogger(AbstractArtifactManagerOld.class);
    private static final String ARTIFACT_PROCESSOR = "deploy.processor";
    private static final String ARTIFACT_PROCESSOR_DEFAULT = "pl.doa.artifact.deploy.XMLDeploymentProcessor";
    private static final String ARTIFACT_ROOT = "deploy.root";
    private static final String ARTIFACT_ROOT_DEFAULT = "/tmp";
    protected final IDOA doa;
    private Ivy ivy;
    private List<ExcludeRule> excludeRules = new ArrayList<ExcludeRule>();
    private ChainResolver chainResolver;
    private List<URL> repositories = new ArrayList<URL>();

    public AbstractArtifactManagerOld(IDOA doa) {
        this.doa = doa;
    }

    @Override
    public final void undeployArtifact(IArtifact artifact)
            throws GeneralDOAException {
        if (artifact == null) {
            throw new GeneralDOAException(
                    "Could not undeploy because artifact is null.");
        }

    }

    @Override
    public final IArtifact deployArtifact(String artifactFileName, InputStream artifactData)
            throws GeneralDOAException {

                File artifactFile;
                try {
                    artifactFile = File.createTempFile("artifact", "tmp");
                    IOUtils.copy(artifactData, new FileWriter(artifactFile));
                } catch (IOException e) {
                    throw new GeneralDOAException(e);
                }
                return deployJarArtifact(artifactFile);


    }

    @Override
    public final IArtifact deployArtifact(String artifactFileName,
                                          byte[] artifactData) throws GeneralDOAException {
        return deployArtifact(artifactFileName, new ByteArrayInputStream(
                artifactData));
    }

    private JarEntry findJarEntry(File file, IJarEntryMatcher entryMatcher)
            throws GeneralDOAException {
        JarFile jarFile;
        try {
            jarFile = new JarFile(file);
        } catch (IOException e) {
            throw new GeneralDOAException(e);
        }
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) entries.nextElement();
            if (entryMatcher.entryMatch(jarEntry)) {
                return jarEntry;
            }
        }
        return null;
    }

    private URL getJarEntryURL(File jarFile, JarEntry jarEntry) {
        String entryLocation = jarEntry.getName();
        try {
            String url =
                    MessageFormat.format("jar:file:{0}!/{1}", jarFile,
                            entryLocation);
            return new URL(url);
        } catch (MalformedURLException e) {
            log.error("", e);
            return null;
        }
    }

    protected IArtifact deployJarArtifact(File artifactFile) throws GeneralDOAException {
        JarEntry mavenDescriptorEntry = findJarEntry(artifactFile, new IJarEntryMatcher() {
                    @Override
                    public boolean entryMatch(JarEntry entry) {
                        if (entry.getName().endsWith("pom.xml") || entry.getName().endsWith(".pom")) {
                            return true;
                        }
                        return false;
                    }
                });
        if (mavenDescriptorEntry != null) {
            log.debug(MessageFormat.format("Found maven artifact descriptor file: {0}, processing ...",
                            mavenDescriptorEntry.getName()));
        } else {
            log.debug(MessageFormat.format("No maven artifact found for descriptor file: {0}, skipping ...",
                            mavenDescriptorEntry.getName()));
            return null;
        }

        URL jarEntryURL = getJarEntryURL(artifactFile, mavenDescriptorEntry);
        log.debug(MessageFormat.format("Getting jar entry from url: {0}", jarEntryURL));

        // parsing maven descriptor
        PomModuleDescriptorParser pomParser = PomModuleDescriptorParser.getInstance();
        try {
            initializeRepository(jarEntryURL);

            ModuleDescriptor moduleDescriptor = pomParser.parseDescriptor(ivy.getSettings(), jarEntryURL, false);
            return deployIvyArtifact(moduleDescriptor, artifactFile, new ArrayList<String>());
        } catch (Exception e) {
            throw new GeneralDOAException(e);
        }
    }

    private IArtifact deployIvyArtifact(ModuleDescriptor descriptor, File artifactFile) throws GeneralDOAException {
        return deployIvyArtifact(descriptor, artifactFile, new ArrayList<String>());
    }

    private IArtifact deployIvyArtifact(ModuleDescriptor moduleDescriptor, File artifactFile, List<String> dependenciesToKeep)
            throws GeneralDOAException {

        List<IArtifact> dependencies = new ArrayList<IArtifact>();
        DependencyDescriptor[] moduleDependencies = moduleDescriptor.getDependencies();

        Artifact artifactTest = moduleDescriptor.getAllArtifacts()[0];
        log.debug("Analyzing dependencies for: " + artifactTest.getName());

        List<String> notDeployed = new ArrayList<String>();
        for (DependencyDescriptor dependency : moduleDependencies) {
            collectExcludeRules(dependency);

            String groupId = dependency.getDependencyId().getOrganisation();
            String artifactId = dependency.getDependencyId().getName();
            String artifactVersion = dependency.getDependencyRevisionId().getRevision();

            if (isExcluded(dependency)) {
                log.debug(MessageFormat.format("Skipping artifact: [{0}.{1}.{2}]", groupId, artifactId, artifactVersion));
                continue;
            }

            log.debug(MessageFormat.format("dependency: {0}.{1}.{2}", groupId, artifactId, artifactVersion));

            String[] configurations = dependency.getModuleConfigurations();
            dependenciesToKeep.add(MessageFormat.format("{0}.{1}.{2}", groupId, artifactId, artifactVersion));
            if (configurations == null || configurations.length == 0) {
                continue;
            }

            boolean include = false;
            inner:
            for (String scope : configurations) {
                if ("runtime".equals(scope)) {
                    include = true;
                    break inner;
                }
            }
            if (!include) {
                log.debug(MessageFormat.format(
                        "Skipping artifact: [{0}.{1}.{2}]", groupId,
                        artifactId, artifactVersion));
                continue;
            }

            // szukanie artefaktu w repo
            IArtifact artifact = lookupArtifact(groupId, artifactId, artifactVersion);
            boolean artifactAlreadyDeployed = (artifact != null);
            log.debug(MessageFormat.format("Artifact {0}.{1} {2} exists? {3}", groupId, artifactId,
                    ((artifactVersion == null) ? "[newest]" : artifactVersion),
                    (artifactAlreadyDeployed) ? "YES" : "NO"));
            if (artifactAlreadyDeployed) {
                dependencies.add(artifact);
                continue;
            }
            // pobieranie zaleznosci z zewnetrznego repozytorium
            ModuleId moduleId = new ModuleId(groupId, artifactId);
            ModuleRevisionId revisionId = new ModuleRevisionId(moduleId, artifactVersion, artifactVersion);
            ResolvedModuleRevision revision = ivy.findModule(revisionId);
            if (revision == null) {
                notDeployed.add(MessageFormat.format("{0}.{1}.{2}", groupId,
                        artifactId, artifactVersion));
                log.error("dependend artifact not found ...");
                continue;
            }
            ModuleDescriptor descriptor = revision.getDescriptor();
            // czytanie dodatkowych repozytoriow
            Resource pomResource = descriptor.getResource();
            if (pomResource instanceof URLResource) {
                URLResource pom = (URLResource) pomResource;
                String pomUrlStr = pom.getURL().toString();
                try {
                    URL pomUrl = (pomUrlStr.endsWith("original")) ? pom.getURL() : new URL(pom.getURL().toString()
                                    + ".original");
                    initializeRepository(pomUrl);
                } catch (Exception e) {
                    log.warn(MessageFormat.format("Unable to parse pom from location: {0}", pomUrlStr));
                }
            }

            Artifact[] artifacts = descriptor.getAllArtifacts();
            for (Artifact ivyArtifact : artifacts) {
                if ("source".equals(ivyArtifact.getType()) || "javadoc".equals(ivyArtifact.getType())
                        || "wrapper".equals(ivyArtifact.getType()) || "bundle".equals(ivyArtifact.getType())) {
                    continue;
                }
                DownloadReport report = revision.getArtifactResolver().download(new Artifact[]{ivyArtifact},
                                new DownloadOptions());

                ArtifactDownloadReport downloadRep =
                        report.getArtifactReport(ivyArtifact);
                DownloadStatus status = downloadRep.getDownloadStatus();
                if (status == DownloadStatus.FAILED) {
                    throw new GeneralDOAException("Unable to download artifact: [{0}]", ivyArtifact.getId());
                }
                File dependencyFile = downloadRep.getLocalFile();
                log.debug(MessageFormat.format("Deploying downloaded artifact: {0}", downloadRep.getName()));

                IArtifact deployedDependency = deployIvyArtifact(descriptor, dependencyFile);
                if (deployedDependency != null) {
                    dependencies.add(deployedDependency);
                }
            }
        }

        if (notDeployed != null && notDeployed.size() > 0) {
            log.debug("---------------------------");
            log.debug("Not deployed dependencies:");
            for (String dependency : notDeployed) {
                log.debug(MessageFormat.format("*** [{0}]", dependency));
            }
            log.debug("---------------------------");
            throw new GeneralDOAException("dependencies check failed ...");
        }

        Artifact[] moduleArtifacts = moduleDescriptor.getAllArtifacts();
        if (moduleArtifacts == null || moduleArtifacts.length == 0) {
            log.debug("No artifacts found in given module descriptor, skipping ...");
            return null;
        }
        Artifact moduleArtifact = moduleArtifacts[0];

        // wyszukiwanie lub tworzenie nowego artefaktu
        ModuleRevisionId revisionId = moduleArtifact.getModuleRevisionId();
        String version = null;
        if (revisionId != null) {
            version = revisionId.getRevision();
        }
        String artifactId = moduleArtifact.getId().getArtifactId().getName();
        String groupId = moduleArtifact.getModuleRevisionId().getOrganisation();
        String artifactDescription = moduleDescriptor.getDescription();

        IArtifact existingArtifact = lookupArtifact(groupId, artifactId, null);
        if (existingArtifact != null && version != null) {
            // TODO implement undeployment
            log.debug("Artifact [" + getArtifactName(moduleArtifact) + "] is already deployed! skipping ...");
            return existingArtifact;
        }
        IArtifact newArtifact =
                doa.createArtifact(
                        getArtifactName(moduleArtifact));
        newArtifact.setDescription(artifactDescription);
        newArtifact.setArtifactFileName(artifactFile.getName());
        newArtifact.setArtifactId(artifactId);
        newArtifact.setGroupId(groupId);
        newArtifact.setVersion(version);
        newArtifact.store(IDOA.ARTIFACTS_CONTAINER);
        try {
            newArtifact.setArtifactResourceStream(new FileInputStream(
                    artifactFile.getAbsolutePath()), artifactFile.length());
            newArtifact.setDependencies(dependencies);
            // rejestrowanie artefaktu w classloaderze
            registerClassloaderArtifact(newArtifact);
        } catch (Exception e) {
            throw new GeneralDOAException(e);
        }


        try {
            JarFile jarFile = new JarFile(artifactFile);
            JarEntry artifactDescriptor = jarFile.getJarEntry(new StringBuilder()
                    .append(getArtifactName(moduleArtifact)).append(".properties").toString());
            if (artifactDescriptor != null) {
                InputStream descriptorStream = jarFile.getInputStream(artifactDescriptor);
                Properties artifactProperties = new Properties();
                artifactProperties.load(descriptorStream);

                // initializing deployment processor
                IDeploymentProcessor processor = (IDeploymentProcessor) doa.instantiateObject(artifactProperties
                        .getProperty(ARTIFACT_PROCESSOR, ARTIFACT_PROCESSOR_DEFAULT));
                if (processor != null) {
                    log.debug(String.format("Running deployment processor: %s", processor.getClass()));
                    processor.setDoa(doa);
                    processor.setArtifact(newArtifact);
                    try {
                        String rootLocation = artifactProperties.getProperty(ARTIFACT_ROOT, ARTIFACT_ROOT_DEFAULT);
                        IEntitiesContainer deploymentRoot = (IEntitiesContainer) doa
                                .lookupEntityByLocation(rootLocation);
                        if (deploymentRoot == null) {
                            log.error(String.format("Unable to find deployment root: [%s]", rootLocation));
                            return null;
                        }
                        processor.process(artifactFile, deploymentRoot);
                    } catch (Exception e) {
                        throw new GeneralDOAException(e);
                    }
                }
            }
            return newArtifact;
        } catch (IOException e) {
            throw new GeneralDOAException(e);
        }
    }

    private String getArtifactName(Artifact artifact) {
        return MessageFormat.format("{0}.{1}", artifact
                .getModuleRevisionId().getOrganisation(), artifact.getId()
                .getArtifactId().getName());
    }

    private boolean isExcluded(DependencyDescriptor dependency) {
        for (ExcludeRule exclusion : this.excludeRules) {
            String groupId = dependency.getDependencyId().getOrganisation();
            String artifactId = dependency.getDependencyId().getName();

            String module = exclusion.getAttribute("module");
            String organisation = exclusion.getAttribute("organisation");
            if (module.equals(artifactId) && organisation.equals(groupId)) {
                return true;
            }

        }
        return false;
    }

    private void collectExcludeRules(DependencyDescriptor dependency) {
        ExcludeRule[] excludeRules =
                dependency
                        .getExcludeRules(dependency.getModuleConfigurations());
        for (ExcludeRule excludeRule : excludeRules) {
            this.excludeRules.add(excludeRule);
        }

    }

    /**
     * Metoda wyszukuje artefakt.
     *
     * @param groupId         Grupa artefaktu.
     * @param artifactId      Identyfikator artefaktu.
     * @param artifactVersion
     * @return Instancja artefaktu.
     */
    public IArtifact lookupArtifact(final String groupId,
                                    final String artifactId, final String artifactVersion) {
        return (IArtifact) doa.lookup(IDOA.ARTIFACTS_CONTAINER,
                new IEntityEvaluator() {

                    @Override
                    public boolean isReturnableEntity(IEntity entity) {
                        if (!(entity instanceof IArtifact)) {
                            return false;
                        }
                        IArtifact artifact = (IArtifact) entity;
                        if (artifact.getGroupId() == null
                                || artifact.getArtifactId() == null) {
                            return false;
                        }
                        String foundGroupId = artifact.getGroupId();
                        String foundArtifactId = artifact.getArtifactId();
                        String foundArtifactVersion = artifact.getVersion();
                        // pobieranie artefaktu w najnowszej wersji
                        if (artifactVersion == null) {
                            return (groupId.equals(foundGroupId) && artifactId
                                    .equals(foundArtifactId));
                        }
                        // pobieranie artefaktu w konkretnej wersji
                        return (groupId.equals(foundGroupId)
                                && artifactId.equals(foundArtifactId) && artifactVersion
                                .equals(foundArtifactVersion));
                    }

                });
    }

    private void registerClassloaderArtifact(IArtifact artifact)
            throws Exception {
        String artifactUrl =
                MessageFormat.format("doa:{0}/{1}", IDOA.ARTIFACTS_CONTAINER,
                        artifact.getName());
        log.debug(MessageFormat.format(
                "Registering Class Loader artifact: [{0}.{1}.{2}]",
                artifact.getArtifactId(), artifact.getGroupId(),
                artifact.getVersion()));
        doa.addURL(new URL(artifactUrl));

    }

    protected void initializeRepository(URL mavenPomUrl) throws Exception {
        if (chainResolver == null) {
            // inicjalizacja Ivy
            IvySettings settings = new IvySettings();
            settings.setDefaultCache(new File(getCacheDirectory()));
            chainResolver = new ChainResolver();
            chainResolver.setName("chain");
            settings.addResolver(chainResolver);
            settings.setDefaultResolver("chain");
            this.ivy = Ivy.newInstance(settings);
            EventManager eventManager = ivy.getEventManager();
            eventManager.addIvyListener(new ArtifactDeploymentListener());

            // dodawanie domyslnych resolverow
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
            chainResolver.add(fsResolver);

            // ladowanie dodatkowych repo
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
                    // collecting repository url
                    repositories.add(new URL(repoUrl));

                    log.debug(MessageFormat.format(
                            "Adding maven repository: {0} [{1}]", repoName,
                            repoUrl));

                    IBiblioResolver mavenResolver = new IBiblioResolver();
                    mavenResolver.setEventManager(eventManager);
                    mavenResolver.setSettings(settings);
                    mavenResolver.setName(repoName);
                    mavenResolver.setM2compatible(true);
                    mavenResolver.setCheckconsistency(false);
                    mavenResolver.setRoot(repoUrl);
                    mavenResolver
                            .setPattern("[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier]).[ext]");
                    chainResolver.add(mavenResolver);
                }
            }
            this.ivy.bind();
        }
        if (mavenPomUrl != null) {
            MavenXpp3Reader pomReader = new MavenXpp3Reader();
            Model pomModel =
                    pomReader.read(new URLInputStreamFacade(mavenPomUrl)
                            .getInputStream());
            List<Repository> repositories = pomModel.getRepositories();
            for (Repository repository : repositories) {
                String repoName = repository.getName();
                String repoUrl = repository.getUrl();
                // collecting repository url
                URL url = new URL(repoUrl);
                if (this.repositories.contains(url)) {
                    log.debug(MessageFormat
                            .format("Repository URL already registered: [{0}]",
                                    repoUrl));
                    continue;
                }
                this.repositories.add(new URL(repoUrl));

                log.debug(MessageFormat.format(
                        "Registering custom Ivy repository: {0} [{1}]",
                        (repoName == null) ? "" : repoName, repoUrl));

                IBiblioResolver mavenResolver = new IBiblioResolver();
                mavenResolver.setEventManager(ivy.getEventManager());
                mavenResolver.setSettings(ivy.getSettings());
                mavenResolver.setName(repoName);
                mavenResolver.setM2compatible(true);
                mavenResolver.setCheckconsistency(false);
                mavenResolver.setRoot(repoUrl);
                chainResolver.add(mavenResolver);
            }
        }

    }

     private IArtifact deployXmlArtifact(String artifactFileName,
                                        InputStream fileStream) throws GeneralDOAException {
        /*IArtifact artifact =
                (IArtifact) doa.lookupEntityByLocation(
                        IDOA.ARTIFACTS_CONTAINER + "/" + artifactFileName);
        if (artifact != null) {
            // TODO implement undeployment
        }
        ByteArrayOutputStream fileContentStream = new ByteArrayOutputStream();
        try {
            IOUtils.copy(fileStream, fileContentStream);
        } catch (IOException e1) {
            throw new GeneralDOAException(e1);
        }
        byte[] fileContent = fileContentStream.toByteArray();
        artifact = doa.createArtifact(artifactFileName, Type.XML);
        artifact.setArtifactFileName(artifactFileName);
        artifact.setArtifactResourceBytes(fileContent);
        artifact.setVersion("" + new Date().getTime());
        artifact.store(IDOA.ARTIFACTS_CONTAINER);
        try {
            // kolekcja obiektow do automatycznego uruchomienia
            List<IStartableEntity> autostartEntities =
                    new ArrayList<IStartableEntity>();

            ArtifactUtils.executeDeploymentScript(artifact, null, fileContent,
                    autostartEntities);
            fileStream.close();

            // uruchamianie wszystkich elementow, ktore sa oznaczone jako
            // autostart
            for (IStartableEntity startableEntity : autostartEntities) {
                try {
                    log.debug(MessageFormat.format("starting up entity: {0}",
                            startableEntity.getLocation()));
                    startableEntity.startup();
                    log.debug(MessageFormat.format(
                            "entity under location {0} started up ...",
                            startableEntity.getLocation()));
                } catch (Exception e) {
                    log.error("", e);
                }
            }

        } catch (Exception e) {
            log.error("", e);
            throw new GeneralDOAException(
                    "error while processing [{0}] file ...", artifactFileName);
        }
        return artifact; */
        return null;
    }

    public abstract String getDeployDirectory();

    public abstract String getCacheDirectory();

    public abstract long getMonitorInterval();

}
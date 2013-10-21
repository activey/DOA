package pl.doa.artifact.impl.maven.exclusion;

import org.apache.maven.model.Exclusion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;

public class PropertiesExclusionsReader implements IDependencyExclusionsReader {

    private static final String EXCLUSIONS_PROPERTIES = "exclusions.properties";

    private final static Logger LOG = LoggerFactory.getLogger(PropertiesExclusionsReader.class);

    @Override
    public Collection<Exclusion> readExclusions() {
        InputStream exclusionsStream = getClass().getClassLoader().getResourceAsStream(EXCLUSIONS_PROPERTIES);
        Properties exclusions = new Properties();
        try {
            LOG.debug("Reading exclusions list");
            Collection<Exclusion> exclusionsList = new ArrayList<Exclusion>();
            exclusions.load(exclusionsStream);
            Enumeration<Object> groups = exclusions.keys();
            for (Object groupObject : exclusions.keySet()) {
                String group = (String) groupObject;
                String artifactId = exclusions.getProperty(group);
                Exclusion exclusion = new Exclusion();
                exclusion.setGroupId(group);
                exclusion.setArtifactId(artifactId);
                exclusionsList.add(exclusion);
            }
            return exclusionsList;
        } catch (IOException e) {
            LOG.error("", e);
            return new ArrayList<Exclusion>();
        }
    }
}

package pl.doa.artifact.impl.maven;

import org.apache.maven.model.Model;

import java.io.File;

public interface IMavenResolver {

    public Model resolveArtifactModel(String groupId, String artifactId, String version);

    public File resolveArtifactFile(String groupId, String artifactId, String version);
}

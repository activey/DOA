package pl.doa.artifact.impl.maven;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import pl.doa.GeneralDOAException;
import pl.doa.artifact.IArtifact;

import java.io.File;

public interface IMavenResolver {

    public Model resolveArtifactModel(Dependency artifactDependency);

    public File resolveArtifactFile(Dependency artifactDependency);
}

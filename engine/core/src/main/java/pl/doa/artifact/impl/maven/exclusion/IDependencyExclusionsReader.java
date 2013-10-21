package pl.doa.artifact.impl.maven.exclusion;

import org.apache.maven.model.Exclusion;

import java.util.Collection;

public interface IDependencyExclusionsReader {

    public Collection<Exclusion> readExclusions();
}

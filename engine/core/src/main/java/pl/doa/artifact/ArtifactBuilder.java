package pl.doa.artifact;

import pl.doa.GeneralDOAException;
import pl.doa.IDOA;

import java.io.File;
import java.io.FileInputStream;

public class ArtifactBuilder {

    private final IDOA doa;
    private final IArtifact artifact;

    public static ArtifactBuilder newArtifact(IDOA doa) throws GeneralDOAException {
        return ArtifactBuilder.newArtifact(doa, "");
    }

    public static ArtifactBuilder newArtifact(IDOA doa, String name) throws GeneralDOAException {
        return new ArtifactBuilder(doa, name);
    }

    private ArtifactBuilder(IDOA doa, String name) throws GeneralDOAException {
        this.doa = doa;
        this.artifact = doa.createArtifact(name);
    }

    public ArtifactBuilder setGroupId(String groupId) {
        this.artifact.setGroupId(groupId);
        return this;
    }

    public ArtifactBuilder setArtifactId(String artifactId) {
        this.artifact.setArtifactId(artifactId);
        return this;
    }

    public ArtifactBuilder setVersion(String version) {
        this.artifact.setVersion(version);
        return this;
    }

    public ArtifactBuilder setArtifactSourceFile(File artifactFile) throws GeneralDOAException {
        artifact.setArtifactFileName(artifactFile.getName());
        try {
            artifact.setArtifactResourceStream(new FileInputStream(
                    artifactFile.getAbsolutePath()), artifactFile.length());
        } catch (Exception e) {
            throw new GeneralDOAException(e);
        }
        return this;
    }

    public ArtifactBuilder setDescription(String description) {
        this.artifact.setDescription(description);
        return this;
    }

    public ArtifactBuilder setNameFromRevision() {
        this.artifact.setName(
                String.format("%s.%s.%s", artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion()));
        return this;
    }

    public IArtifact build() throws GeneralDOAException {
        this.artifact.store(IDOA.ARTIFACTS_CONTAINER);
        return this.artifact;
    }

}

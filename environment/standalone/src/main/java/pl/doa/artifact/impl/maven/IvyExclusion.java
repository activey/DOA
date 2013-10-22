package pl.doa.artifact.impl.maven;

import org.apache.ivy.core.module.descriptor.DefaultExcludeRule;
import org.apache.ivy.core.module.descriptor.ExcludeRule;
import org.apache.ivy.core.module.id.ArtifactId;
import org.apache.ivy.plugins.matcher.PatternMatcher;
import org.apache.maven.model.Exclusion;

import java.util.Map;

public class IvyExclusion extends Exclusion {

    public IvyExclusion(ExcludeRule excludeRule) {
        ArtifactId id = excludeRule.getId();
        setArtifactId(id.getModuleId().getName());
        setGroupId(id.getModuleId().getOrganisation());
    }
}

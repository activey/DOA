package pl.doa.jvm.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.IDOA;
import pl.doa.artifact.IArtifact;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.jvm.DOAClassLoader;

import static pl.doa.entity.evaluator.EntityArtifactDependenciesEvaluator.withArtifactDependencies;

public class ObjectFactory {

	private final static Logger LOG = LoggerFactory
			.getLogger(ObjectFactory.class);

	public static <T extends Object> T instantiateObject(IDOA doa,
			String className, ClassLoader parentLoader, IEntityEvaluator dependenciesEvaluator) {
		ClassLoader loader = new DOAClassLoader(doa, parentLoader).loadArtifacts(dependenciesEvaluator);
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className, true, loader);
			return (T) clazz.newInstance();
		} catch (Exception e) {
			LOG.error("", e);
			return null;
		}
	}

    public static <T extends Object> T instantiateObject(IDOA doa,
            String className, IEntityEvaluator dependenciesEvaluator) {
        return instantiateObject(doa, className, null, dependenciesEvaluator);
    }

	// instantiateObjectWithArtifactDependencies

	public static <T extends Object> T instantiateObjectWithArtifactDependencies(
			IDOA doa, String className, IArtifact artifactWithDependencies) {
        if (className == null) {
            return null;
        }
        DOAClassLoader loader = new DOAClassLoader(doa, ObjectFactory.class.getClassLoader());
        if (artifactWithDependencies != null) {
            loader.loadArtifacts(withArtifactDependencies(artifactWithDependencies));
        }
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className, true, loader);
			return (T) clazz.newInstance();
		} catch (Exception e) {
			LOG.error("", e);
			return null;
		}
	}

	public static <T extends Object> T instantiateObject(IDOA doa,
			String className) {
		return instantiateObject(doa, className, null, null);
	}
}

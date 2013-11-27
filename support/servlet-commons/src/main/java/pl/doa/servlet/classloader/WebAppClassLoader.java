package pl.doa.servlet.classloader;

import pl.doa.IDOA;
import pl.doa.document.IDocument;
import pl.doa.jvm.DOAClassLoader;

import static pl.doa.entity.evaluator.CompoundEvaluator.forAll;
import static pl.doa.entity.evaluator.EntityArtifactDependenciesEvaluator.withEntityDependencies;

public class WebAppClassLoader extends DOAClassLoader {

    public WebAppClassLoader(IDOA doa, IDocument applicationDocument, ClassLoader parent) {
        super(doa, parent);
        loadArtifacts(forAll(withEntityDependencies(applicationDocument), new HttpExcludedEvaluator()));
    }
}

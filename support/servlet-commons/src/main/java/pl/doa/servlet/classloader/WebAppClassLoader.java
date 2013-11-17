package pl.doa.servlet.classloader;

import pl.doa.IDOA;
import pl.doa.jvm.DOAClassLoader;

public class WebAppClassLoader extends DOAClassLoader {

    public WebAppClassLoader(IDOA doa, ClassLoader parent) {
        super(doa, parent);
        loadArtifacts(new HttpExcludedEvaluator());
    }
}

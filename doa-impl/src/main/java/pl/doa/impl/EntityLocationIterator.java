/**
 *
 */
package pl.doa.impl;

/**
 * @author activey
 */
public class EntityLocationIterator extends AbstractPathIterator {


    public EntityLocationIterator(String path) {
        super(path);
    }

    public EntityLocationIterator(String path, boolean backwards) {
        super(path, backwards);
    }

    @Override
    protected String getPathSeparator() {
        return "/";
    }


}
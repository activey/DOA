/**
 *
 */
package pl.doa.impl;

import java.io.Serializable;

/**
 * @author activey
 */
public class EntityLocationIterator extends AbstractPathIterator implements Serializable {

    public EntityLocationIterator() {

    }

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
/**
 *
 */
package pl.doa.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author activey
 */
public class IteratorCollection<T> implements Collection<T> {

    private List<T> innerList = null;

    public IteratorCollection(Iterator<T> iterator) {
        this.innerList = new ArrayList<T>();
        if (iterator == null) {
            return;
        }
        IteratorIterable<T> iterable = new IteratorIterable<T>(iterator);
        for (T element : iterable) {
            innerList.add(element);
        }
    }

    @Override
    public boolean add(T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends T> arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object otherObject) {
        return innerList.contains(otherObject);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return innerList.containsAll(collection);
    }

    @Override
    public boolean isEmpty() {
        return innerList.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return innerList.iterator();
    }

    @Override
    public boolean remove(Object arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return innerList.size();
    }

    @Override
    public Object[] toArray() {
        return innerList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return innerList.toArray(a);
    }

}

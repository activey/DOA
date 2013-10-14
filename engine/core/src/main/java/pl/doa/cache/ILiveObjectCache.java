package pl.doa.cache;

/**
 * @author activey
 * @date: 10.10.13 12:49
 */
public interface ILiveObjectCache<T> {

    public T getObject(long id);

    public void setObject(T object, long id);

    public T removeObject(long id);

    public boolean hasObject(long id);
}

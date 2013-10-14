package pl.doa.cache.impl;

import pl.doa.cache.ILiveObjectCache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author activey
 * @date 10.10.13 12:50
 */
public class SimpleObjectCache<T> implements ILiveObjectCache<T> {

    private final Map<Long, T> cacheMap;

    public SimpleObjectCache() {
        this.cacheMap = new ConcurrentHashMap<Long, T>();
    }

    @Override
    public T getObject(long id) {
        return cacheMap.get(id);
    }

    @Override
    public void setObject(T object, long id) {
        cacheMap.put(id, object);
    }

    @Override
    public T removeObject(long id) {
        return cacheMap.remove(id);
    }

    @Override
    public boolean hasObject(long id) {
        return cacheMap.containsKey(id);
    }
}

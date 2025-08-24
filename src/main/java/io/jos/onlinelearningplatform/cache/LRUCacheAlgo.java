
package io.jos.onlinelearningplatform.cache;

import java.util.Map;

public class LRUCacheAlgo<K,V> implements CacheAlgo<K,V> {

    private final int capacity;
    private final Map<K, V> cache;

    public LRUCacheAlgo(int capacity) {
        this.capacity = capacity;
        this.cache = new java.util.LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
                return size() > LRUCacheAlgo.this.capacity;
            }
        };
    }
    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }
    @Override
    public V get(K key) {
        return cache.get(key);
    }
    @Override
    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }
    @Override
    public int size() {
        return cache.size();
    }
}
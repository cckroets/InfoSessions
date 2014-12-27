package com.sixbynine.infosessions.data;

/**
 * @author curtiskroetsch
 */
public interface Cache<K, V> {

    void put(K key, V value);

    V get(K key);

    void clear();
}

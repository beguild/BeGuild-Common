package dev.frydae.beguild.utils.java;

import java.util.HashMap;
import java.util.Map;

public class CaselessHashMap<T> extends HashMap<String, T> {
    public CaselessHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public CaselessHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public CaselessHashMap() {
    }

    public CaselessHashMap(Map<? extends String, ? extends T> m) {
        super(m);
    }

    @Override
    public T put(String key, T value) {
        return super.put(key.toLowerCase(), value);
    }

    @Override
    public T get(Object key) {
        return super.get(((String) key).toLowerCase());
    }

    @Override
    public void putAll(Map<? extends String, ? extends T> m) {
        m.forEach(this::put);
    }

    @Override
    public T remove(Object key) {
        return super.remove(((String) key).toLowerCase());
    }

    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(((String) key).toLowerCase());
    }
}

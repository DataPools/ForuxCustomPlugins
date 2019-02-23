package com.exloki.balancedfly.core.collections;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class StringKeyMap<V> implements Map<String, V> {

    private Map<String, V> backingMap;

    public StringKeyMap(Map<String, V> backingMap) {
        if(backingMap == null) {
            throw new IllegalStateException("backingMap cannot be null");
        }
        this.backingMap = backingMap;
    }

    @Override
    public int size() {
        return backingMap.size();
    }

    @Override
    public boolean isEmpty() {
        return backingMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if(key == null) return false;
        String strVal = key.toString().toLowerCase();
        return backingMap.containsKey(strVal);
    }

    @Override
    public boolean containsValue(Object value) {
        return backingMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        if(key == null) return null;
        String strVal = key.toString().toLowerCase();
        return backingMap.get(strVal);
    }

    @Override
    public V put(String key, V value) {
        if(key == null) return null;
        key = key.toLowerCase();
        return backingMap.put(key, value);
    }

    @Override
    public V remove(Object key) {
        if(key == null) return null;
        String strVal = key.toString().toLowerCase();
        return backingMap.remove(strVal);
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        for (Entry<? extends String, ? extends V> entry : m.entrySet()) {
            if(entry.getKey() == null) continue;
            String strVal = entry.getKey().toLowerCase();
            backingMap.put(strVal, entry.getValue());
        }
    }

    @Override
    public void clear() {
        backingMap.clear();
    }

    @Override
    public Set<String> keySet() {
        return backingMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return backingMap.values();
    }

    @Override
    public Set<Entry<String, V>> entrySet() {
        return backingMap.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return o == this || backingMap.equals(o);
    }

    @Override
    public int hashCode() {
        return backingMap.hashCode();
    }
}

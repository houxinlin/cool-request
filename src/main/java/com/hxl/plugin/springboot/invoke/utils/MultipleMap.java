package com.hxl.plugin.springboot.invoke.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MultipleMap<K, V, VV> {
    private Map<K, Value<V, VV>> map = new HashMap<>();

    public int size() {
        return map.size();
    }

    public Set<K> keySet() {
        return map.keySet();
    }

    public void clear() {
        map.clear();
    }

    public void put(K key, V v1, VV v2) {
        map.put(key, new Value(v1, v2));
    }

    public void setAllSecondValue(VV value) {
        for (K k : map.keySet()) {
            map.get(k).setV2(value);
        }
    }

    public void setSecondValue(K key, VV value) {
        if (map.containsKey(key)) {
            map.get(key).setV2(value);
        }
    }

    public V getFirstValue(K key) {
        if (!map.containsKey(key)) return null;
        return map.get(key).getV1();
    }

    public VV getSecondValue(K key) {
        if (!map.containsKey(key)) return null;
        return map.get(key).getV2();
    }

    class Value<V, VV> {
        private V v1;
        private VV v2;

        public Value(V v1, VV v2) {
            this.v1 = v1;
            this.v2 = v2;
        }

        public V getV1() {
            return v1;
        }

        public void setV1(V v1) {
            this.v1 = v1;
        }

        public VV getV2() {
            return v2;
        }

        public void setV2(VV v2) {
            this.v2 = v2;
        }
    }
}

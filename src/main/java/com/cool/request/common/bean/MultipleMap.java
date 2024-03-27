/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * MultipleMap.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.common.bean;

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

    public void remove(K key) {
        map.remove(key);
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
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

/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * CollectionUtils.java is part of Cool Request
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

package com.cool.request.utils;

import com.cool.request.utils.url.MultiValueMap;
import com.cool.request.utils.url.MultiValueMapAdapter;

import java.util.*;

public class CollectionUtils {
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * Return {@code true} if the supplied Map is {@code null} or empty.
     * Otherwise, return {@code false}.
     *
     * @param map the Map to check
     * @return whether the given Map is empty
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }


    public static <K, V> MultiValueMap<K, V> unmodifiableMultiValueMap(
            MultiValueMap<? extends K, ? extends V> targetMap) {

        Map<K, List<V>> result = newLinkedHashMap(targetMap.size());
        targetMap.forEach((key, value) -> {
            List<? extends V> values = Collections.unmodifiableList(value);
            result.put(key, (List<V>) values);
        });
        Map<K, List<V>> unmodifiableMap = Collections.unmodifiableMap(result);
        return toMultiValueMap(unmodifiableMap);
    }

    public static <K, V> MultiValueMap<K, V> toMultiValueMap(Map<K, List<V>> targetMap) {
        return new MultiValueMapAdapter<>(targetMap);
    }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(int expectedSize) {
        return new LinkedHashMap<>(computeMapInitialCapacity(expectedSize), DEFAULT_LOAD_FACTOR);
    }

    private static int computeMapInitialCapacity(int expectedSize) {
        return (int) Math.ceil(expectedSize / (double) DEFAULT_LOAD_FACTOR);
    }

    /**
     * 合并元素，并且重复元素优先第一个集合
     */
    public static <T> List<T> uniqueMerge(List<T> t1, List<T> t2) {
        if (t1 == null || t1.isEmpty()) {
            if (t2 != null) return t2;
            return new ArrayList<>();
        }
        if (t2 == null || t2.isEmpty()) {
            return t1;
        }

        List<T> result = new ArrayList<>(t1);
        for (T t : t2) {
            if (!result.contains(t)) result.add(t);
        }
        return result;
    }

    /**
     * 合并元素，即使元素相同
     */
    public static <T> List<T> merge(List<T> t1, List<T> t2) {
        if (t1 == null || t1.isEmpty()) {
            if (t2 != null) return t2;
            return new ArrayList<>();
        }
        if (t2 == null || t2.isEmpty()) {
            return t1;
        }

        List<T> result = new ArrayList<>(t1);
        result.addAll(t2);
        return result;
    }
}

/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * MultiValueMap.java is part of Cool Request
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

package com.cool.request.utils.url;

import java.util.List;
import java.util.Map;


/**
 * Extension of the {@code Map} interface that stores multiple values.
 *
 * @author Arjen Poutsma
 * @since 3.0
 * @param <K> the key type
 * @param <V> the value element type
 */
public interface MultiValueMap<K, V> extends Map<K, List<V>> {

    /**
     * Add the given single value to the current list of values for the given key.
     * @param key the key
     * @param value the value to be added
     */
    void add(K key,  V value);

    /**
     * Add all the values of the given list to the current list of values for the given key.
     * @param key they key
     * @param values the values to be added
     * @since 5.0
     */
    void addAll(K key, List<? extends V> values);

    /**
     * Add all the values of the given {@code MultiValueMap} to the current values.
     * @param values the values to be added
     * @since 5.0
     */
    void addAll(MultiValueMap<K, V> values);

    /**
     * Set the given single value under the given key.
     * @param key the key
     * @param value the value to set
     */
    void set(K key,  V value);

    /**
     * Set the given values under.
     * @param values the values.
     */
    void setAll(Map<K, V> values);

}

/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ArgumentHolder.java is part of Cool Request
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

package com.cool.request.lib.curl;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public interface ArgumentHolder {

    String getName();

    Map<String, String> getMetadata();

    default String getContentType() {
        return getMetadata().get("type");
    }

    default boolean hasContenType() {
        return getMetadata().containsKey("type");
    }

    static Pair<String, Map<String, String>> parse(String name) {
        if (name.contains(";")) {
            String[] parts = name.split(";");
            String realName = parts[0];
            Map<String, String> metadata = new HashMap<>();
            for (int i = 1; i< parts.length; i++) {
                String[] typeParts = parts[i].split("\\s*=\\s*", 2);
                metadata.put(typeParts[0].toLowerCase(Locale.US), typeParts[1]);
            }
            return Pair.of(realName, metadata);
        } else {
            return Pair.of(name, Collections.emptyMap());
        }
    }
}
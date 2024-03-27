/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * FileArgumentHolder.java is part of Cool Request
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
import java.util.Map;
import java.util.Objects;

public class FileArgumentHolder implements ArgumentHolder {
    private final String name;
    private final Map<String, String> metadata;

    private FileArgumentHolder(String name, Map<String, String> metadata) {
        this.name = name;
        this.metadata = metadata;
    }

    public static FileArgumentHolder of(String name) {
        if (name == null) {
            return new FileArgumentHolder("", Collections.emptyMap());
        }
        Pair<String, Map<String, String>> parsed = ArgumentHolder.parse(name);
        return new FileArgumentHolder(parsed.getLeft(), parsed.getRight());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FileArgumentHolder other = (FileArgumentHolder) obj;
        return Objects.equals(name, other.name);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "FileArgumentHolder(" + name + ", " + metadata + ")";
    }

    @Override
    public Map<String, String> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }
}
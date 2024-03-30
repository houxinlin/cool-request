/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ServerVariables.java is part of Cool Request
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

package com.cool.request.lib.openapi;


import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * ServerVariables
 *
 * @see "https://github.com/OAI/OpenAPI-Specification/blob/3.0.1/versions/3.0.1.md#serverVariablesObject"
 */

public class ServerVariables extends LinkedHashMap<String, ServerVariable> {
    public ServerVariables() {
    }

    private java.util.Map<String, Object> extensions = null;

    public ServerVariables addServerVariable(String name, ServerVariable item) {
        this.put(name, item);
        return this;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServerVariables serverVariables = (ServerVariables) o;
        return Objects.equals(this.extensions, serverVariables.extensions) &&
                super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(extensions, super.hashCode());
    }

    /**
     * @deprecated As extensions don't make sense at this level
     */
    @Deprecated
    public java.util.Map<String, Object> getExtensions() {
        return extensions;
    }

    /**
     * @deprecated As extensions don't make sense at this level
     */
    @Deprecated
    public void addExtension(String name, Object value) {
        if (name == null || name.isEmpty() || !name.startsWith("x-")) {
            return;
        }
        if (this.extensions == null) {
            this.extensions = new java.util.LinkedHashMap<>();
        }
        this.extensions.put(name, value);
    }

    /**
     * @deprecated As extensions don't make sense at this level
     */
    @Deprecated
    public void setExtensions(java.util.Map<String, Object> extensions) {
        this.extensions = extensions;
    }

    /**
     * @deprecated As extensions don't make sense at this level
     */
    @Deprecated
    public ServerVariables extensions(java.util.Map<String, Object> extensions) {
        this.extensions = extensions;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ServerVariables {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}
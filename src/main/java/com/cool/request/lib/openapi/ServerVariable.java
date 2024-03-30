/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ServerVariable.java is part of Cool Request
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


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ServerVariable
 *
 * @see "https://github.com/OAI/OpenAPI-Specification/blob/3.0.1/versions/3.0.1.md#serverVariableObject"
 * @see "https://github.com/OAI/OpenAPI-Specification/blob/3.1.0/versions/3.1.0.md#serverVariableObject"
 */

public class ServerVariable {
    private List<String> _enum = null;
    private String _default = null;
    private String description = null;
    private java.util.Map<String, Object> extensions = null;

    /**
     * returns the _enum property from a ServerVariable instance.
     *
     * @return List&lt;String&gt; _enum
     **/

    public List<String> getEnum() {
        return _enum;
    }

    public void setEnum(List<String> _enum) {
        this._enum = _enum;
    }

    public ServerVariable _enum(List<String> _enum) {
        this._enum = _enum;
        return this;
    }

    public ServerVariable addEnumItem(String _enumItem) {
        if (this._enum == null) {
            this._enum = new ArrayList<>();
        }
        this._enum.add(_enumItem);
        return this;
    }

    /**
     * returns the _default property from a ServerVariable instance.
     *
     * @return String _default
     **/

    public String getDefault() {
        return _default;
    }

    public void setDefault(String _default) {
        this._default = _default;
    }

    public ServerVariable _default(String _default) {
        this._default = _default;
        return this;
    }

    /**
     * returns the description property from a ServerVariable instance.
     *
     * @return String description
     **/

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ServerVariable description(String description) {
        this.description = description;
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
        ServerVariable serverVariable = (ServerVariable) o;
        return Objects.equals(this._enum, serverVariable._enum) &&
                Objects.equals(this._default, serverVariable._default) &&
                Objects.equals(this.description, serverVariable.description) &&
                Objects.equals(this.extensions, serverVariable.extensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_enum, _default, description, extensions);
    }

    public java.util.Map<String, Object> getExtensions() {
        return extensions;
    }

    public void addExtension(String name, Object value) {
        if (name == null || name.isEmpty() || !name.startsWith("x-")) {
            return;
        }
        if (this.extensions == null) {
            this.extensions = new java.util.LinkedHashMap<>();
        }
        this.extensions.put(name, value);
    }

    
    public void addExtension31(String name, Object value) {
        if (name != null && (name.startsWith("x-oas-") || name.startsWith("x-oai-"))) {
            return;
        }
        addExtension(name, value);
    }

    public void setExtensions(java.util.Map<String, Object> extensions) {
        this.extensions = extensions;
    }

    public ServerVariable extensions(java.util.Map<String, Object> extensions) {
        this.extensions = extensions;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ServerVariable {\n");

        sb.append("    _enum: ").append(toIndentedString(_enum)).append("\n");
        sb.append("    _default: ").append(toIndentedString(_default)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
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

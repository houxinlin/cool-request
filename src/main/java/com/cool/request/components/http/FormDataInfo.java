/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * FormDataInfo.java is part of Cool Request
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

package com.cool.request.components.http;

import java.io.Serializable;
import java.util.Objects;

public class FormDataInfo extends RequestParameterDescription implements Cloneable, Serializable {
    private static final long serialVersionUID = 1000000;
    private String value;

    public FormDataInfo(String name, String value, String type) {
        super(name, type, "");
        this.value = value;
    }

    public FormDataInfo() {
        super("", "", "");
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        FormDataInfo that = (FormDataInfo) object;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public FormDataInfo clone() {
        return new FormDataInfo(getName(), getValue(), getType());
    }
}

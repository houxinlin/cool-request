/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * KeyValue.java is part of Cool Request
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

public class KeyValue implements Cloneable, Serializable {
    private static final long serialVersionUID = 1000000;
    private String key;
    private String value;
    private String valueType = "string";
    private String describe = "";

    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public KeyValue(String key, String value, String valueType) {
        this.key = key;
        this.value = value;
        this.valueType = valueType;
    }

    public KeyValue(String key, String value, String valueType, String describe) {
        this.key = key;
        this.value = value;
        this.valueType = valueType;
        this.describe = describe;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public KeyValue() {
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public KeyValue clone() {
        return new KeyValue(getKey(), getValue());
    }
}

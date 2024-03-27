/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * CommunicationPackage.java is part of Cool Request
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

package com.cool.request.common.model.pack;

import com.cool.request.common.model.Model;
import com.cool.request.utils.GsonUtils;

public abstract class CommunicationPackage {
    private final Model data;

    public CommunicationPackage(Model model) {
        model.setType(getType());
        this.data = model;
    }

    public abstract String getType();

    public String toJson() {
        if (data == null) {
            return "{}";
        }
        return toJson(data);
    }

    public String toJson(Object o) {
        return GsonUtils.toJsonString(o);
    }
}

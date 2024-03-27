/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ExceptionInvokeResponseModel.java is part of Cool Request
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

import com.cool.request.components.http.response.InvokeResponseModel;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Optional;

public class ExceptionInvokeResponseModel extends InvokeResponseModel implements Serializable {
    private static final long serialVersionUID = 1000000;

    public ExceptionInvokeResponseModel(String id, Exception e) {
        setBaseBodyData(Base64.getEncoder().encodeToString(Optional.ofNullable(e.getMessage()).orElse("").getBytes(StandardCharsets.UTF_8)));
        setHeader(new ArrayList<>());
        setId(id);
    }
}

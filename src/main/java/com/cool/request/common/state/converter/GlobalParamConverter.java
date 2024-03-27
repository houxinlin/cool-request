/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * GlobalParamConverter.java is part of Cool Request
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

package com.cool.request.common.state.converter;

import com.cool.request.common.state.GlobalParamPersistent;
import com.google.gson.Gson;
import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GlobalParamConverter extends Converter<GlobalParamPersistent.GlobalParam> {
    @Override
    public @Nullable GlobalParamPersistent.GlobalParam  fromString(@NotNull String value) {
        return new Gson().fromJson(value, GlobalParamPersistent.GlobalParam.class);
    }

    @Override
    public @Nullable String toString(GlobalParamPersistent.@NotNull GlobalParam value) {
        return new Gson().toJson(value);
    }
}

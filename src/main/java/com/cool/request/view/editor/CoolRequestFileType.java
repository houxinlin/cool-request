/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * CoolRequestFileType.java is part of Cool Request
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

package com.cool.request.view.editor;

import com.cool.request.components.http.Controller;
import com.cool.request.utils.HttpMethodIconUtils;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CoolRequestFileType implements FileType {
    private final Controller controller;

    public CoolRequestFileType(Controller controller) {
        this.controller = controller;
    }

    @Override
    public @NonNls @NotNull String getName() {
        return FileTypes.UNKNOWN.getName();
    }

    @Override
    public @NotNull String getDescription() {
        return "coolrequest";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "coolrequest";
    }

    @Override
    public Icon getIcon() {
        return HttpMethodIconUtils.getIconByHttpMethod(controller.getHttpMethod());
    }

    @Override
    public boolean isBinary() {
        return true;
    }

    @Override
    public @NonNls @Nullable String getCharset(@NotNull VirtualFile file, byte @NotNull [] content) {
        return null;
    }
}

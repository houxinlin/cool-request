/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * FileUtils.java is part of Cool Request
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

package com.cool.request.utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {
    public static final int BUFFER_SIZE = 8192;

    public static void writeString(Path path, String value) throws IOException {
        createParentDir(path);
        Files.writeString(path, value, StandardCharsets.UTF_8);

    }

    private static void createParentDir(Path path) throws IOException {
        if (!Files.exists(path.getParent())) {
            Files.createDirectory(path.getParent());
        }
    }

    public static int copy(@NotNull InputStream inputStream, @NotNull OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int read;
        int total = 0;
        while ((read = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, read);
            total += read;
        }
        return total;
    }

    public static boolean writeFile(String target, byte[] content) {
        Path path = Paths.get(target);
        if (Files.notExists(path.getParent())) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException ignored) {
            }
        }
        try {
            Files.write(path, content);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean writeFile(String target, String content) {
        if (content == null) return false;
        return writeFile(target, content.getBytes());
    }

    public static String readFile(String path) {
        try {
            return Files.readString(Paths.get(path));
        } catch (IOException e) {
            return null;
        }
    }

    public static void deleteIfExists(Path path) throws IOException {
        Files.deleteIfExists(path);
    }
}

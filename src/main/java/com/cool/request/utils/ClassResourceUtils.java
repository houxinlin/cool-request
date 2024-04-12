/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ClassResourceUtils.java is part of Cool Request
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

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClassResourceUtils {
    public static boolean exist(String name) {
        URL resource = ClassResourceUtils.class.getResource(name);
        return resource != null;
    }

    public static List<String> readLines(String name) {
        List<String> result = new ArrayList<>();
        URL resource = ClassResourceUtils.class.getResource(name);
        try (BufferedReader inputStream = new BufferedReader(new InputStreamReader(resource.openStream()))) {
            String temp;
            while ((temp = inputStream.readLine()) != null) {
                result.add(temp);
            }
        } catch (IOException ignored) {
        }
        return result;
    }

    public static byte[] read(String name) {
        URL resource = ClassResourceUtils.class.getResource(name);
        try (InputStream inputStream = resource.openStream()) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[4096];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        } catch (IOException ignored) {
        }
        return null;
    }
}

/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * JavaCompilerUtils.java is part of Cool Request
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

package com.cool.request.components.http.script;

import com.cool.request.common.constant.CoolRequestConfigConstant;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class JavaCompilerUtils {

    public static JavaCompiler getSystemJavaCompiler() {


        try {
            JavaCompiler systemJavaCompiler = ToolProvider.getSystemJavaCompiler();
            if (systemJavaCompiler != null) return systemJavaCompiler;
            File file = CoolRequestConfigConstant.CONFIG_JAVAC_PATH.toFile();
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{file.toURI().toURL()});
            Class<?> javaCompilerClass = urlClassLoader.loadClass("javax.tools.JavaCompiler");
            Class<?> javacToolClass = Class.forName("com.sun.tools.javac.api.JavacTool", true, urlClassLoader);
            Class<?> subclass = javacToolClass.asSubclass(javaCompilerClass);
            return (JavaCompiler) subclass.getConstructor().newInstance();
        } catch (Exception ignored) {
        }
        return null;
    }
}

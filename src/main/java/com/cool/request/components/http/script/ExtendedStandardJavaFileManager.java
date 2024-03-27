/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ExtendedStandardJavaFileManager.java is part of Cool Request
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

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExtendedStandardJavaFileManager extends
        ForwardingJavaFileManager<JavaFileManager> {
    private final List<CompiledCode> compiledCode = new ArrayList<CompiledCode>();
    private final DynamicClassLoader cl;

    protected ExtendedStandardJavaFileManager(JavaFileManager fileManager,
                                              DynamicClassLoader cl) {
        super(fileManager);
        this.cl = cl;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(
            Location location, String className,
            JavaFileObject.Kind kind, FileObject sibling) throws IOException {

        try {
            CompiledCode innerClass = new CompiledCode(className);
            compiledCode.add(innerClass);
            cl.addCode(innerClass);
            return innerClass;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error while creating in-memory output file for "
                            + className, e);
        }
    }

    @Override
    public JavaFileObject getJavaFileForInput(Location location, String className, JavaFileObject.Kind kind) throws IOException {
        return super.getJavaFileForInput(location, className, kind);
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        return cl;
    }
}

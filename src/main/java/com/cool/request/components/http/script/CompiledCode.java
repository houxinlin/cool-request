/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * CompiledCode.java is part of Cool Request
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

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class CompiledCode extends SimpleJavaFileObject {
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private final String className;

    public CompiledCode(String className) throws Exception {
        super(new URI(className), Kind.CLASS);
        this.className = className;
    }
    
    public String getClassName() {
		return className;
	}

    @Override
    public OutputStream openOutputStream() throws IOException {
        return baos;
    }
    public byte[] getByteCode() {
        return baos.toByteArray();
    }
}

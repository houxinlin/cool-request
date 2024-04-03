/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * PropertiesReader.java is part of Cool Request
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

package com.cool.request.lib.springmvc.config.reader;

import com.cool.request.lib.springmvc.config.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

public class PropertiesReader {
    public int readServerPort(Project project, Module module) {
        UserProjectConfigReaderBuilder<String> userProjectConfigReaderBuilder = new UserProjectConfigReaderBuilder()
                .addReader(new PropertiesUserProjectReader(project, module))
                .addReader(new YamlUserProjectReader(project, module))
                .addReader(new DefaultValueReader("8080"));
        String read = userProjectConfigReaderBuilder.read(SpringKey.KEY_SERVER_PORT_NAME);
        return Integer.parseInt(read);
    }

    public String readContextPath(Project project, Module module) {
        UserProjectConfigReaderBuilder<String> userProjectConfigReaderBuilder = new UserProjectConfigReaderBuilder()
                .addReader(new PropertiesUserProjectReader(project, module))
                .addReader(new YamlUserProjectReader(project, module))
                .addReader(new DefaultValueReader(""));
        return userProjectConfigReaderBuilder.read(SpringKey.KEY_CONTEXT_PATH);
    }

    public String readCustomAsString(String key, Project project, Module module) {
        UserProjectConfigReaderBuilder<String> userProjectConfigReaderBuilder = new UserProjectConfigReaderBuilder()
                .addReader(new PropertiesUserProjectReader(project, module))
                .addReader(new YamlUserProjectReader(project, module))
                .addReader(new DefaultValueReader(""));
        return userProjectConfigReaderBuilder.read(key);
    }
}

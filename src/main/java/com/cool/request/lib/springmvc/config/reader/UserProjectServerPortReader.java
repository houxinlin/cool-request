/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * UserProjectServerPortReader.java is part of Cool Request
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

public class UserProjectServerPortReader implements UserProjectReader<Integer> {
    private static final int DEFAULT_PORT = 8080;
    private final Project project;
    private final Module module;

    public UserProjectServerPortReader(Project project, Module module) {
        this.project = project;
        this.module = module;
    }

    @Override
    public Integer read() {
        UserProjectConfigReaderBuilder<Integer> userProjectConfigReaderBuilder = new UserProjectConfigReaderBuilder()
                .addReader(new PropertiesUserProjectServerPortReader(project, module))
                .addReader(new YamlUserProjectServerPortReader(project, module))
                .addReader(new DefaultValueReader(DEFAULT_PORT));
        return userProjectConfigReaderBuilder.read();
    }
}

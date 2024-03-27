/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * YamlUserProjectServerPortReader.java is part of Cool Request
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

package com.cool.request.lib.springmvc.config;

import com.cool.request.lib.springmvc.config.base.BaseYamlUserProjectConfigReader;
import com.cool.request.utils.StringUtils;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

public class YamlUserProjectServerPortReader extends BaseYamlUserProjectConfigReader<Integer> {
    public YamlUserProjectServerPortReader(Project project, Module module) {
        super(project, module);
    }

    @Override
    public Integer read() {
        String value = doRead("application.yaml", SpringKey.KEY_NAME, false);
        if (!StringUtils.isEmpty(value)) return Integer.valueOf(value);

        value = doRead("application.yml", SpringKey.KEY_NAME, false);
        if (!StringUtils.isEmpty(value)) return Integer.valueOf(value);

        value = doRead("bootstrap.yaml", SpringKey.KEY_NAME, false);
        if (value != null) {
            return Integer.valueOf(value);
        }
        return null;
    }
}

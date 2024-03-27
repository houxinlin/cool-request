/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * BasePropertiesUserProjectConfigReader.java is part of Cool Request
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

package com.cool.request.lib.springmvc.config.base;

import com.cool.request.lib.springmvc.config.UserProjectReader;
import com.cool.request.utils.PsiUtils;
import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.cool.request.lib.springmvc.config.SpringKey.KEY_ACTIVE;

public abstract class BasePropertiesUserProjectConfigReader<T> implements UserProjectReader<T> {

    private Project project;
    private Module module;

    public BasePropertiesUserProjectConfigReader(Project project, Module module) {
        this.project = project;
        this.module = module;
    }

    private String generatorPropertiesFileName(String active) {
        return "application-" + active + ".properties";
    }

    protected String doRead(String name, String key, boolean last) {
        PsiFile[] propertiesFiles = PsiUtils.getUserProjectFile(name, project, module);
        //找不到目标文件
        if (propertiesFiles.length == 0) {
            return null;
        }
        List<PsiFile> properties = Arrays.asList(propertiesFiles)
                .stream()
                .filter(psiFile -> psiFile.getParent().getName().endsWith("resources") && psiFile instanceof PropertiesFile).collect(Collectors.toList());
        //目标文件不符合
        if (properties.isEmpty()) {
            return null;
        }

        PropertiesFile propertiesFile = (PropertiesFile) properties.get(0);
        String active = getPropertiesValue(propertiesFile, KEY_ACTIVE);
        //优先active里面的,可能产生递归
        if (StringUtil.isNotEmpty(active) && !last) {
            String newFile = generatorPropertiesFileName(active);
            String value = doRead(newFile, key, true);
            if (value != null) {
                return value;
            }
        }
        return getPropertiesValue(propertiesFile, key);
    }

    private static String getPropertiesValue(@NotNull PropertiesFile psiFile, @NotNull String propName) {
        return psiFile.getNamesMap().get(propName);
    }
}

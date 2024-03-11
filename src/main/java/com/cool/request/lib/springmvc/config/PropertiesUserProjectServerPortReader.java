package com.cool.request.lib.springmvc.config;

import com.cool.request.lib.springmvc.config.base.BasePropertiesUserProjectConfigReader;
import com.cool.request.utils.StringUtils;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

public class PropertiesUserProjectServerPortReader extends BasePropertiesUserProjectConfigReader<Integer> {
    public PropertiesUserProjectServerPortReader(Project project, Module module) {
        super(project, module);
    }

    @Override
    public Integer read() {
        String value = doRead("application.properties", SpringKey.KEY_NAME, false);
        if (!StringUtils.isEmpty(value)) Integer.valueOf(value);
        value = doRead("bootstrap.properties", SpringKey.KEY_NAME, false);
        if (value != null) {
            return Integer.valueOf(value);
        }
        return null;
    }

}

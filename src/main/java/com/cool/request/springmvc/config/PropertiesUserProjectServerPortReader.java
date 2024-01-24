package com.cool.request.springmvc.config;

import com.cool.request.springmvc.config.base.BasePropertiesUserProjectConfigReader;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

public class PropertiesUserProjectServerPortReader extends BasePropertiesUserProjectConfigReader<Integer> {
    public PropertiesUserProjectServerPortReader(Project project, Module module) {
        super(project, module);
    }

    @Override
    public Integer read() {
        String value = doRead("application.properties", SpringKey.KEY_NAME,false);
        if (value != null) {
            return Integer.valueOf(value);
        }
        return null;
    }

}

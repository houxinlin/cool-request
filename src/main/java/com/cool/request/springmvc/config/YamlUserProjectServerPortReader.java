package com.cool.request.springmvc.config;

import com.cool.request.springmvc.config.base.BaseYamlUserProjectConfigReader;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

public class YamlUserProjectServerPortReader extends BaseYamlUserProjectConfigReader<Integer> {
    public YamlUserProjectServerPortReader(Project project, Module module) {
        super(project, module);
    }

    @Override
    public Integer read() {
        String value = doRead("application.yaml", SpringKey.KEY_NAME, false);
        if (value != null) {
            return Integer.valueOf(value);
        }
        return null;
    }
}

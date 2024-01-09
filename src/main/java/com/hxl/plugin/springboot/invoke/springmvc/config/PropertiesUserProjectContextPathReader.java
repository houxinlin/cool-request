package com.hxl.plugin.springboot.invoke.springmvc.config;

import com.hxl.plugin.springboot.invoke.springmvc.config.base.BasePropertiesUserProjectConfigReader;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

public class PropertiesUserProjectContextPathReader extends BasePropertiesUserProjectConfigReader<String> {
    public PropertiesUserProjectContextPathReader(Project project, Module module) {
        super(project, module);
    }

    @Override
    public String read() {
        String value = doRead("application.properties", SpringKey.KEY_CONTEXT_PATH, false);
        return value;
    }
}

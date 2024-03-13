package com.cool.request.lib.springmvc.config;

import com.cool.request.lib.springmvc.config.base.BasePropertiesUserProjectConfigReader;
import com.cool.request.utils.StringUtils;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

public class PropertiesUserProjectContextPathReader extends BasePropertiesUserProjectConfigReader<String> {
    public PropertiesUserProjectContextPathReader(Project project, Module module) {
        super(project, module);
    }

    @Override
    public String read() {
        String contentPath = doRead("application.properties", SpringKey.KEY_CONTEXT_PATH, false);
        if (!StringUtils.isEmpty(contentPath)) return contentPath;
        return doRead("bootstrap.properties", SpringKey.KEY_CONTEXT_PATH, false);
    }
}

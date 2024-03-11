package com.cool.request.lib.springmvc.config;

import com.cool.request.lib.springmvc.config.base.BaseYamlUserProjectConfigReader;
import com.cool.request.utils.StringUtils;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

public class YamlUserProjectContextPathReader extends BaseYamlUserProjectConfigReader<String> {

    public YamlUserProjectContextPathReader(Project project, Module module) {
        super(project, module);
    }

    @Override
    public String read() {

        String contentPath = doRead("application.yaml", SpringKey.KEY_CONTEXT_PATH, false);
        if (!StringUtils.isEmpty(contentPath)) return contentPath;
        return doRead("bootstrap.yaml", SpringKey.KEY_CONTEXT_PATH, false);
    }
}

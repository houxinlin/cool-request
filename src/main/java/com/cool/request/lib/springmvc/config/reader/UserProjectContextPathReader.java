package com.cool.request.lib.springmvc.config.reader;

import com.cool.request.lib.springmvc.config.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

public class UserProjectContextPathReader implements UserProjectReader<String> {
    private  final String DEFAULT_CONTEXT_PATH = "";
    private final Project project;
    private final Module module;

    public UserProjectContextPathReader(Project project, Module module) {
        this.project = project;
        this.module = module;
    }

    @Override
    public String read() {
        UserProjectConfigReaderBuilder<String> userProjectConfigReaderBuilder = new UserProjectConfigReaderBuilder()
                .addReader(new PropertiesUserProjectContextPathReader(project, module))
                .addReader(new YamlUserProjectContextPathReader(project, module))
                .addReader(new DefaultValueReader(DEFAULT_CONTEXT_PATH));
        return userProjectConfigReaderBuilder.read();
    }
}

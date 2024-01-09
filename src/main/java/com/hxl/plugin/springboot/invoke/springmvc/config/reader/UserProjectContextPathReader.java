package com.hxl.plugin.springboot.invoke.springmvc.config.reader;

import com.hxl.plugin.springboot.invoke.springmvc.config.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

public class UserProjectContextPathReader implements UserProjectReader<String> {
    private Project project;
    private Module module;

    public UserProjectContextPathReader(Project project, Module module) {
        this.project = project;
        this.module = module;
    }

    @Override
    public String read() {
        UserProjectConfigReaderBuilder<String> userProjectConfigReaderBuilder = new UserProjectConfigReaderBuilder()
                .addReader(new PropertiesUserProjectContextPathReader(project, module))
                .addReader(new YamlUserProjectContextPathReader(project, module))
                .addReader(new DefaultValueReader("/"));
        return userProjectConfigReaderBuilder.read();
    }
}

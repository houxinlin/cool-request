package com.hxl.plugin.springboot.invoke.springmvc.config.reader;

import com.hxl.plugin.springboot.invoke.springmvc.config.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

public class UserProjectServerPortReader implements UserProjectReader<Integer> {
    private static final int DEFAULT_PORT = 8080;
    private final Project project;
    private final Module module;

    public UserProjectServerPortReader(Project project, Module module) {
        this.project = project;
        this.module = module;
    }

    @Override
    public Integer read() {
        UserProjectConfigReaderBuilder<Integer> userProjectConfigReaderBuilder = new UserProjectConfigReaderBuilder()
                .addReader(new PropertiesUserProjectServerPortReader(project, module))
                .addReader(new YamlUserProjectServerPortReader(project, module))
                .addReader(new DefaultValueReader(DEFAULT_PORT));
        return userProjectConfigReaderBuilder.read();
    }
}

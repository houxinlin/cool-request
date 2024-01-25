package com.cool.request.component.http;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

@Service
public final class StaticResourceServerService {
    public static StaticResourceServerService getInstance(Project project) {
        return project.getService(StaticResourceServerService.class);
    }
}

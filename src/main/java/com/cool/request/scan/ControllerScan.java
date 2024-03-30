package com.cool.request.scan;

import com.cool.request.components.http.Controller;
import com.intellij.openapi.project.Project;

import java.util.List;

public interface ControllerScan {
    public List<Controller> scanController(Project project);
}

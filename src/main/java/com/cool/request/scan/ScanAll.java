package com.cool.request.scan;

import com.cool.request.components.http.Controller;
import com.cool.request.scan.spring.SpringControllerScan;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;

public class ScanAll  implements ControllerScan{
    private SpringControllerScan springControllerScan =new SpringControllerScan();
    @Override
    public List<Controller> scanController(Project project) {
        List<Controller> result =new ArrayList<>();

        result.addAll(springControllerScan.scanController(project));
        return result;
    }
}

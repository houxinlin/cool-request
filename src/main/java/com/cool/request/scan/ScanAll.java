package com.cool.request.scan;

import com.cool.request.components.http.Controller;
import com.cool.request.components.scheduled.BasicScheduled;
import com.cool.request.scan.spring.SpringControllerScan;
import com.cool.request.scan.spring.SpringScheduledScan;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;

public class ScanAll implements ControllerScan, ScheduledScan {
    private SpringControllerScan springControllerScan = new SpringControllerScan();
    private SpringScheduledScan springScheduledScan = new SpringScheduledScan();

    @Override
    public List<Controller> scanController(Project project) {
        List<Controller> result = new ArrayList<>();

        result.addAll(springControllerScan.scanController(project));
        return result;
    }

    @Override
    public List<BasicScheduled> scanScheduled(Project project) {
        return springScheduledScan.scanScheduled(project);
    }
}

package com.cool.request.scan;

public abstract class BaseControllerScan {
    private ControllerConverter controllerConverter;

    public BaseControllerScan(ControllerConverter controllerConverter) {
        this.controllerConverter = controllerConverter;
    }

    public ControllerConverter getControllerConverter() {
        return controllerConverter;
    }
}

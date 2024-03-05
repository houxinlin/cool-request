package com.cool.request.lib.springmvc;

public enum ControllerAnnotation {
    REST_CONTROLLER("RestController","org.springframework.web.bind.annotation.RestController"),
    CONTROLLER("Controller","org.springframework.stereotype.Controller"),
    JAX_RS_PATH("Path","javax.ws.rs.Path"),
    ;

    ControllerAnnotation(String name,String fullName) {
        annotationName = fullName;
        this.name=name;
    }

    private final String annotationName;
    private final String name;
    public String getAnnotationName() {
        return annotationName;
    }

    public String getName() {
        return name;
    }
}

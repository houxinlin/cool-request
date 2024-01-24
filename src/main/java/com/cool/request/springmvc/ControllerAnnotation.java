package com.cool.request.springmvc;

public enum ControllerAnnotation {
    RestController("RestController","org.springframework.web.bind.annotation.RestController"),
    Controller("Controller","org.springframework.stereotype.Controller");


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

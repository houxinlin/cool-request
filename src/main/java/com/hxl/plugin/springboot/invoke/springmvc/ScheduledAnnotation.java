package com.hxl.plugin.springboot.invoke.springmvc;

public enum ScheduledAnnotation {
    SCHEDULED_ANNOTATION("Scheduled","org.springframework.scheduling.annotation.Scheduled");
    ScheduledAnnotation(String name,String fullName) {
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

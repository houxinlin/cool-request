package com.cool.request.lib.springmvc;

public enum ScheduledAnnotation {
    SCHEDULED_ANNOTATION("Scheduled", "org.springframework.scheduling.annotation.Scheduled"),

    XXL_JOB_ANNOTATION("XxlJob", "com.xxl.job.core.handler.annotation.XxlJob");

    ScheduledAnnotation(String name, String fullName) {
        annotationName = fullName;
        this.name = name;
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

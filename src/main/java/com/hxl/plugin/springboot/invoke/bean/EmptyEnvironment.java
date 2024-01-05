package com.hxl.plugin.springboot.invoke.bean;

public class EmptyEnvironment extends RequestEnvironment {
    public EmptyEnvironment() {
        setEnvironmentName("None");
        setId("-1");
        setPrefix("");
    }
}

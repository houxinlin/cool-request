package com.cool.request.common.bean;

public class EmptyEnvironment extends RequestEnvironment {
    public EmptyEnvironment() {
        setEnvironmentName("None");
        setId("-1");
        setHostAddress("");
    }
}

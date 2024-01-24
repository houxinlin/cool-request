package com.cool.request.bean;

public class EmptyEnvironment extends RequestEnvironment {
    public EmptyEnvironment() {
        setEnvironmentName("None");
        setId("-1");
        setHostAddress("");
    }
}

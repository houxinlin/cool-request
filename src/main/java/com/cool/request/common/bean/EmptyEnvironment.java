package com.cool.request.common.bean;

import java.util.ArrayList;

public class EmptyEnvironment extends RequestEnvironment {
    public EmptyEnvironment() {
        setEnvironmentName("None");
        setId("-1");
        setHostAddress("");
    }
}

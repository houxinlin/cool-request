package com.cool.request.lib.openapi;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

public abstract class DiscriminatorMixin {

    @JsonIgnore
    public abstract Map<String, Object> getExtensions();
}

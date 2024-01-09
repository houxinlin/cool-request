package com.hxl.plugin.springboot.invoke.springmvc.config;

public class DefaultValueReader<T> implements UserProjectReader<T> {
    private T value;

    public DefaultValueReader(T value) {
        this.value = value;
    }

    @Override
    public T read() {
        return value;
    }
}

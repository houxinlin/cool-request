package com.cool.request.lib.springmvc.config;

public class DefaultValueReader<T> implements UserProjectReader<T> {
    private final T value;

    public DefaultValueReader(T value) {
        this.value = value;
    }

    @Override
    public T read() {
        return value;
    }
}

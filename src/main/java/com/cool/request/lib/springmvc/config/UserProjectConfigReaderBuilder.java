package com.cool.request.lib.springmvc.config;

import java.util.ArrayList;
import java.util.List;

public class UserProjectConfigReaderBuilder<T> implements UserProjectReader<T> {
    private final List<UserProjectReader<T>> readers = new ArrayList<>();

    public UserProjectConfigReaderBuilder addReader(UserProjectReader<T> reader) {
        readers.add(reader);
        return this;
    }

    @Override
    public T read() {
        for (UserProjectReader reader : readers) {
            try {
                Object read = reader.read();
                if (read != null) {
                    return (T) read;
                }
            } catch (Exception e) {
            }
        }
        return null;
    }
}

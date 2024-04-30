package com.cool.request.view.table;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class EmptySuggestFactory implements SuggestFactory {
    private static final EmptySuggestFactory empty = new EmptySuggestFactory();

    public static EmptySuggestFactory getInstance() {
        return empty;
    }

    @Override
    public Function<String, List<String>> createSuggestLookup() {
        return s -> Collections.emptyList();
    }

    @Override
    public Window getWindow() {
        return null;
    }

    @Override
    public List<String> getKeySuggest() {
        return Collections.emptyList();
    }

    @Override
    public List<String> getValueSuggest(String key) {
        return Collections.emptyList();
    }
}

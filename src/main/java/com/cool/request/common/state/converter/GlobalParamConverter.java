package com.cool.request.common.state.converter;

import com.cool.request.common.state.GlobalParamPersistent;
import com.google.gson.Gson;
import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GlobalParamConverter extends Converter<GlobalParamPersistent.GlobalParam> {
    @Override
    public @Nullable GlobalParamPersistent.GlobalParam  fromString(@NotNull String value) {
        return new Gson().fromJson(value, GlobalParamPersistent.GlobalParam.class);
    }

    @Override
    public @Nullable String toString(GlobalParamPersistent.@NotNull GlobalParam value) {
        return new Gson().toJson(value);
    }
}

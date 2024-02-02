package com.cool.request.common.model.pack;

import com.cool.request.common.model.Model;
import com.cool.request.utils.GsonUtils;

public abstract class CommunicationPackage {
    private final Model data;

    public CommunicationPackage(Model model) {
        model.setType(getType());
        this.data = model;
    }

    public abstract String getType();

    public String toJson() {
        if (data == null) {
            return "{}";
        }
        return toJson(data);
    }

    public String toJson(Object o) {
        return GsonUtils.toJsonString(o);
    }
}

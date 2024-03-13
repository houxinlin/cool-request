package com.cool.request.common.bean.components;


import com.intellij.openapi.project.Project;

import java.util.Objects;

public abstract class BasicComponent implements Component {
    private String id;

    public abstract void calcId(Project project);

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        BasicComponent that = (BasicComponent) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

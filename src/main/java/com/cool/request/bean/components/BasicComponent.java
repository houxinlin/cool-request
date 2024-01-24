package com.cool.request.bean.components;


public abstract class BasicComponent implements Component {
    private String id;

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}

package com.hxl.plugin.springboot.invoke.model;

public class ClearCommunicationPackage  extends CommunicationPackage{
    public ClearCommunicationPackage(Model model) {
        super(model);
    }

    @Override
    public String getType() {
        return "clear";
    }
}

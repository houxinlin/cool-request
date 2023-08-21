package com.hxl.plugin.springboot.invoke.model;

public class ScheduledCommunicationPackage  extends CommunicationPackage{
    public ScheduledCommunicationPackage(Model model) {
        super(model);
    }


    @Override
    public String getType() {
        return "scheduled";
    }
}

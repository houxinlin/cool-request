package com.hxl.plugin.springboot.invoke.model.pack;


import com.hxl.plugin.springboot.invoke.model.ProjectStartupModel;

public class ProjectStartupCommunicationPackage  extends CommunicationPackage{
    public ProjectStartupCommunicationPackage(ProjectStartupModel model) {
        super(model);
    }

    @Override
    public String getType() {
        return "startup";
    }
}

package com.cool.request.model.pack;


import com.cool.request.model.ProjectStartupModel;

public class ProjectStartupCommunicationPackage  extends CommunicationPackage{
    public ProjectStartupCommunicationPackage(ProjectStartupModel model) {
        super(model);
    }

    @Override
    public String getType() {
        return "startup";
    }
}

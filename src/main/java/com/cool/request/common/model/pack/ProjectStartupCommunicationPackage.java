package com.cool.request.common.model.pack;


import com.cool.request.common.model.ProjectStartupModel;

public class ProjectStartupCommunicationPackage  extends CommunicationPackage{
    public ProjectStartupCommunicationPackage(ProjectStartupModel model) {
        super(model);
    }

    @Override
    public String getType() {
        return "startup";
    }
}

package com.cool.request.model.pack;


import com.cool.request.model.Model;

public class ScheduledCommunicationPackage  extends CommunicationPackage{
    public ScheduledCommunicationPackage(Model model) {
        super(model);
    }


    @Override
    public String getType() {
        return "scheduled";
    }
}

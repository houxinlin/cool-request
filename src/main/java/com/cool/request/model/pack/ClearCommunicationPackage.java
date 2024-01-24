package com.cool.request.model.pack;

import com.cool.request.model.Model;

public class ClearCommunicationPackage  extends CommunicationPackage{
    public ClearCommunicationPackage(Model model) {
        super(model);
    }
    @Override
    public String getType() {
        return "clear";
    }
}

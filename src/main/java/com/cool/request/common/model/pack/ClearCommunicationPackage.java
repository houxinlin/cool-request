package com.cool.request.common.model.pack;

import com.cool.request.common.model.Model;

public class ClearCommunicationPackage  extends CommunicationPackage{
    public ClearCommunicationPackage(Model model) {
        super(model);
    }
    @Override
    public String getType() {
        return "clear";
    }
}

package com.cool.request.model.pack;

import com.cool.request.model.Model;

public class RequestMappingCommunicationPackage extends CommunicationPackage {
    public RequestMappingCommunicationPackage(Model model) {
        super(model);
    }

    @Override
    public String getType() {
        return "controller";
    }

}

package com.cool.request.common.model.pack;

import com.cool.request.common.model.Model;

public class RequestMappingCommunicationPackage extends CommunicationPackage {
    public RequestMappingCommunicationPackage(Model model) {
        super(model);
    }

    @Override
    public String getType() {
        return "controller";
    }

}

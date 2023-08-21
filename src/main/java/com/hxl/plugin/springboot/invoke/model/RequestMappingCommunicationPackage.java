package com.hxl.plugin.springboot.invoke.model;

public class RequestMappingCommunicationPackage extends CommunicationPackage {
    public RequestMappingCommunicationPackage(Model model) {
        super(model);
    }

    @Override
    public String getType() {
        return "controller";
    }

}

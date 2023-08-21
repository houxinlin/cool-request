package com.hxl.plugin.springboot.invoke.model;

public class InvokeResponseCommunicationPackage  extends CommunicationPackage{
    public InvokeResponseCommunicationPackage(Model model) {
        super(model);
    }

    @Override
    public String getType() {
        return "response_info";
    }
}

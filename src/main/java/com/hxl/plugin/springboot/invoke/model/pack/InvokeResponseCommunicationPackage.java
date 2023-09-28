package com.hxl.plugin.springboot.invoke.model.pack;

import com.hxl.plugin.springboot.invoke.model.Model;

public class InvokeResponseCommunicationPackage  extends CommunicationPackage{
    public InvokeResponseCommunicationPackage(Model model) {
        super(model);
    }

    @Override
    public String getType() {
        return "response_info";
    }
}

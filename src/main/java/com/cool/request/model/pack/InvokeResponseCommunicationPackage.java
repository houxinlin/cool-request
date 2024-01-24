package com.cool.request.model.pack;

import com.cool.request.model.Model;

public class InvokeResponseCommunicationPackage  extends CommunicationPackage{
    public InvokeResponseCommunicationPackage(Model model) {
        super(model);
    }

    @Override
    public String getType() {
        return "response_info";
    }
}

package com.cool.request.common.model.pack;


import com.cool.request.common.model.Model;

public class XXLJobPackage extends CommunicationPackage {
    public XXLJobPackage(Model model) {
        super(model);
    }

    @Override
    public String getType() {
        return "xxl_job";
    }
}

package com.hxl.plugin.springboot.invoke.model.pack;

import com.hxl.plugin.springboot.invoke.model.Model;

public class ClearCommunicationPackage  extends CommunicationPackage{
    public ClearCommunicationPackage(Model model) {
        super(model);
    }
    @Override
    public String getType() {
        return "clear";
    }
}

package com.hxl.plugin.springboot.invoke.listener;

import com.hxl.plugin.springboot.invoke.model.ProjectStartupModel;

public interface ProjectStartupListener  extends CommunicationListener{
    public  void onStartup(ProjectStartupModel model);
}

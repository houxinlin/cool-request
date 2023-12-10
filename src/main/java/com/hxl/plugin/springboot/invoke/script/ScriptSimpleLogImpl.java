package com.hxl.plugin.springboot.invoke.script;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.intellij.openapi.project.Project;

public class ScriptSimpleLogImpl  implements ILog{
    private final Project project;
    public ScriptSimpleLogImpl( Project project) {
        this.project= project;
    }

    @Override
    public void clearLog(String id) {
        project.getMessageBus().syncPublisher(IdeaTopic.SCRIPT_LOG).clear(id);
    }

    @Override
    public void log(String id, String value) {
        project.getMessageBus().syncPublisher(IdeaTopic.SCRIPT_LOG).log(id,value);
    }
}

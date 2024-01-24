package com.cool.request.script;

import com.cool.request.IdeaTopic;
import com.intellij.openapi.project.Project;

public class ScriptSimpleLogImpl implements ILog {
    private final Project project;
    private final String id;

    public ScriptSimpleLogImpl(Project project, String id) {
        this.project = project;
        this.id = id;
    }

    @Override
    public void clearLog() {
        project.getMessageBus().syncPublisher(IdeaTopic.SCRIPT_LOG).clear(id);
    }

    @Override
    public void println(String value) {
        project.getMessageBus().syncPublisher(IdeaTopic.SCRIPT_LOG).log(id, value + "\n");
    }

    @Override
    public void print(String value) {
        project.getMessageBus().syncPublisher(IdeaTopic.SCRIPT_LOG).log(id, value);
    }
}

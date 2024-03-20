package com.cool.request.components.http.script;

import com.cool.request.components.http.Controller;
import com.cool.request.script.ILog;
import com.cool.request.view.page.IScriptLog;
import com.intellij.openapi.project.Project;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ScriptSimpleLogImpl extends PrintStream implements ILog {
    private final Project project;
    private final Controller controller;

    private final IScriptLog scriptLogPage;

    public ScriptSimpleLogImpl(Project project, Controller controller, IScriptLog scriptLogPage) {
        super(new ByteArrayOutputStream());
        this.project = project;
        this.controller = controller;
        this.scriptLogPage = scriptLogPage;
    }


    @Override
    public void clearLog() {
        scriptLogPage.clear(controller);
    }

    @Override
    public void println(String value) {
        scriptLogPage.println(controller, value);
    }

    @Override
    public void print(String value) {
        scriptLogPage.print(controller, value);
    }
}

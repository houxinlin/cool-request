package com.hxl.plugin.springboot.invoke.action.actions;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.util.function.Supplier;

public abstract class BaseAnAction extends AnAction {
    private Project project;

    public BaseAnAction(Project project, Supplier<String> title, Supplier<String> description, Icon icon) {
        super(title.get(), description.get(), icon);
        this.project=project;
        ApplicationManager.getApplication().getMessageBus().connect().subscribe(IdeaTopic.LANGUAGE_CHANGE, (IdeaTopic.BaseListener) () -> {
            getTemplatePresentation().setText(title.get());
            getTemplatePresentation().setDescription(description.get());
        });
    }

    public Project getProject() {
        return project;
    }
}

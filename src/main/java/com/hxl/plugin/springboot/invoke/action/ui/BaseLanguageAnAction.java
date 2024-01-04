package com.hxl.plugin.springboot.invoke.action.ui;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.util.function.Supplier;

public abstract class BaseLanguageAnAction extends AnAction {

    public BaseLanguageAnAction(Project project, Supplier<String> title, Supplier<String> description, Icon icon) {
        super(title.get(), description.get(), icon);
        project.getMessageBus().connect().subscribe(IdeaTopic.LANGUAGE_CHANGE, () -> {
            getTemplatePresentation().setText(title.get());
            getTemplatePresentation().setDescription(description.get());

        });
    }
}

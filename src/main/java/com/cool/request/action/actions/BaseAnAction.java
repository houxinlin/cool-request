package com.cool.request.action.actions;

import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.components.CoolRequestPluginDisposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.messages.MessageBusConnection;

import javax.swing.*;
import java.util.function.Supplier;

public abstract class BaseAnAction extends AnAction {
    private final Project project;

    /**
     * Constructs a new BaseAnAction object.
     *
     * @param project     The project in which the action is being created.
     * @param title       A supplier function that provides the title of the action.
     * @param description A supplier function that provides the description of the action.
     * @param icon        The icon to be used for the action.
     */
    public BaseAnAction(Project project, Supplier<String> title, Supplier<String> description, Icon icon) {
        super(title.get(), description.get(), icon);
        this.project = project;
        MessageBusConnection connect = ApplicationManager.getApplication().getMessageBus().connect();
        if (project == null) {
            return;
        }
        connect.subscribe(CoolRequestIdeaTopic.COOL_REQUEST_SETTING_CHANGE, (CoolRequestIdeaTopic.BaseListener) () -> {
            getTemplatePresentation().setText(title.get());
            getTemplatePresentation().setDescription(description.get());
        });
        Disposer.register(CoolRequestPluginDisposable.getInstance(project), connect);
    }

    public BaseAnAction(Project project, Supplier<String> title, Icon icon) {
        this(project, title, title, icon);

    }


    public Project getProject() {
        return project;
    }
}

package com.hxl.plugin.springboot.invoke.view;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.action.ui.*;

import com.hxl.plugin.springboot.invoke.net.PluginCommunication;
import com.hxl.plugin.springboot.invoke.utils.*;
import com.hxl.plugin.springboot.invoke.view.dialog.SettingDialog;
import com.hxl.plugin.springboot.invoke.view.events.IToolBarViewEvents;
import com.hxl.plugin.springboot.invoke.view.main.MainBottomHTTPContainer;
import com.hxl.plugin.springboot.invoke.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.JBSplitter;

import java.awt.*;
import java.io.IOException;
import java.net.URI;


/**
 * Main View
 */
public class CoolIdeaPluginWindowView extends SimpleToolWindowPanel implements IToolBarViewEvents {
    private final MainTopTreeView mainTopTreeView;
    private final MainBottomHTTPContainer mainBottomHTTPContainer;
    private final UserProjectManager userProjectManager = new UserProjectManager();
    private final JBSplitter jbSplitter = new JBSplitter(true, "", 0.35f);
    public CoolIdeaPluginWindowView(Project project) {
        super(true);
        setLayout(new BorderLayout());
        project.putUserData(Constant.UserProjectManagerKey, userProjectManager);
        this.mainTopTreeView = new MainTopTreeView(project, this);
        this.mainBottomHTTPContainer = new MainBottomHTTPContainer(project, this);

        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new RefreshAction(this));
        group.add(new HelpAction(this));
        group.add(new CleanAction(this));
        group.add(new SettingAction(this));
        group.add(new FloatWindowsAnAction());
        group.add(new ChangeMainLayoutAnAction());
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("bar", group, false);
        toolbar.setTargetComponent(this);
        setToolbar(toolbar.getComponent());
        initUI();
        initSocket(project);
    }

    private void initSocket(Project project) {
        try {
            int port = SocketUtils.getSocketUtils().getPort(project);
            System.out.println(port);
            PluginCommunication pluginCommunication = new PluginCommunication(new MessageHandlers(userProjectManager));
            pluginCommunication.startServer(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initUI() {
        ApplicationManager.getApplication().getMessageBus().connect().subscribe(IdeaTopic.CHANGE_LAYOUT,
                (IdeaTopic.BaseListener) () -> {
            boolean orientation = jbSplitter.getOrientation();
            jbSplitter.setOrientation(!orientation);
        });

        jbSplitter.setFirstComponent(mainTopTreeView);
        jbSplitter.setSecondComponent(mainBottomHTTPContainer);
        this.add(jbSplitter, BorderLayout.CENTER);
    }

    @Override
    public void openSettingView() {
        SettingDialog.show();
    }

    @Override
    public void clearTree() {
        mainTopTreeView.clear();
    }

    @Override
    public void pluginHelp() {
        try {
            Desktop.getDesktop().browse(URI.create("https://www.houxinlin.com/invoke"));
        } catch (IOException e) {
        }
    }
    @Override
    public void refreshTree() {
        userProjectManager.projectEndpointRefresh();
    }

    public MainBottomHTTPContainer getMainBottomHTTPContainer() {
        return mainBottomHTTPContainer;
    }

    public MainTopTreeView getMainTopTreeView() {
        return mainTopTreeView;
    }


}

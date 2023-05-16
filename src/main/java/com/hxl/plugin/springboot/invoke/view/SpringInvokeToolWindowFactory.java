package com.hxl.plugin.springboot.invoke.view;

import com.hxl.plugin.springboot.invoke.utils.SocketUtils;
import com.hxl.plugin.springboot.invoke.net.PluginCommunication;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;



public class SpringInvokeToolWindowFactory implements ToolWindowFactory{
    public SpringInvokeToolWindowFactory() {
    }

    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        MainView mainView = new MainView(project);
        PluginCommunication pluginCommunication = new PluginCommunication(mainView);
        toolWindow.getContentManager().addContent(
                toolWindow.getContentManager().getFactory().createContent(mainView.getView(), "", true)
        );
        try {
            int port = SocketUtils.getSocketUtils().getPort(project);
            System.out.println(port);
            pluginCommunication.startServer(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
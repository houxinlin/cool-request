package com.hxl.plugin.springboot.invoke.tool;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.bean.DynamicAnActionResponse;
import com.hxl.plugin.springboot.invoke.bean.components.Component;
import com.hxl.plugin.springboot.invoke.cache.ComponentCacheManager;
import com.hxl.plugin.springboot.invoke.net.CommonOkHttpRequest;
import com.hxl.plugin.springboot.invoke.net.PluginCommunication;
import com.hxl.plugin.springboot.invoke.net.RequestContextManager;
import com.hxl.plugin.springboot.invoke.utils.*;
import com.hxl.plugin.springboot.invoke.view.component.ApiToolPage;
import com.intellij.openapi.project.Project;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CoolRequest implements Provider {
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);

    private final UserProjectManager userProjectManager;
    private final ComponentCacheManager componentCacheManager;
    private ApiToolPage apiToolPage;
    private final int pluginListenerPort;

    /**
     * 项目启动后，但是窗口没打开，然后在打开窗口，将挤压的东西推送到窗口
     */
    private List<List<Component>> backlogData = new ArrayList<>();

    public static synchronized CoolRequest initCoolRequest(Project project) {
        if (project.getUserData(Constant.CoolRequestKey) != null) return project.getUserData(Constant.CoolRequestKey);
        return new CoolRequest(project);
    }

    public void addBacklogData(List<Component> backlogData) {
        this.backlogData.add(backlogData);
    }

    private CoolRequest(Project project) {
        userProjectManager = new UserProjectManager(project, this);
        componentCacheManager = new ComponentCacheManager(project);
        project.putUserData(Constant.CoolRequestKey, this);
        ProviderManager.registerProvider(CoolRequest.class, Constant.CoolRequestKey, this, project);

        project.putUserData(Constant.UserProjectManagerKey, userProjectManager);
        project.putUserData(Constant.RequestContextManagerKey, new RequestContextManager());
        project.putUserData(Constant.ComponentCacheManagerKey, componentCacheManager);

        initSocket(project);
        // 拉取检查更新
        scheduledThreadPoolExecutor.scheduleAtFixedRate(this::pullNewAction, 0, 2, TimeUnit.HOURS);
        pluginListenerPort = SocketUtils.getSocketUtils().getPort(project);
    }

    /**
     * 拉取检查更新
     */
    private void pullNewAction() {
        CommonOkHttpRequest commonOkHttpRequest = new CommonOkHttpRequest();
        commonOkHttpRequest.getBody(Constant.URL.PULL_ACTION).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    if (response.code() == 200) {
                        String body = response.body().string();
                        DynamicAnActionResponse dynamicAnActionResponse = ObjectMappingUtils.readValue(body, DynamicAnActionResponse.class);

                        if (new Version(Constant.VERSION).compareTo(new Version(dynamicAnActionResponse.getLastVersion())) < 0) {
                            if (apiToolPage != null) {
                                SwingUtilities.invokeLater(() -> apiToolPage.showUpdateMenu());
                            }
                        }
                        if (apiToolPage == null) return;
                        apiToolPage.removeAllDynamicAnActions();
                        for (DynamicAnActionResponse.AnAction action : dynamicAnActionResponse.getActions()) {
                            ImageIcon imageIcon = new ImageIcon(ImageIO.read(new URL(action.getIconUrl())));
                            apiToolPage.addNewDynamicAnAction(action.getName(), action.getValue(), imageIcon);
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        });
    }

    public int getPluginListenerPort() {
        return pluginListenerPort;
    }

    /**
     * 初始化socket
     *
     * @param project 项目
     */
    private void initSocket(Project project) {
        try {
            // 获取项目端口号
            int port = SocketUtils.getSocketUtils().getPort(project);
            PluginCommunication pluginCommunication = new PluginCommunication(project, new MessageHandlers(userProjectManager));
            pluginCommunication.startServer(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void attachWindowView(ApiToolPage apiToolPage) {
        this.apiToolPage = apiToolPage;
        if (apiToolPage != null) {
            for (List<Component> data : this.backlogData) {
                userProjectManager.addComponent(data);
            }
            backlogData.clear();
        }
    }

    public boolean canAddComponentToView() {
        return apiToolPage != null;
    }
}

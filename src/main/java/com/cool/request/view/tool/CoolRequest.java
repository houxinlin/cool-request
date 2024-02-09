package com.cool.request.view.tool;

import com.cool.request.common.bean.DynamicAnActionResponse;
import com.cool.request.common.bean.components.Component;
import com.cool.request.common.cache.ComponentCacheManager;
import com.cool.request.common.config.Version;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.component.http.net.CommonOkHttpRequest;
import com.cool.request.component.http.net.CoolPluginSocketServer;
import com.cool.request.component.http.net.RequestContextManager;
import com.cool.request.utils.GsonUtils;
import com.cool.request.utils.SocketUtils;
import com.cool.request.view.component.ApiToolPage;
import com.cool.request.view.main.RequestEnvironmentProvide;
import com.cool.request.view.tool.provider.RequestEnvironmentProvideImpl;
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
    private Project project;

    /**
     * 项目启动后，但是窗口没打开，然后在打开窗口，将挤压的东西推送到窗口
     */
    private List<List<Component>> backlogData = new ArrayList<>();

    public static synchronized CoolRequest initCoolRequest(Project project) {
        if (project.getUserData(CoolRequestConfigConstant.CoolRequestKey) != null) return project.getUserData(CoolRequestConfigConstant.CoolRequestKey);
        return new CoolRequest(project);
    }

    public void addBacklogData(List<Component> backlogData) {
        this.backlogData.add(backlogData);
    }

    private CoolRequest(Project project) {
        this.project = project;
        userProjectManager = new UserProjectManager(project, this);
        componentCacheManager = new ComponentCacheManager(project);
        initSocket(project);
        // 拉取检查更新
        scheduledThreadPoolExecutor.scheduleAtFixedRate(this::pullNewAction, 0, 2, TimeUnit.SECONDS);
        pluginListenerPort = SocketUtils.getSocketUtils().getPort(project);
    }

    /**
     * 拉取检查更新
     */
    private void pullNewAction() {
        CommonOkHttpRequest commonOkHttpRequest = new CommonOkHttpRequest();
        commonOkHttpRequest.getBody(CoolRequestConfigConstant.URL.PULL_ACTION).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    if (response.code() == 200) {
                        String body = response.body().string();
                        DynamicAnActionResponse dynamicAnActionResponse = GsonUtils.readValue(body, DynamicAnActionResponse.class);

                        if (new Version(CoolRequestConfigConstant.VERSION).compareTo(new Version(dynamicAnActionResponse.getLastVersion())) < 0) {
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
        int port = SocketUtils.getSocketUtils().getPort(project);
        CoolPluginSocketServer.start(new MessageHandlers(userProjectManager), project, port);
//        try {
//            // 获取项目端口号
//
////            PluginCommunication pluginCommunication = new PluginCommunication(project, new MessageHandlers(userProjectManager));
////            pluginCommunication.startServer(port);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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

    public void installProviders() {
        ProviderManager.registerProvider(CoolRequest.class, CoolRequestConfigConstant.CoolRequestKey, this, project);
        ProviderManager.registerProvider(RequestEnvironmentProvide.class, CoolRequestConfigConstant.RequestEnvironmentProvideKey, new RequestEnvironmentProvideImpl(project), project);
        project.putUserData(CoolRequestConfigConstant.UserProjectManagerKey, userProjectManager);
        project.putUserData(CoolRequestConfigConstant.RequestContextManagerKey, new RequestContextManager());
        project.putUserData(CoolRequestConfigConstant.ComponentCacheManagerKey, componentCacheManager);

    }

}
